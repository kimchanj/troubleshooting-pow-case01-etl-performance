import json
import tempfile
import unittest
from pathlib import Path

from analyze import analyze


ROOT = Path(__file__).resolve().parents[2]


class AnalyzerTests(unittest.TestCase):
    def setUp(self):
        self.result = analyze(ROOT)
        self.nodes = {node["id"]: node for node in self.result["nodes"]}
        self.edges = self.result["edges"]

    def test_known_structure(self):
        labels = {node["label"] for node in self.result["nodes"]}
        for expected in {"EtlPerformanceApplication", "BaselineEtlRunner", "ShipmentImporter",
                         "ShipmentTransactionService", "EtlMapper", "Select findProduct", "Insert insertShipment"}:
            self.assertIn(expected, labels)

    def test_known_static_relationships(self):
        edge_pairs = {(edge["type"], edge["from"], edge["to"]) for edge in self.edges}
        self.assertIn(("DEPENDENCY", "type:io.github.kimchanj.etlperformance.etl.ShipmentImporter",
                       "type:io.github.kimchanj.etlperformance.etl.ShipmentCsvReader"), edge_pairs)
        self.assertIn(("CALL", "method:io.github.kimchanj.etlperformance.etl.ShipmentImporter#importFile(Path)",
                       "method:io.github.kimchanj.etlperformance.etl.ShipmentTransactionService#importShipment(List)"), edge_pairs)

    def test_no_runtime_truth_is_fabricated(self):
        self.assertEqual([], self.result["verifications"])
        self.assertTrue(all(edge["evidence"]["level"] == "STATIC" for edge in self.edges))

    def test_source_location(self):
        node = self.nodes["method:io.github.kimchanj.etlperformance.etl.ShipmentTransactionService#importShipment(List)"]
        self.assertEqual("app/src/main/java/io/github/kimchanj/etlperformance/etl/ShipmentTransactionService.java", node["source"]["path"])
        self.assertEqual(21, node["source"]["line"])

    def test_deterministic_json(self):
        first = json.dumps(analyze(ROOT), sort_keys=True, ensure_ascii=False)
        second = json.dumps(analyze(ROOT), sort_keys=True, ensure_ascii=False)
        self.assertEqual(first, second)


if __name__ == "__main__":
    unittest.main()
