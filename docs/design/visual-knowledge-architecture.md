# Visual Knowledge Architecture — Prototype v0.2

## 1. Origin

STEP 2를 이해하는 과정에서 source를 읽기 전에 업무 흐름과 runtime boundary를 복원하는 비용이 드러났다. 이 관찰에서 System Mapper 가설과 STEP 2.5가 생겼다. 이 문서는 처음부터 있던 비전을 소급해 기록하지 않는다. v0.1 human evaluation에서 새로 발견된 연구 분기를 설명한다.

## 2. v0.1 Human Evaluation

v0.1은 `Source → Analyzer → JSON → Curation → Viewer` pipeline과 search, click, drill-down, source evidence를 기술적으로 연결했다. 그러나 역할 카드를 나열하고 관계를 별도 목록으로 보여 주었다. 사용자는 정보를 찾을 수 있었지만 구조와 분기를 공간적으로 읽을 수 없었다.

## 3. Problem

Interactive card browser와 visual knowledge map은 같지 않다. 전체를 먼저 파악하려면 관계가 카드 순서가 아니라 선, 방향, 거리와 분기로 보여야 한다. 선택한 대상을 자세히 보면서도 상위 context를 잃지 않아야 한다.

## 4. Expanded Vision

```text
Complex Information
  → Domain Analyzer
  → Structured Knowledge
  → Common Knowledge IR
  → Visual Grammar Engine
  → Interactive Knowledge Map
  → Semantic Drill-down
  → Original Evidence
```

연구 질문은 복잡한 정보를 먼저 전체 구조로 이해하고 필요한 의미 수준만 단계적으로 내려가 원본 증거를 확인할 수 있는지로 확장되었다. Vision은 넓지만 이번 구현은 Source Code domain에 한정한다.

## 5. Domain Adapter Concept

Domain analyzer는 원본에서 domain-specific node와 relationship을 추출하고 공통 renderer가 읽는 형태로 변환하는 adapter다. 현재 Java/SQL analyzer가 첫 adapter다. Domain knowledge를 renderer 안에 넣지 않고, renderer는 node, edge, hierarchy, evidence와 source reference를 소비한다.

## 6. Common Knowledge IR

현재 `system-map.json`은 stable node ID, type, label, details, source와 `from/to/type/evidence` edge를 이미 제공한다. Curation은 description과 inferred system flow를 별도 파일에 둔다. 부족한 공통 개념은 명시적 hierarchy level, domain identifier, provenance producer, runtime evidence reference와 representation hint다. v0.2는 대규모 schema rewrite 대신 view model에서 `rank`와 representation을 계산한다. 실제 두 번째 domain이 나타나기 전까지 공통 schema를 확정하지 않는다.

## 7. Visual Grammar

- **Node:** 정보 실체. shape 안에 domain-specific type과 label을 함께 표시한다.
- **Edge:** 방향 있는 관계. 모든 edge type을 label로 그리며 `CALL`에 한정하지 않는다.
- **Hierarchy:** semantic level과 layered rank로 전체에서 세부로 이동한다.
- **Evidence:** `STATIC`은 실선, `INFERRED`는 점선, `VERIFIED`는 굵은 선으로 표현하고 text label과 legend를 함께 둔다.
- **Source reference:** 가능한 node에서 repository-relative path와 line으로 original evidence를 연결한다.

## 8. Semantic Zoom

```text
SYSTEM → TYPE → METHOD → SQL / DB OBJECT → SOURCE
```

화면 배율과 semantic zoom을 구분한다. Wheel/button zoom은 같은 표현을 확대하고, node expand는 더 구체적인 view model로 바꾼다. Breadcrumb와 Parent가 상위 의미 수준을 보존한다.

## 9. Evidence / Provenance

Analyzer가 직접 찾은 관계는 `STATIC`, human curation이나 framework semantics는 `INFERRED`, 향후 runtime에서 확인한 관계만 `VERIFIED`다. v0.2는 서로 합쳐 그리되 line style, text label, detail panel에서 provenance를 잃지 않는다. 현재 `VERIFIED`를 새로 만들지 않는다.

## 10. Visual Representation Types

같은 IR은 flow map, hierarchy/mind map, call graph, sequence, argument/evidence map, timeline과 network graph로 표현될 수 있다. 표현 선택은 IR을 바꾸지 않고 별도의 view-model adapter가 맡는다.

## 11. Source Code as Domain #1

System view는 application startup, CSV input, importer branch, shipment transaction, mapper와 Oracle boundary를 layered flow로 그린다. Type view는 caller, contained method와 dependency를 배치한다. Method view는 `Caller → Selected → Callee/SQL → DB Object`를 보여 준다. Detail panel은 role, evidence, annotation, SQL과 source를 담당한다.

## 12. Future Paper Domain

Paper domain은 claim, section, table과 figure node 및 `SUPPORTS`, `CONTRADICTS`, `USES`, `DERIVED_FROM`, `COMPARES` edge를 가질 수 있다. 이 예는 visual grammar의 domain independence를 검토하기 위한 것이며 PDF parsing, 요약, paper analyzer는 구현하지 않는다.

## 13. v0.2 Scope

기존 `viewer/`는 v0.1 실험으로 보존하고 `viewer-v0.2/`를 별도로 둔다. v0.2는 dependency 없는 SVG renderer, 결정적 layered layout, 실제 arrow edge, system flow, type/method drill-down, focus + context, search, breadcrumb, parent, zoom, pan, fit과 detail panel을 구현한다. 현재 작은 graph에서는 longest-path rank와 rank 내 균등 간격이 겹침 없는 결과를 제공한다.

Dagre/ELK는 더 큰 compound graph와 crossing 최소화에 강하고, Cytoscape.js는 network interaction 생태계가 크다. D3는 낮은 수준의 SVG control을 제공한다. 현재 graph는 작고 계층적이며 offline repository라는 조건 때문에 작은 전용 SVG layout을 선택했다. graph 규모나 cycle이 커져 crossing과 배치 품질이 무너지면 ELK 또는 Dagre 도입을 다시 검토한다.

## 14. Non-goals

Paper/PDF analyzer, 다른 언어 analyzer, runtime tracing, performance instrumentation, profiler/APM, universal UML, 지식 관리 platform과 STEP 3은 범위 밖이다. v0.2가 도구의 유용성이나 범용성을 입증했다고 주장하지 않는다.

## 15. Falsification / Failure Conditions

다음 human evaluation 질문에 부정적인 답이 반복되면 현재 표현 가설을 수정한다.

1. 첫 화면만 보고 전체 구조와 시작점을 설명할 수 있는가?
2. 화살표를 따라 실행 흐름과 branch를 추적할 수 있는가?
3. CSV, orchestration, transaction, persistence와 database boundary가 구분되는가?
4. Type을 확장하면 caller, dependency, method와 주요 outgoing call이 보이는가?
5. Method에서 caller, callee, SQL과 DB object로 자연스럽게 이동하는가?
6. 전체 구조로 쉽게 돌아가고 source를 읽을 시점을 판단할 수 있는가?
7. v0.1보다 구조 이해가 쉬운가?
8. 다른 정보 domain에도 적용할 가능성이 보이는가?

Node overlap, 잘린 label, 과도한 crossing, 방향 모호성, context 상실 또는 작은 화면에서의 사용 불가도 prototype failure다. 이 판단은 구현자가 대신 확정하지 않고 사용자의 평가로 남긴다.
