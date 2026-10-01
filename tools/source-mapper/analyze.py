#!/usr/bin/env python3
"""CASE #01 minimal Java source analyzer. Uses only the Python standard library."""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import dataclass, field
from pathlib import Path


TYPE_RE = re.compile(r"\b(public\s+)?(class|interface|record)\s+(\w+)(?:\s+implements\s+([\w., <>]+))?")
METHOD_RE = re.compile(
    r"^\s*(?:(public|protected|private)\s+)?(?:static\s+)?(?:([\w<>?,.\[\]]+)\s+)?(\w+)\s*\(([^)]*)\)\s*(?:\{|;)$"
)
FIELD_RE = re.compile(r"^\s*private\s+(?:static\s+)?(?:final\s+)?([\w<>?,.\[\]]+)\s+(\w+)\s*(?:[;=])")
CALL_RE = re.compile(r"\b(\w+)\.(\w+)\s*\(")
ANNOTATION_RE = re.compile(r"^\s*@([\w.]+)")
STRING_RE = re.compile(r'"((?:\\.|[^"\\])*)"')


def stable_node_id(kind: str, *parts: str) -> str:
    return kind.lower() + ":" + "#".join(parts)


def simple_type(value: str) -> str:
    value = re.sub(r"<.*>", "", value).replace("[]", "").strip()
    return value.split(".")[-1]


def parse_parameters(text: str) -> list[tuple[str, str]]:
    result = []
    for part in [item.strip() for item in text.split(",") if item.strip()]:
        tokens = [token for token in part.split() if not token.startswith("@") and token != "final"]
        if len(tokens) >= 2:
            result.append((simple_type(tokens[-2]), tokens[-1]))
    return result


def annotation_block(lines: list[str], start: int) -> tuple[str, int]:
    text = lines[start].strip()
    balance = text.count("(") - text.count(")")
    end = start
    while balance > 0 and end + 1 < len(lines):
        end += 1
        text += " " + lines[end].strip()
        balance += lines[end].count("(") - lines[end].count(")")
    return text, end


@dataclass
class MethodFact:
    owner: str
    name: str
    line: int
    return_type: str | None
    parameters: list[tuple[str, str]]
    annotations: list[dict]
    body: list[tuple[int, str]] = field(default_factory=list)

    @property
    def id(self) -> str:
        signature = ",".join(kind for kind, _ in self.parameters)
        return stable_node_id("method", self.owner, f"{self.name}({signature})")


@dataclass
class TypeFact:
    package: str
    name: str
    kind: str
    path: str
    line: int
    annotations: list[dict]
    implemented: list[str]
    fields: dict[str, str]
    methods: list[MethodFact]

    @property
    def qualified_name(self) -> str:
        return f"{self.package}.{self.name}"

    @property
    def id(self) -> str:
        return stable_node_id("type", self.qualified_name)


def parse_java(path: Path, root: Path) -> TypeFact | None:
    lines = path.read_text(encoding="utf-8").splitlines()
    package_match = next((re.match(r"\s*package\s+([\w.]+);", line) for line in lines if "package " in line), None)
    package = package_match.group(1) if package_match else ""
    pending: list[dict] = []
    type_fact = None
    current_method = None
    method_depth = 0
    depth = 0
    index = 0
    while index < len(lines):
        line = lines[index]
        annotation = ANNOTATION_RE.match(line)
        if annotation:
            block, end = annotation_block(lines, index)
            pending.append({"name": annotation.group(1).split(".")[-1], "text": block, "line": index + 1})
            index = end
            line = lines[index]
        if type_fact is None:
            match = TYPE_RE.search(line)
            if match:
                implemented = [item.strip() for item in (match.group(4) or "").split(",") if item.strip()]
                type_fact = TypeFact(package, match.group(3), match.group(2), path.relative_to(root).as_posix(),
                                     index + 1, pending, implemented, {}, [])
                pending = []
        else:
            field_match = FIELD_RE.match(line)
            if current_method is None and field_match:
                type_fact.fields[field_match.group(2)] = simple_type(field_match.group(1))
            method_line = re.sub(r"@\w+\([^)]*\)\s*", "", line)
            method_match = METHOD_RE.match(method_line)
            if current_method is None and method_match:
                name = method_match.group(3)
                return_type = method_match.group(2)
                if name == type_fact.name and return_type is None:
                    return_type = None
                current_method = MethodFact(type_fact.qualified_name, name, index + 1,
                                            simple_type(return_type) if return_type else None,
                                            parse_parameters(method_match.group(4)), pending)
                type_fact.methods.append(current_method)
                pending = []
                method_depth = depth + line.count("{") - line.count("}")
                if line.strip().endswith(";"):
                    current_method = None
            elif current_method is None and line.strip() and not line.strip().startswith("//"):
                if not annotation:
                    pending = []
            if current_method is not None:
                current_method.body.append((index + 1, line))
        depth += line.count("{") - line.count("}")
        if current_method is not None and depth < method_depth:
            current_method = None
        index += 1
    return type_fact


def sql_from_annotation(annotation: dict) -> str | None:
    if annotation["name"] not in {"Select", "Insert", "Update", "Delete", "SelectKey"}:
        return None
    values = [bytes(value, "utf-8").decode("unicode_escape") for value in STRING_RE.findall(annotation["text"])]
    if annotation["name"] == "SelectKey" and values:
        values = values[:1]
    sql = " ".join(values)
    return re.sub(r"\s+", " ", sql).strip() or None


def analyze(root: Path) -> dict:
    java_root = root / "app" / "src" / "main" / "java"
    types = [fact for path in sorted(java_root.rglob("*.java")) if (fact := parse_java(path, root))]
    by_simple = {fact.name: fact for fact in types}
    methods = {(fact.name, method.name): method for fact in types for method in fact.methods}
    nodes, edges = [], []

    for fact in types:
        nodes.append({"id": fact.id, "type": "TYPE", "label": fact.name,
                      "source": {"path": fact.path, "line": fact.line},
                      "details": {"package": fact.package, "javaKind": fact.kind,
                                  "annotations": [a["name"] for a in fact.annotations],
                                  "implementedInterfaces": fact.implemented,
                                  "componentCandidate": any(a["name"] in {"Component", "Service", "SpringBootApplication", "Mapper"} for a in fact.annotations)}})
        for method in fact.methods:
            nodes.append({"id": method.id, "type": "METHOD", "label": f"{method.name}()",
                          "source": {"path": fact.path, "line": method.line},
                          "details": {"owner": fact.qualified_name, "returnType": method.return_type,
                                      "parameters": [{"type": t, "name": n} for t, n in method.parameters],
                                      "annotations": [a["name"] for a in method.annotations],
                                      "constructor": method.name == fact.name}})
            edges.append(edge("CONTAINS", fact.id, method.id, fact.path, method.line, "Type declares method"))
            if method.name == fact.name:
                for dep_type, _ in method.parameters:
                    if dep_type in by_simple:
                        edges.append(edge("DEPENDENCY", fact.id, by_simple[dep_type].id, fact.path, method.line,
                                          "Source-declared constructor dependency"))
            for annotation in method.annotations:
                sql = sql_from_annotation(annotation)
                if sql:
                    sql_id = stable_node_id("sql", fact.qualified_name, method.name, annotation["name"])
                    nodes.append({"id": sql_id, "type": "SQL", "label": f"{annotation['name']} {method.name}",
                                  "source": {"path": fact.path, "line": annotation["line"]},
                                  "details": {"annotation": annotation["name"], "statement": sql}})
                    edges.append(edge("MAPS_TO", method.id, sql_id, fact.path, annotation["line"], "Mapper annotation declares SQL"))

    db_objects = extract_db_objects(root)
    for name, item in db_objects.items():
        nodes.append({"id": stable_node_id("db_object", name), "type": "DB_OBJECT", "label": name,
                      "source": {"path": item["path"], "line": item["line"]}, "details": {"objectKind": item["kind"]}})

    sql_nodes = [node for node in nodes if node["type"] == "SQL"]
    for node in sql_nodes:
        statement = node["details"]["statement"].upper()
        for name in sorted(db_objects):
            if re.search(rf"\b{re.escape(name)}\b", statement):
                edges.append(edge("DB_ACCESS", node["id"], stable_node_id("db_object", name),
                                  node["source"]["path"], node["source"]["line"], "SQL directly references database object"))

    for fact in types:
        fields = fact.fields
        for method in fact.methods:
            variables = {name: kind for kind, name in method.parameters} | fields
            for line_number, line in method.body:
                for receiver, called in CALL_RE.findall(line):
                    target_type = variables.get(receiver)
                    target = methods.get((target_type, called)) if target_type else None
                    if target:
                        edges.append(edge("CALL", method.id, target.id, fact.path, line_number,
                                          f"Explicit call through receiver '{receiver}' declared as {target_type}"))

    nodes = sorted({node["id"]: node for node in nodes}.values(), key=lambda item: item["id"])
    edges = sorted({item["id"]: item for item in edges}.values(), key=lambda item: item["id"])
    return {"metadata": {"schemaVersion": "0.1", "project": "case-01-etl-performance",
                         "generator": "tools/source-mapper/analyze.py", "scope": "app/src/main/java and app/db/schema.sql"},
            "nodes": nodes, "edges": edges, "verifications": []}


def edge(kind: str, source: str, target: str, path: str, line: int, basis: str) -> dict:
    return {"id": stable_node_id("edge", kind, source, target, str(line)), "type": kind,
            "from": source, "to": target,
            "evidence": {"level": "STATIC", "basis": basis, "source": {"path": path, "line": line}}}


def extract_db_objects(root: Path) -> dict:
    path = root / "app" / "db" / "schema.sql"
    result = {}
    for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        match = re.match(r"CREATE\s+(TABLE|SEQUENCE)\s+(\w+)", line, re.IGNORECASE)
        if match:
            result[match.group(2).upper()] = {"kind": match.group(1).upper(), "path": path.relative_to(root).as_posix(), "line": number}
    return result


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    root = args.root.resolve()
    output = args.output or Path(__file__).resolve().parent / "output" / "system-map.json"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(analyze(root), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Generated {output.relative_to(root) if output.is_relative_to(root) else output}")


if __name__ == "__main__":
    main()
