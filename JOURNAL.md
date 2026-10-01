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
