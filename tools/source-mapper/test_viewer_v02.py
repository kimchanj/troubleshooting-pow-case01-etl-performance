import json
import unittest
from pathlib import Path

ROOT = Path(__file__).parent


class ViewerV02ContractTest(unittest.TestCase):
    def test_v01_is_preserved_and_v02_assets_exist(self):
        self.assertTrue((ROOT / "viewer" / "index.html").is_file())
        for name in ("index.html", "styles.css", "app.js"):
            self.assertTrue((ROOT / "viewer-v0.2" / name).is_file())

    def test_svg_graph_and_navigation_controls_are_present(self):
        html = (ROOT / "viewer-v0.2" / "index.html").read_text(encoding="utf-8")
        for marker in ('id="graph"', 'id="edges"', 'id="nodes"', 'id="fit"', 'id="breadcrumb"'):
            self.assertIn(marker, html)

    def test_curated_system_edges_reference_known_nodes(self):
        generated = json.loads((ROOT / "output" / "system-map.json").read_text(encoding="utf-8"))
        curated = json.loads((ROOT / "curation" / "system-map-curation.json").read_text(encoding="utf-8"))
        ids = {node["id"] for node in generated["nodes"]} | {node["id"] for node in curated["semanticNodes"]}
        for edge in curated["systemView"]["edges"]:
            self.assertIn(edge["from"], ids)
            self.assertIn(edge["to"], ids)
            self.assertIn(edge["evidence"], {"STATIC", "INFERRED", "VERIFIED"})


if __name__ == "__main__":
    unittest.main()
