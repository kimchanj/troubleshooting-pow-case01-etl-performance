# Research Journal

질문, 관찰, 가설, 실험과 판단의 변화를 가능한 한 시간순으로 기록한다. 이후 알게 된 사실은 새 Entry에 남긴다. 회고를 적을 때에는 실제 당시 기록과 구분한다.

## 2026-09-30 — STEP -1: Origin

### 지금 시작하는 이유

실제 엔터프라이즈 ETL/Batch 시스템에서 성능 문제를 해결하는 경험을 했다. 처음에는 해결방법을 몰랐고, 관찰과 측정, 원인 분해, 가설과 실험을 반복하며 병목의 원인을 찾아갔다. 이 경험을 계기로 답을 이미 아는 것 외에, 모르는 문제에서 증거를 모아 답을 찾아가는 능력에 관심이 생겼다.

이 Entry는 그 경험을 바탕으로 지금 프로젝트를 시작하는 이유를 적은 기록이다. 과거 문제 해결 당시의 실시간 기록은 아니다. 과거 시스템의 세부 조건이나 측정 수치는 이 저장소에 제공되지 않았고, 이번 Case에서 새 실험도 아직 하지 않았다.

### 결과와 함께 남기고 싶은 것

완성된 결과만 남기면 무엇을 몰랐고 왜 특정 가설을 세웠으며 어떤 증거로 생각을 바꾸었는지 보여주기 어렵다고 생각한다. 연구자나 예술가의 작업노트처럼 질문, 실패와 수정까지 남기고 싶다.

Proof of Work라는 개념에 관심을 가지게 된 이유도 여기에 있다. 코드뿐 아니라 문제를 좁히는 과정과 검증의 근거를 함께 남겨, 다른 사람이 그 판단을 살펴볼 수 있는 기록을 만들려 한다. 이런 기록이 얼마나 유용한지는 아직 확인해야 한다.

### 현재의 커리어 가설

원인을 알 수 없는 시스템에서 증거를 수집하고 문제를 좁혀가며 답을 찾는 능력이 내 경쟁력 중 하나일 수 있다. Performance Troubleshooter라는 방향은 이 생각에서 출발한 가설이다. 지금 결론 내리지 않고, Case #01과 이후 Case들을 통해 검증하고 수정하려 한다.

### 현재의 작업 방식에 대한 판단

작업증명은 Case-by-Case로 축적하고 각 Case를 독립적인 GitHub 저장소로 관리하려 한다. 플랫폼과 문제가 달라지면 진단도구도 달라질 수 있다. 현재는 범용 프레임워크를 먼저 만들지 않고, 실제 필요에 따라 각 Case 안에서 도구를 설계하는 편이 적절하다고 판단한다. 여러 Case에서 반복되는 패턴이 관찰되면 공통화 여부를 다시 검토한다.

AI 사용도 기록의 일부로 남긴다. 이번 단계에서는 내가 제공한 작업지시서를 바탕으로 AI(Codex)가 문서 초안을 작성하고 저장소 초기화 작업을 수행한다. 향후 AI의 제안과 내가 내린 판단, 실제 검증 결과를 구분해 남기려 한다.

### 아직 모르는 것

- 기밀을 노출하지 않으면서 어떤 문제 범위와 기술 구조를 공개적으로 다룰 수 있을까?
- 무엇을 측정해야 병목에 대한 가설을 구분할 수 있을까?
- 어떤 조건과 증거를 남겨야 다른 사람이 결과를 검토할 수 있을까?
- 과정을 어느 정도 세밀하게 기록해야 판단의 변화가 드러날까?
- AI의 도움과 인간의 판단은 실제 작업에서 어떻게 맞물릴까?
- 이 Case가 커리어 가설을 얼마나 뒷받침하거나 수정하게 만들까?

### Case #01에서 확인하고 싶은 것

공개 가능한 ETL 성능 문제에서 관찰과 측정을 통해 원인을 좁히는 과정을 설명할 수 있는지 확인하고 싶다. 실패한 가설을 수정한 이유와 결과를 검증한 근거까지 남길 수 있는지도 보고 싶다. 구체적인 실험, 성공 기준, 플랫폼과 도구는 아직 정하지 않았다.

이번 STEP -1에서는 Origin 문서와 이 첫 기록을 남긴다. 소스 코드 구현, 환경 구축, 성능 실험은 시작하지 않는다. 다음 단계의 범위와 결정 사항은 별도로 정한다.

## 2026-09-30 — STEP 0: Top-down Roadmap

STEP 0을 시작하면서 개별 환경과 기술 선택에 들어가기 전에 CASE #01 전체에서 현재 위치를 잃지 않기 위한 Top-down Roadmap을 만들었다. 각 STEP의 목적뿐 아니라 하지 않을 일과 다음 STEP으로 이어지는 관계를 함께 기록했다.

Roadmap은 미래 결과를 확정하는 계획서가 아니다. 현재 계획된 cache, batch, Oracle 진단과 transaction 검증은 앞선 evidence가 해당 방향을 지지할 때 수행할 후보들이다. 관찰과 실험 결과가 계획과 맞지 않으면 이전 단계로 돌아가거나 Roadmap을 수정하며, 중요한 변경 이유는 이 Journal에 남긴다.

현재 상태는 STEP -1 완료, STEP 0 진행 중, STEP 1부터 STEP 16까지 시작 전이다. STEP 0의 환경과 재현성 원칙은 아직 별도로 결정해야 하며, STEP 1은 시작하지 않았다.

## 2026-10-01 — STEP 0: Project Foundation 확정

### 목적과 실제 환경

이후 실험을 같은 조건에서 다시 실행할 수 있도록 최소 개발·실험 기반을 만들었다. 조사 당시 Java, Maven, Oracle, DBeaver와 container runtime은 설치되어 있지 않았다. Windows 10 Pro 22H2 x64와 기존 VS Code 환경은 유지했다.

사용자와 논의한 뒤 Eclipse Temurin JDK 21, Maven Wrapper, Spring Boot 3.5.x, MyBatis 3.x, Oracle JDBC, JUnit, Windows native Oracle AI Database Free와 DBeaver Community를 선택했다. Docker와 Podman은 이번 Case에서 사용하지 않기로 했다.

실제 설치·구성된 주요 버전은 Temurin 21.0.12.1+1 LTS, Maven Wrapper 3.3.4가 받는 Apache Maven 3.9.16, Spring Boot 3.5.16, MyBatis Starter 3.0.5, Oracle JDBC 23.26.3.0.0, Oracle AI Database 26ai Free binary 23.26.0.0.0, DBeaver Community 26.2.1이다.

### 선택 이유와 대안

Maven은 시스템에 별도로 설치하지 않고 Wrapper로 버전과 실행 방식을 저장소에 고정했다. Oracle 측 evidence를 다룰 계획이므로 H2로 대체하지 않았다. 현재 PC에 container runtime이 없고 Windows native 설치 방향을 선택했기 때문에 Docker도 추가하지 않았다. Spring Batch는 아직 필요성이 확인되지 않아 제외했다.

공식 Spring Initializr의 현재 기본 목록은 Spring Boot 4.x로 이동했지만, 이번 Case에서 확정한 3.5.x와 MyBatis 3.x 호환 조합을 유지했다. 애플리케이션은 연구 문서와 실행 코드를 분리하기 위해 `app/`에 두었다.

### 설치 중 수정한 판단

첫 Oracle 설치 시도에서 선행 Visual C++ Runtime 업데이트가 Windows 재시작을 요구해 설치가 중단되었다. 로그의 restart-required 결과를 확인한 뒤 Windows를 재시작하고 Oracle 설치를 다시 진행했다. 두 번째 시도에서 database service와 listener가 정상 생성되었다.

Oracle AI Database 26ai Free는 실무 Oracle 19c와 버전·optimizer·기능·patch 수준이 다르며, 2 CPU, 2 GB database memory, 12 GB user data 제한이 있다. 이 차이와 제한을 이후 성능 결과의 실험환경 제약으로 기록하기로 했다.

### 실제 검증

Maven Wrapper에서 `clean verify`를 실행해 build 성공과 JUnit context test 통과를 확인했다. `spring-boot:run`으로 Spring Boot 3.5.16 애플리케이션이 Java 21에서 시작되고 오류 없이 정상 종료하는 것도 확인했다.

Oracle에서는 `OracleServiceFREE`와 listener 실행을 확인했고 listener에 `FREE`와 `freepdb1` 서비스가 `READY` 상태로 등록되었다. 설치 직후 현재 Codex 프로세스의 로그인 토큰에는 새 `ORA_DBA` 그룹이 반영되지 않아 SQL*Plus 로컬 OS 인증은 확인하지 못했다. 비밀번호를 Codex나 저장소에 전달하지 않았다.

### 다음 STEP으로 넘기는 질문

아직 정하지 않은 것은 synthetic ETL target의 구조, application 전용 Oracle schema와 최소 권한, credential 주입 방식, dataset 규모와 생성 규칙, baseline 측정 기준이다. 이 질문들은 STEP 1 이후 evidence가 필요한 시점에 다룬다. STEP 1의 설계나 ETL 기능 구현은 시작하지 않았다.

## 2026-10-01 — STEP 1: Synthetic ETL Target Design

### 당시 판단: 구현 전에 target을 설계하는 이유

baseline 코드를 먼저 만들면 익숙한 구현 방식이나 예상한 최적화에 맞춰 문제를 구성할 수 있다. 이번 STEP에서는 관찰할 문제의 경계와 명시적으로 모르는 것을 먼저 기록해, 이후 코드와 실험이 처음부터 정답을 심은 demo가 되지 않도록 한다.

실제 회사 시스템에서 가져오는 것은 회사명, 업무 용어, schema나 코드가 아니라 Input → Parse → Validate → Reference Lookup → Business Processing → Persistence → Result라는 일반적인 문제 구조뿐이다. 이렇게 추상화해야 공개 저장소의 기밀성을 지키면서도 진단 과정을 재현할 수 있다.

### 당시 판단: 선택한 domain과 대안

Synthetic Warehouse / Shipment Import를 선택했다. 배송 CSV의 각 품목을 parse하고 supplier, warehouse와 product를 확인한 뒤 금액을 계산해 저장하는 흐름은 ETL 단계를 자연스럽게 표현하고 일반인이 이해하기 쉽다. 주소, 고객, 결제와 재고 같은 불필요한 업무 모델은 제외했다.

주문 가져오기도 고려했지만 할인, 결제와 고객 정책으로 범위가 커질 가능성이 높았다. 단순 이벤트 가져오기는 더 작지만 reference lookup과 관계형 persistence를 자연스럽게 설명하기 어려웠다. 배송 품목 모델이 필요한 구조와 단순성 사이에서 더 적절하다고 판단했다.

### 현재 명시적으로 모르는 것

아직 어떤 단계가 처리시간을 지배하는지 모른다. reference lookup이나 persistence가 병목이라는 증거가 없고, CPU, parsing, memory와 database 중 무엇이 데이터 증가에 따라 먼저 지배적이 될지도 모른다. cache와 batch가 유효한 실험인지조차 이후 evidence로 판단해야 한다. 성능 목표나 예상 개선 배율도 정하지 않았다.

STEP 1 시작 전 최종 환경 확인에서는 재로그인된 Windows 토큰에 `ORA_DBA`가 반영되었고, SQL*Plus 관리자 OS 인증으로 Oracle AI Database 26ai Free 23.26.0.0.0 인스턴스가 `OPEN`/`ACTIVE` 상태임을 read-only 조회로 확인했다. password는 사용하거나 기록하지 않았다. 이는 앞선 STEP 0 기록 이후 확인된 사실이며 기존 Entry는 당시 기록으로 유지한다.

### 다음 STEP으로 넘기는 질문

- CSV의 정확한 문법, 날짜 형식과 숫자 정밀도를 어디까지 고정할 것인가?
- shipment 단위의 입력 일관성과 오류를 baseline에서 어떻게 표현할 것인가?
- 자연스러운 최소 transaction 경계와 persistence 호출 방식은 무엇인가?
- application schema와 최소 권한, credential 주입을 어떻게 구성할 것인가?
- STEP 3에서 어떤 dataset 크기와 값 분포를 재현 가능하게 만들 것인가?

이 질문은 STEP 2 이후 각 범위에서 결정한다. 이번 STEP에서는 ETL 코드, schema, table, dataset과 성능 계측을 만들지 않는다.

## 2026-10-01 — STEP 2: Baseline Implementation

### Baseline을 시작한 이유

STEP 1의 설계를 실제로 실행 가능한 비교 기준으로 만들기 시작했다. 목표는 빠른 구현이 아니라 이후 측정과 진단에서 동일하게 실행할 수 있는 단순하고 기능적으로 올바른 target이다.

### 주요 구현 선택

Web API 없이 `ApplicationRunner`로 명시적인 CSV 파일 한 개를 가져오도록 했다. UTF-8 CSV의 날짜는 ISO-8601 local date-time, 수량은 양의 정수, 단가는 0 이상 소수점 2자리로 확정했다. MyBatis mapper에는 reference 조회와 shipment·item insert에 필요한 SQL만 두었다.

Oracle `FREEPDB1`에는 전용 `ETL_LAB` schema를 사용하고, reference 3종과 shipment 2종 table 및 최소 constraint를 구성하기로 했다. 관리용 SYSDBA와 application 연결을 분리하고, 실제 credential은 환경변수와 SQL*Plus 숨김 입력으로만 전달한다.

### Transaction boundary 판단

같은 `shipment_external_id`의 Shipment Header와 Shipment Items를 하나의 transaction으로 처리한다. 이유는 성능이 아니라 이 관계가 현재 정의한 최소 자연스러운 업무 무결성 경계이기 때문이다. CSV 행 단위 transaction은 shipment의 부분 저장을 허용할 수 있어 사용하지 않는다.

파일 전체를 하나의 transaction으로 묶지도 않는다. file-level all-or-nothing은 현재 요구사항이나 정답으로 가정하지 않는다. 이 선택은 STEP 2 baseline일 뿐이며, 이후 failure injection과 evidence가 file-level atomicity의 필요성을 보여주는지는 열린 질문이다.

### 의도적으로 하지 않은 것

Cache, reference preload, bulk select/load, MyBatis BATCH, parallel/async 처리와 성능 계측을 넣지 않았다. 실제 업무 흐름에 필요한 조회와 저장만 순서대로 수행한다. 대량 dataset도 만들지 않고 correctness fixture를 6행으로 제한했다.

### 구현 중 확인한 점과 아직 모르는 것

Spring transaction proxy가 shipment마다 적용되도록 파일 orchestration과 transaction service를 별도 component로 분리해야 했다. 이 분리는 최적화가 아니라 선택한 무결성 경계를 실제로 적용하기 위한 것이다.

아직 데이터 규모가 커질 때 어느 단계가 지배적인지, shipment 단위 transaction이 failure 상황에서 충분한지, file-level atomicity가 필요한지 모른다. STEP 3에는 재현 가능한 dataset 크기와 분포를, STEP 4에는 baseline 실행·측정 조건을 넘긴다.

### 구현 중 실패와 수정

최초 application user 생성 script에서 SQL*Plus의 기본 substitution verification이 숨김 입력으로 받은 값을 치환된 SQL 문에 표시하는 문제가 발생했다. 해당 credential을 즉시 새 값으로 교체해 무효화하고, 모든 password 입력 script에 `SET VERIFY OFF`를 적용했다. 이후 schema 초기화와 애플리케이션 실행은 secure string을 현재 PowerShell process memory에서만 사용하고 종료 시 지우도록 수정했다. 실제 값은 source, 문서 또는 Git에 기록하지 않았다.

### 실제 correctness 검증

Maven `clean test`에서 6개 테스트가 통과했다. CSV parsing과 validation, line amount 계산, inactive reference 거부, 하나의 shipment header와 여러 item의 관계, 6행 fixture의 shipment별 grouping을 확인했다.

Oracle `FREEPDB1`에서 `ETL_LAB` account가 `OPEN` 상태이며 table 5개와 sequence 5개가 생성된 것을 확인했다. Synthetic reference seed는 Product 4행, Supplier 3행, Warehouse 3행이다. Spring Boot baseline이 fixture를 가져와 Shipment 3행과 Shipment Item 6행을 저장했다.

read-only 검증에서 shipment별 item 수와 금액 합계는 `SHIP-0001` 3개/94.50, `SHIP-0002` 2개/110.00, `SHIP-0003` 1개/150.00이었고 orphan item은 0개였다. 이는 작은 fixture의 기능 검증 결과이며 성능 evidence가 아니다.

## 2026-10-01 — STEP 2 Review: System Mapping and Runtime Verification

### Initial Assumption

처음에는 performance diagnosis가 주로 observation과 measurement에서 시작하고, 이후 timer와 counter 같은 diagnostic instrumentation을 추가해 병목 위치를 찾는다고 생각했다. 이 관점에서는 instrumentation이 진단 초반의 핵심 도구에 가까웠다.

### Actual Experience

STEP 2 baseline은 작은 synthetic application이지만, 처음 보는 Java/Spring Boot/MyBatis 코드의 실행을 따라가려면 먼저 업무 목적과 input/output, entry point, 주요 execution path, component 관계, transaction boundary와 Oracle boundary를 확인해야 했다. Call Tree로 정적 구조를 정리한 뒤 debugger의 Call Stack과 Variables를 통해 CSV row가 Java 객체와 shipment group을 거쳐 MyBatis SQL 직전까지 이동하는 경로를 살펴보았다.

이 과정은 전체 class와 method를 완벽하게 이해하는 작업이 아니었다. 다음과 같이 상위 구조에서 문제와 관련된 경로로 좁혀 가는 code comprehension의 drill-down이었다.

```text
System Purpose
    ↓
Input / Output
    ↓
Entry Point
    ↓
Main Workflow
    ↓
Major Components
    ↓
Transaction Boundary
    ↓
DB / Network / External Boundaries
    ↓
Problem-relevant Call Path
    ↓
Relevant Methods
    ↓
Relevant SQL
```

### Problem With the Initial Model

system map이 없는 상태에서는 timer나 counter를 어디에 두어야 하는지, 측정값이 어떤 업무 단위와 runtime boundary를 나타내는지 판단하기 어렵다. Diagnostic utility 자체보다 먼저 **어디에서 관찰을 시작해야 하는가**를 결정할 근거가 필요했다.

그렇다고 남이 작성한 모든 코드를 완벽하게 이해해야만 진단할 수 있다는 뜻은 아니다. 현재 경험이 가리키는 것은 의미 있는 observation point를 고를 수 있을 정도로 업무 흐름과 실행 지도를 빠르게 복원해야 할 수 있다는 점이다.

### New Insight

System mapping은 문서를 많이 읽는 사전 절차라기보다, 문제와 관련된 실행 경로의 지도를 만드는 활동으로 볼 수 있다. 정적 Call Tree는 후보 지도이고 debugger에서 확인한 실제 호출, 변수 변화, transaction proxy와 DB boundary가 그 지도를 검증하는 runtime evidence가 된다.

현재 더 자연스러워 보이는 순서는 다음과 같다.

```text
System Mapping
    ↓
Runtime Verification
    ↓
Observation Point Selection
    ↓
Instrumentation
```

### Revised Working Hypothesis

CASE #01에서 얻은 method revision candidate는 다음과 같다.

```text
Unknown System
    ↓
Business Mapping
    ↓
System Mapping
    ↓
Code Mapping
    ↓
Runtime Verification
    ↓
Observation Point Selection
    ↓
Instrumentation
    ↓
Measurement
    ↓
Drill-down
    ↓
Evidence
    ↓
Hypothesis
    ↓
Experiment
```

> A troubleshooter does not necessarily need to understand the entire system before diagnosis. The first task may be to reconstruct enough of the system's business and runtime map to know where meaningful observation should begin.

즉 전체 시스템을 완벽하게 이해하는 것이 먼저라기보다, 어디를 관찰해야 하는지 판단할 수 있을 정도로 업무 흐름과 실행 지도를 빠르게 복원하는 능력이 중요할 수 있다.

### Human + AI Collaboration Hypothesis

이번 review에서 AI는 repository와 dependency/call relationship을 탐색하고 candidate Call Tree와 관련 code/SQL 위치를 제시했다. Human의 역할은 그 지도를 이해한 뒤 debugger에서 실제 Call Stack과 Variables를 확인하고, Spring의 보이지 않는 호출과 transaction·database boundary를 runtime evidence로 검증하는 것이었다.

```text
AI-generated system map
    ↓
Human runtime verification
    ↓
Revised system map
    ↓
Instrumentation decision
```

이는 AI가 작성한 코드를 이해하지 않은 채 승인하는 방식이 아니다. AI가 탐색 속도를 높이고 Human이 실제 실행 증거로 지도를 확인·수정하며 observation point를 판단하는 협업 가설이다.

### Future Validation Needed

이 흐름은 CASE #01의 작은 baseline을 이해하는 과정에서 나온 working hypothesis일 뿐 universal methodology로 확정하지 않는다. 다른 언어, 더 큰 시스템, 이미 익숙한 시스템과 production incident에서도 mapping phase가 반복해서 필요한지 확인해야 한다. Mapping을 어느 깊이까지 해야 충분한지, 제한된 시간 안에 어떤 artifact가 가장 유용한지도 아직 모른다.

이번 기록은 방법론을 완성된 결과처럼 포장하기 위한 것이 아니다. `Initial Assumption → Actual Experience → Problem With the Initial Model → New Insight → Revised Working Hypothesis → Future Validation Needed`로 판단이 바뀐 과정을 보존한다.

## 2026-10-02 — STEP 2.5 Born: System Mapper Design

Initial Roadmap에는 STEP 2와 STEP 3 사이의 별도 단계가 없었다. STEP 2 baseline을 구현한 뒤 unfamiliar Spring Boot/MyBatis source를 사람이 이해하는 과정에서 entry point, implicit framework call, transaction과 database boundary를 복원하는 인지 비용이 실제로 드러났다.

이 friction은 성능 계측 전에 어디를 관찰할지 선택할 정도의 system map이 필요할 수 있다는 앞선 working hypothesis로 이어졌다. 그 결과 원래 계획에 없던 STEP 2.5 — System Mapping & Runtime Comprehension을 추가했다. 처음부터 도구가 필요하다고 알고 있었던 것이 아니라 STEP 2 review에서 필요 후보가 생긴 것이다.

현재 tool hypothesis는 interactive source map이 unfamiliar system의 구조와 주요 실행 경로를 이해하는 비용을 줄여 troubleshooting 준비를 개선할 수 있다는 것이다. 아직 효과를 검증하지 않았고, 제품명도 정하지 않았다. Working name은 System Mapper다.

구현부터 시작하지 않고 Purpose & Boundary, Information Model, Visual/UI Design을 먼저 작성했다. Java source, mapper annotation SQL, configuration, Oracle DDL과 tests에서 직접 얻을 수 있는 관계를 `STATIC`, framework semantics로 추론한 관계를 `INFERRED`, 향후 debugger로 확인할 관계를 `VERIFIED`로 구분했다. 이 구분은 AI나 analyzer의 추론을 실행 사실처럼 보이지 않게 하기 위한 것이다.

Analyzer와 HTML Viewer는 아직 만들지 않았다. 다음 판단은 최소 map이 실제 code comprehension에 유용한지, 어떤 자동 추출이 신뢰할 수 있는지, human verification evidence를 어떻게 기록할지에 대한 후속 설계와 검증에 달려 있다. STEP 3은 시작하지 않았다.

## 2026-10-02 — STEP 2.5-D: Minimal Source Analyzer

Python 표준 라이브러리만으로 CASE #01 Java source와 schema SQL을 읽어 deterministic `system-map.json`을 생성했다. ETL Maven/runtime dependency와 source는 변경하지 않았다. Type, method, selected annotation, constructor dependency, declared field receiver를 통한 explicit call, MyBatis annotation SQL과 직접 참조된 DB object는 설계대로 비교적 쉽게 추출할 수 있었다.

첫 ground truth test에서는 interface method가 access modifier를 생략하고 parameter annotation을 포함하기 때문에 mapper method를 놓쳤다. Interface syntax만 허용하도록 parser를 수정했다. 이후 generated map을 확인하면서 method name만 stable ID로 사용하면 `ShipmentCsvReader.error()` overload가 합쳐지는 문제도 발견해 parameter type signature를 ID에 포함했다. 이는 구현 전 IR 초안에서 빠졌던 실제 설계 수정이다.

Receiver type을 field나 parameter 선언에서 확실히 찾을 수 있는 call만 연결했다. 같은 method name이 보인다는 이유만으로 target을 추측하지 않았다. Spring의 component scan과 `ApplicationRunner` 호출, transaction proxy, commit/rollback, MyBatis proxy와 JDBC runtime frame은 정적 사실로 만들지 않았다. Analyzer가 생성한 57개 edge는 모두 `STATIC`이며 `VERIFIED`는 생성하지 않았다.

두 번 생성한 JSON의 SHA-256이 같았고 5개 analyzer test가 통과했다. 현재 artifact는 Type 11, Method 27, SQL 8, DB Object 10의 56 nodes와 57 edges를 포함한다. 이 숫자는 현재 source snapshot의 결과이지 analyzer의 일반적 완전성을 뜻하지 않는다.

HTML Viewer와 performance overlay는 시작하지 않았다. STEP 2.5-E 전에 사람이 결정할 것은 first viewer에서 어떤 node/edge를 기본 표시할지, `COMPONENT` semantic layer를 generated facts와 어떻게 결합할지, inferred framework edge를 별도 curated data로 둘지다.

## 2026-10-02 — STEP 2.5-E: Interactive System Map Viewer v0.1

Analyzer-generated JSON을 수정하지 않고 별도 curation JSON에 system/input/database 의미, type 설명과 execution-oriented root 관계를 두었다. Generated fact와 사람이 부여한 semantic meaning을 분리하니 Spring startup과 MyBatis/JDBC 같은 관계를 `STATIC`으로 오해하지 않고 `INFERRED`로 표현할 수 있었다.

처음에는 8개 root node를 순서대로 선형 배치하려 했지만 CSV input은 reader로 분기되고 application startup은 runner로 이어져 카드 순서가 실제 관계를 잘못 암시할 수 있었다. 첫 화면을 8개 역할 카드와 evidence가 표시된 별도 execution relationship 목록으로 수정했다. 전체 56 nodes를 한 화면에 graph로 그리지 않고 선택 node와 직접 관련된 최대 12개 generated node만 보여주도록 했다.

Vanilla HTML/CSS/JavaScript와 Python standard HTTP server를 사용해 build chain과 frontend dependency를 추가하지 않았다. 실제 browser smoke test에서 System View 8개 node, ShipmentImporter TYPE view, `importFile()` METHOD view, search로 EtlMapper 이동, Mapper method → Insert SQL → SHIPMENT object drill-down, breadcrumb 복귀, detail/evidence/source 표시를 확인했다. Browser console warning과 error는 없었다.

현재 결과는 구현자가 수행한 smoke test이며 utility가 실제 사용자의 comprehension cost를 줄였다는 증거는 아니다. STEP 2.5-F에서는 사용자가 첫 화면의 방향성, node 용어, drill-down 깊이, SQL 가독성, source navigation과 정보 과부하 여부를 직접 평가해야 한다. Runtime verification과 `VERIFIED` evidence는 아직 시작하지 않았다.
