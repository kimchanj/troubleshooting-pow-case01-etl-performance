# Minimal Source Mapper

CASE #01 Java source에서 신뢰할 수 있는 작은 구조적 사실을 추출해 `output/system-map.json`을 생성한다. Python 3 표준 라이브러리만 사용하며 ETL application dependency에는 영향을 주지 않는다.

## Run

Repository root에서 실행한다.

```powershell
python tools/source-mapper/analyze.py
```

Tests:

```powershell
python -m unittest discover -s tools/source-mapper -p "test_*.py"
```

## Scope

- Java package, type, method, constructor, selected annotation and source line
- source-declared constructor dependency
- declared field receiver를 통한 명시적 method call
- MyBatis annotation SQL
- schema SQL의 table/sequence와 mapper SQL의 직접 참조

## Evidence

자동 생성 edge는 source에서 직접 확인한 `STATIC`만 사용한다. Spring bean lifecycle, `ApplicationRunner` 호출, transaction proxy, commit/rollback, MyBatis proxy와 JDBC runtime frame은 생성하지 않는다. `VERIFIED`는 향후 human runtime verification을 위한 빈 배열로 남긴다.

## Limitations

이 도구는 CASE #01 전용 최소 analyzer다. 완전한 Java parser, symbol solver, Spring runtime reconstruction 또는 SQL parser가 아니다. Receiver type을 source field/parameter에서 안전하게 찾을 수 없는 호출은 연결하지 않는다. SQL object 탐지는 현재 schema에 선언된 object 이름의 직접 참조만 인정한다.

Generated JSON은 Analyzer와 향후 Viewer 사이의 contract이자 해당 시점의 Proof of Work이므로 Git에 포함한다. 순서와 ID는 deterministic하며 source location은 repository-relative path만 사용한다.

## Current Output

CASE #01 source 기준으로 56 nodes와 57 edges를 생성한다. Node는 `TYPE` 11, `METHOD` 27, `SQL` 8, `DB_OBJECT` 10개이며 edge는 `CONTAINS` 27, `CALL` 10, `DEPENDENCY` 4, `MAPS_TO` 8, `DB_ACCESS` 8개다. 모든 자동 edge는 `STATIC`이고 `verifications`는 비어 있다.

## Ground Truth Check

| Relationship | Expected | Analyzer result | Evidence | Correct? | Notes |
| --- | --- | --- | --- | --- | --- |
| Application → ApplicationRunner | Framework startup | 생성하지 않음 | Runtime/framework semantics 필요 | Yes | STATIC으로 가장하지 않음 |
| BaselineEtlRunner → ShipmentImporter | Explicit call | `CALL` 생성 | STATIC | Yes | field receiver type으로 해석 |
| ShipmentImporter → ShipmentCsvReader | Explicit call | `CALL` 생성 | STATIC | Yes | `csvReader.read()` |
| ShipmentImporter → ShipmentTransactionService | Explicit call | `CALL` 생성 | STATIC | Yes | `transactionService.importShipment()` |
| ShipmentTransactionService → EtlMapper | Explicit mapper calls | `CALL` 생성 | STATIC | Yes | declared mapper field를 통해 해석 |
| EtlMapper method → annotation SQL | Mapper boundary | `MAPS_TO` 생성 | STATIC | Yes | 8개 annotation SQL |
| Mapper SQL → Oracle object | Direct SQL reference | `DB_ACCESS` 생성 | STATIC | Yes | schema.sql object 이름과 직접 대조 |
| `@Transactional` → proxy/commit | Runtime transaction behavior | 생성하지 않음 | Human verification 필요 | Yes | `VERIFIED` 자동 생성 금지 |
