# System Mapper v0.1 — Requirements and Design

## 1. Problem / Actual Friction

STEP 2의 작은 Spring Boot ETL도 처음 보는 사람이 entry point, 주요 call flow, data transformation, Spring의 암시적 호출, transaction과 MyBatis/Oracle boundary를 함께 복원하려면 여러 source와 설정을 오가야 했다. 계측 지점을 고르기 전에 문제와 관련된 runtime path를 이해할 수 있는 지도가 필요하다는 friction이 실제 code walkthrough에서 나타났다.

Working name은 **System Mapper**다. 제품명이나 범용 도구를 뜻하지 않는다.

## 2. Research Question

> Can an interactive source map reduce the cognitive cost of understanding an unfamiliar system enough to improve troubleshooting preparation?

처음 보는 프로젝트에서 Interactive Source Map을 사용하면 소스를 개별적으로 탐색하는 것보다 전체 구조와 주요 실행 경로를 더 빠르고 쉽게 이해할 수 있는가?

## 3. Purpose

CASE #01의 business flow와 source/runtime boundary를 top-down으로 탐색할 수 있는 candidate map을 정의한다. 목표는 전체 codebase를 완전히 모델링하는 것이 아니라 의미 있는 observation point를 선택할 만큼의 지도를 제공하는 것이다.

## 4. User

첫 사용자는 개발 경험은 있지만 Spring Boot가 익숙하지 않고, 처음 보는 CASE #01 repository에서 성능 진단을 준비하는 사람이다.

## 5. Actual CASE #01 Structure

- **Entry:** `EtlPerformanceApplication.main()` → `SpringApplication.run()`
- **Startup:** `BaselineEtlRunner` (`@Component`, `ApplicationRunner`, `@ConditionalOnProperty`)
- **Orchestration:** `ShipmentImporter.importFile()`
- **Input:** UTF-8 CSV → `ShipmentCsvReader` → `CsvShipmentRow`
- **Transaction:** `ShipmentTransactionService.importShipment()` (`@Service`, `@Transactional`)
- **Persistence:** `EtlMapper` (`@Mapper`, annotation SQL), `ReferenceData`, `ShipmentRecord`, `ShipmentItemRecord`
- **Configuration:** environment-backed datasource and input path in `application.properties`
- **Database:** Oracle `PRODUCT`, `SUPPLIER`, `WAREHOUSE`, `SHIPMENT`, `SHIPMENT_ITEM` and five sequences
- **Tests:** application class availability, CSV parsing/validation, shipment grouping, transaction service behavior

The primary flow is:

```text
CSV → BaselineEtlRunner → ShipmentImporter → ShipmentCsvReader
    → ShipmentTransactionService → EtlMapper → JDBC → Oracle
```

## 6. v0.1 Scope

v0.1 design covers one Java/Maven application and its checked-in source. It represents packages, important types and methods, constructor dependencies, explicit calls, Spring/MyBatis annotations, mapper SQL, source locations, database objects, data flow and evidence level. Analyzer and Viewer remain separate future components.

## 7. Top-down Levels

### Level 0 — System

```text
baseline-shipments.csv [INPUT]
        ↓ DATA_FLOW
ETL Performance Application [SYSTEM]
        ↓ DB_ACCESS
Oracle FREEPDB1 / ETL_LAB [DATABASE]
```

### Level 1 — Component / Class

Show `EtlPerformanceApplication`, `BaselineEtlRunner`, `ShipmentImporter`, `ShipmentCsvReader`, `CsvShipmentRow`, `ShipmentTransactionService`, `EtlMapper`, persistence records and Oracle. Default view highlights the primary execution path; supporting records are collapsed.

### Level 2 — Method

For a selected class, show only relevant methods. For `ShipmentTransactionService`: `importShipment()`, `validateConsistentHeader()` and `requireActive()`, plus mapper calls. For `ShipmentCsvReader`: `read()`, `parseColumns()` and `toRow()`.

### Level 3 — Source / SQL

Show repository-relative file, line, annotations, callers/callees and mapper SQL. For `EtlMapper.insertShipment()`, show `@SelectKey`, `@Insert`, parameter object and the Oracle boundary.

## 8. Information Model

The map contains metadata, nodes, edges and optional verification records. A node is a discoverable system element. An edge states one relationship and its evidence. Descriptions explain the role to a reader unfamiliar with Spring. Source locations remain repository-relative so the JSON is portable.

## 9. Node Model

| Type | Meaning | CASE #01 example |
| --- | --- | --- |
| `SYSTEM` | Top-level application | ETL Performance Application |
| `INPUT` | External input artifact | baseline CSV |
| `COMPONENT` | DI-managed runtime role | ShipmentImporter |
| `TYPE` | Java class/interface/record | CsvShipmentRow, EtlMapper |
| `METHOD` | Relevant executable unit | importShipment() |
| `DATABASE` | External DB boundary | Oracle FREEPDB1 |
| `DB_OBJECT` | Table or sequence | SHIPMENT, shipment_seq |
| `SQL` | Mapper query/update | insertShipment SQL |

`CLASS` is folded into `TYPE`; Java kind is stored as `class`, `interface` or `record`. Package and annotation are attributes rather than node types. This avoids duplicate visual nodes.

## 10. Edge Model

| Type | Meaning |
| --- | --- |
| `CONTAINS` | System/package/type contains another element |
| `CALL` | Source method explicitly calls another method |
| `DEPENDENCY` | Constructor or field requires another component/type |
| `DATA_FLOW` | Data changes or moves between stages |
| `DB_ACCESS` | Method/SQL crosses into the database boundary |
| `MAPS_TO` | Mapper method maps to SQL or record/table |

An edge must have one source, one target, evidence and optional explanation. Visual flow direction does not claim runtime order unless the edge description says so.

## 11. Evidence Model

- `STATIC`: directly present in checked-in source, annotation, configuration or SQL.
- `INFERRED`: derived from Spring/MyBatis semantics or analyzer interpretation; must explain the basis.
- `VERIFIED`: a human confirmed the relation with debugger/runtime evidence and recorded how.

Evidence is attached to each edge, not globally to the whole map. `@Transactional` statically exists, while “Spring proxy begins a transaction before this call” is initially `INFERRED`. v0.1 does not create `VERIFIED` automatically.

## 12. UI / Interaction Model

The future HTML Viewer has three stable regions:

1. **Breadcrumb / Current Location:** `System > ShipmentTransactionService > importShipment()`.
2. **Main Visual Map:** nodes and edges at the selected level; primary flow emphasized, supporting relationships collapsible.
3. **Detail Panel:** role, type, source location, class/method, callers, callees, dependency, annotations, transaction note, mapper SQL, evidence and explanation.

Clicking a node drills down one level. Clicking a breadcrumb moves up without losing the selected primary path. Evidence filters allow `STATIC`, `INFERRED` and `VERIFIED` to be distinguished. The UI must not present inferred framework behavior with the same certainty as source facts.

## 13. Interaction / Drill-down Example

```text
System
  → click Application
Components
  → click ShipmentImporter
Methods
  → click importFile()
Source / SQL
  → inspect source location, callees and evidence
  → select importShipment()
  → inspect @Transactional inference and mapper boundaries
```

The workflow is `Map → Node → Source File/Line → Debugger → Runtime Verification → Map`.

## 14. Intermediate JSON Model

Minimal `system-map.json` draft:

```json
{
  "metadata": {
    "schemaVersion": "0.1",
    "project": "case-01-etl-performance",
    "generatedFrom": "repository source",
    "generatedAt": null
  },
  "nodes": [
    {
      "id": "method:ShipmentTransactionService#importShipment",
      "type": "METHOD",
      "label": "importShipment()",
      "role": "Persists one shipment and its items in one transaction",
      "owner": "type:ShipmentTransactionService",
      "source": {
        "path": "app/src/main/java/io/github/kimchanj/etlperformance/etl/ShipmentTransactionService.java",
        "line": 21
      },
      "annotations": ["Transactional"],
      "details": {
        "transactionBoundary": "shipment",
        "sqlBoundary": false
      }
    },
    {
      "id": "sql:EtlMapper#insertShipment",
      "type": "SQL",
      "label": "INSERT SHIPMENT",
      "source": {
        "path": "app/src/main/java/io/github/kimchanj/etlperformance/persistence/EtlMapper.java",
        "line": 23
      },
      "details": {
        "statementKind": "INSERT",
        "databaseObject": "db:SHIPMENT"
      }
    }
  ],
  "edges": [
    {
      "id": "edge:importShipment-insertShipment",
      "type": "CALL",
      "from": "method:ShipmentTransactionService#importShipment",
      "to": "method:EtlMapper#insertShipment",
      "evidence": {
        "level": "STATIC",
        "basis": "Direct method invocation at source line 31"
      }
    }
  ],
  "verifications": []
}
```

The draft is an information contract, not a committed JSON artifact or final schema.

## 15. Analyzer Feasibility

| Extraction | Feasibility | Notes |
| --- | --- | --- |
| package, type kind/name, method signature | `EASY / reliable` | Java syntax tree |
| annotation and source line | `EASY / reliable` | Direct syntax |
| constructor dependency | `EASY / reliable` | Constructor parameters; DI meaning may be inferred |
| mapper method and annotation SQL | `EASY / reliable` | Current mapper uses annotations |
| explicit method call | `POSSIBLE / limitations` | Overloads, interfaces and dynamic dispatch require resolution |
| record/class data shape | `POSSIBLE / limitations` | Fields/components are easy; semantic role needs description |
| configuration placeholder relation | `POSSIBLE / limitations` | Property parsing is easy; runtime override is external |
| Spring component discovery | `POSSIBLE / limitations` | Annotation and package scan semantics imply registration |
| ApplicationRunner runtime invocation | `POSSIBLE / limitations` | Framework behavior is inferred, not an explicit call |
| `@Transactional` proxy and commit/rollback flow | `HARD / defer` | Boundary can be inferred; actual proxy/runtime outcome needs verification |
| MyBatis mapper proxy call target | `POSSIBLE / limitations` | Annotation SQL known; proxy/JDBC frames are runtime-generated |
| Complete runtime call order | `HARD / defer` | Conditions, exceptions, proxies and data affect execution |
| Dynamic SQL, reflection, external libraries | `HARD / defer` | Not required by current CASE #01 |

## 16. Spring / MyBatis Analysis Limits

Static source proves annotations, constructor parameters, mapper method declarations and direct calls. It does not itself prove that a particular conditional bean was created, which proxy class ran, whether a transaction committed, or which runtime configuration won. Spring DI, `ApplicationRunner`, `@ConditionalOnProperty` and `@Transactional` edges therefore begin as `INFERRED`. MyBatis annotation SQL is `STATIC`; execution through its mapper proxy and JDBC is inferred until runtime verification.

## 17. Source Navigation

Portable v0.1 should display repository-relative `path:line` and provide a copy action. Browser links such as `vscode://file/...` depend on local absolute paths and browser security policy, so they should be optional and generated locally rather than stored in JSON. A future local viewer may offer an IDE link when workspace root is configured; stable fallback remains path plus line number.

## 18. Non-goals

- Analyzer or HTML Viewer implementation in STEP 2.5-A~C
- Multi-language analysis, complete Java/Spring reconstruction or universal UML
- JVM tracing, APM, profiler, performance measurement or execution-plan analysis
- AI API integration, automatic transaction tracing or automatic `VERIFIED` evidence
- IDE/VS Code extension and production-scale optimization
- ETL source refactoring or new runtime dependency

## 19. Future Performance Overlay

The node/edge IDs allow later evidence such as elapsed time, call count, DB call count, SQL hotspot and transaction result to reference the same map. No metric fields or instrumentation are implemented now. Whether this is useful is deferred until STEP 5 or later evidence supports it.

## 20. Human Verification Plan

STEP 2.5-G may select a small primary path: `main → ApplicationRunner → importFile → importShipment → mapper SQL`. A human uses breakpoints, Call Stack and Variables to check runtime order, bean/proxy boundaries, shipment grouping and the database handoff. Each confirmed edge receives a verification record containing edge ID, method, observation and date; contradicted edges are revised rather than promoted.

## 21. Utility Evaluation Plan

Use the map on CASE #01 and compare task completion with direct source browsing. Record whether the user can find the entry point, explain component roles, follow the primary call/data path, locate transaction and DB boundaries, jump to source and choose debugger breakpoints. Also record wrong interpretations, time spent and whether visual density increases confusion. No success claim is made before this evaluation.

## 22. Open Questions

- Which small set of nodes is sufficient at Level 1 without hiding an important boundary?
- Should `COMPONENT` and `TYPE` be one visual node with multiple attributes?
- How should alternative/error paths appear without overwhelming the primary flow?
- What exact human evidence is enough to promote an edge to `VERIFIED`?
- Should descriptions be checked in, generated, or maintained separately from analyzer output?
- Can source navigation remain useful when the viewer is opened outside VS Code?
- What comparison method can show reduced cognitive cost without turning this step into a performance benchmark?
