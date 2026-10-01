# Baseline ETL Implementation

## Purpose

STEP 1에서 설계한 Synthetic Warehouse / Shipment Import를 처음 실행 가능한 형태로 구현한다. 이 baseline은 기능적으로 올바르고 반복 실행 가능하며, 이후 측정의 비교 기준이 된다. 빠른 결과나 특정 병목을 만들기 위한 구현은 포함하지 않는다.

## Baseline Architecture

```text
ApplicationRunner
    → ShipmentImporter
        → ShipmentCsvReader
        → group rows by shipment_external_id
        → ShipmentTransactionService (one transaction per shipment)
            → EtlMapper
                → Oracle FREEPDB1 / ETL_LAB
```

구조는 CSV 입력, parsing과 validation, reference lookup, 금액 계산, shipment와 item 저장을 순서대로 드러낸다. Web API가 필요하지 않은 일회성 import이므로 Controller 없이 `ApplicationRunner`를 사용한다.

## Application Execution

애플리케이션은 실제 credential을 저장하지 않고 다음 환경변수를 읽는다.

- `ETL_DB_USERNAME`: application user. 현재 baseline은 `ETL_LAB` 사용
- `ETL_DB_PASSWORD`: 사용자가 로컬 환경에만 설정하는 password
- `ETL_DB_URL`: 선택 사항. 기본값은 로컬 `FREEPDB1` service
- `ETL_INPUT_FILE`: 가져올 UTF-8 CSV 경로

`ETL_INPUT_FILE`이 비어 있으면 import를 실행하지 않는다. 실행 시 Spring Boot가 CSV를 한 번 가져온 뒤 종료한다. 같은 external ID의 재입력은 중복 shipment로 거부한다.

## CSV Format

첫 행은 아래 header와 정확히 일치해야 한다.

```text
shipment_external_id,shipped_at,supplier_code,warehouse_code,product_code,quantity,unit_price
```

- encoding: UTF-8, UTF-8 BOM도 허용
- `shipped_at`: ISO-8601 local date-time, 예: `2026-01-15T09:30:00`
- `quantity`: 0보다 큰 정수
- `unit_price`: 0 이상, 최대 소수점 2자리인 decimal
- quoted CSV field와 두 개의 double quote로 표현한 escaped quote를 지원

기능 검증 fixture는 3개 shipment와 6개 item만 포함한다. STEP 3의 대량 dataset 역할을 하지 않는다.

## Oracle Application Schema

`FREEPDB1`에 전용 application user `ETL_LAB`을 사용한다. SYSDBA는 user 생성에만 사용하고 애플리케이션 연결에는 사용하지 않는다. 관리 script는 SQL*Plus `ACCEPT ... HIDE`로 password를 사용자에게 직접 받아 실제 값을 파일이나 로그에 저장하지 않는다.

`ETL_LAB`에는 `CREATE SESSION`, `CREATE TABLE`, `CREATE SEQUENCE`와 제한된 USERS tablespace quota만 부여한다. DBA role은 부여하지 않는다.

## Oracle Objects

- reference tables: `PRODUCT`, `SUPPLIER`, `WAREHOUSE`
- transaction tables: `SHIPMENT`, `SHIPMENT_ITEM`
- ID sequences: table별 sequence 5개
- primary key와 code/external ID unique constraint
- shipment에서 supplier·warehouse로 향하는 foreign key
- shipment item에서 shipment·product로 향하는 foreign key
- 수량, 단가, 금액과 active flag에 대한 check constraint
- foreign key 탐색에 자연스러운 shipment item index

## Reference Seed

correctness 확인에 필요한 synthetic reference만 넣는다.

- Product: active 3개, inactive 1개
- Supplier: active 2개, inactive 1개
- Warehouse: active 2개, inactive 1개

inactive 값은 validation test 후보를 제공한다. 실제 업무 데이터나 대량 실험 데이터는 아니다.

## Persistence Approach

MyBatis annotation mapper가 단순한 SQL을 실행한다. 각 행에 필요한 supplier, warehouse와 product를 업무 흐름에 따라 조회한다. shipment header는 external ID별 한 번 생성하고 각 CSV 행을 item으로 저장한다. ID는 Oracle sequence에서 얻는다.

generic DAO, cache, preload, bulk select와 MyBatis BATCH executor는 사용하지 않는다. 조회와 insert는 현재 요구사항을 직접 표현하는 수준으로만 둔다.

## Transaction Boundary

하나의 `shipment_external_id`에 속하는 Shipment Header와 모든 Shipment Items를 하나의 transaction으로 처리한다. 선택 이유는 성능이 아니라 STEP 1에서 정의한 관계 무결성의 최소 자연스러운 업무 경계이기 때문이다. header와 item 중 하나라도 실패하면 해당 shipment 전체가 rollback된다.

CSV 파일 전체를 묶지 않으므로 앞에서 완료된 다른 shipment는 이후 shipment 실패로 자동 rollback되지 않는다. 행 단위 transaction도 사용하지 않아 하나의 shipment가 부분 저장되는 것을 막는다.

이 경계는 STEP 2 baseline의 결정이며 최종 transaction 설계가 아니다. failure injection과 evidence가 file-level atomicity의 필요성을 보여주는지는 STEP 12 후보에서 별도로 검증한다.

## Error Handling

다음 오류는 원인과 CSV line 또는 reference code를 포함한 `EtlValidationException`으로 중단한다.

- header 또는 column 수 오류, 닫히지 않은 quoted field
- 누락된 필수값, 잘못된 날짜·수량·단가
- 존재하지 않거나 inactive인 reference
- 같은 external ID 안에서 배송 시각, supplier 또는 warehouse가 충돌하는 행
- 이미 저장된 shipment external ID

retry, dead letter queue, compensation과 부분 retry는 구현하지 않는다.

## Correctness Verification

단위 테스트는 CSV parsing, validation, line amount, inactive reference 거부, 하나의 header와 여러 item 관계, fixture의 shipment별 grouping을 확인한다. 실제 Oracle 검증은 schema·seed 적용 후 fixture를 실행하고 shipment 3개, item 6개, orphan item 0개와 계산 합계를 조회한다.

## Why This Is a Natural Baseline

입력을 읽고 업무 단위로 묶은 뒤 필요한 참조를 조회하고 header와 item을 순서대로 저장한다. 각 component는 한 가지 역할을 가지며 SQL과 control flow를 바로 따라갈 수 있다. 실제 요구사항에 없는 지연이나 호출을 넣지 않았고, 측정 전에 cache나 batch를 적용하지 않았다.

## Deliberately Not Optimized

- reference cache 또는 preload
- bulk lookup과 bulk loading
- MyBatis BATCH executor
- parallel, async 또는 multi-thread processing
- 성능 timer, SQL/DB call counter, APM과 benchmark
- index를 이용한 결과 유도나 의도적인 index 방해

## Decisions Deferred

- dataset 크기, 값 분포와 재현 가능한 생성 규칙은 STEP 3에서 결정한다.
- baseline 측정 조건은 STEP 4에서 결정한다.
- 계측은 STEP 5에서 실제 관찰 필요에 맞춰 결정한다.
- file-level atomicity가 필요한지는 이후 failure injection과 evidence로 검증한다.
- cache와 batch 실험은 STEP 4~8 evidence가 지지할 때만 후보로 진행한다.
