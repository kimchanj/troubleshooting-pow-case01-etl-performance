import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOL = ROOT / "tools" / "source-mapper"


class ViewerContractTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.map = json.loads((TOOL / "output" / "system-map.json").read_text(encoding="utf-8"))
        cls.curation = json.loads((TOOL / "curation" / "system-map-curation.json").read_text(encoding="utf-8"))
        cls.generated_ids = {node["id"] for node in cls.map["nodes"]}
        cls.semantic_ids = {node["id"] for node in cls.curation["semanticNodes"]}

    def test_system_view_is_progressive(self):
        ids = self.curation["systemView"]["nodeIds"]
        self.assertLess(len(ids), len(self.map["nodes"]))
        self.assertEqual(8, len(ids))
        self.assertTrue(set(ids) <= self.generated_ids | self.semantic_ids)

    def test_curated_evidence_is_explicit(self):
        levels = {edge["evidence"] for edge in self.curation["systemView"]["edges"]}
        self.assertTrue(levels <= {"STATIC", "INFERRED"})
        self.assertNotIn("VERIFIED", levels)

    def test_mapper_sql_and_db_drilldown_contract(self):
        sql_nodes = [node for node in self.map["nodes"] if node["type"] == "SQL"]
        self.assertTrue(sql_nodes)
        self.assertTrue(any(edge["type"] == "MAPS_TO" for edge in self.map["edges"]))
        self.assertTrue(any(edge["type"] == "DB_ACCESS" for edge in self.map["edges"]))

    def test_viewer_contains_required_controls(self):
        html = (TOOL / "viewer" / "index.html").read_text(encoding="utf-8")
        script = (TOOL / "viewer" / "app.js").read_text(encoding="utf-8")
        for value in ['id="breadcrumb"', 'id="map"', 'id="detail"', 'id="search"']:
            self.assertIn(value, html)
        for value in ["renderSystem", "renderNeighborhood", "renderDetail", "setupSearch"]:
            self.assertIn(value, script)


if __name__ == "__main__":
    unittest.main()
