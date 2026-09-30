# Project Foundation

기록 시점: 2026-10-01 / STEP 0 — Project Foundation

이 문서는 CASE #01에서 이후 실험을 수행할 최소 개발·실험 기반과 그 한계를 기록한다. 실제 회사 시스템을 복제하지 않으며, 앞으로 만들 도메인과 데이터는 공개 가능한 synthetic 형태로 제한한다.

## 확인한 로컬 환경

| 항목 | 확인 결과 |
| --- | --- |
| OS | Windows 10 Pro 22H2 x64 (build 19045) |
| IDE | Visual Studio Code 1.139.1 |
| Git | 2.54.0.windows.1 |
| JDK | Eclipse Temurin 21.0.12.1+1 LTS |
| `JAVA_HOME` | 사용자 환경변수로 설정됨 |
| Build tool | Maven Wrapper 3.3.4 / Apache Maven 3.9.16 |
| Database | Oracle AI Database 26ai Free, binary version 23.26.0.0.0 |
| Database service | `OracleServiceFREE` 실행 확인 |
| Listener | 실행 확인, `FREE`와 `freepdb1` 서비스가 `READY`로 등록됨 |
| DB tool | DBeaver Community 26.2.1 |
| Container runtime | 설치하지 않음 |

Oracle 설치 직후 현재 Codex 프로세스의 Windows 로그인 토큰에는 새로 추가된 `ORA_DBA` 그룹이 반영되지 않아 SQL*Plus의 로컬 OS 인증(`/ as sysdba`)은 확인하지 못했다. listener와 인스턴스 등록 상태는 확인했으며, 비밀번호를 자동화 도구나 저장소에 전달하지 않았다. 새 로그인 세션에서 OS 인증을 다시 확인할 수 있다.

## 선택한 애플리케이션 스택

| 구성요소 | 버전 또는 선택 |
| --- | --- |
| Java | 21 LTS |
| Spring Boot | 3.5.16 |
| MyBatis Spring Boot Starter | 3.0.5 |
| Oracle JDBC | `ojdbc11` 23.26.3.0.0 |
| Test | Spring Boot Test / JUnit Jupiter 5.12.2 |
| Packaging | executable JAR |

Java 21은 장기 지원 버전이며 Spring Boot 3.5.x의 지원 범위에 들어간다. MyBatis Starter 3.0.5는 Spring Boot 3.2–3.5 계열을 위한 3.x 라인이다. Oracle JDBC는 Java 21에서 사용할 수 있는 JDBC 4.3 드라이버인 `ojdbc11`을 사용한다.

버전은 재현 가능한 build를 위해 `pom.xml`과 Maven Wrapper 설정에 명시했다. Wrapper가 받는 Maven 3.9.16 ZIP에는 SHA-256 checksum 검증값도 고정했다.

## Repository와 애플리케이션 구조

```text
repository root
├── README.md
├── JOURNAL.md
├── docs/
│   ├── origin/
│   ├── foundation/
│   └── roadmap.md
└── app/
    ├── .mvn/wrapper/
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src/
```

연구 기록과 실행 가능한 애플리케이션을 구분하면서도 하나의 Case 안에서 함께 추적하기 위해 Spring Boot 프로젝트를 `app/`에 둔다. Repository root를 Maven 프로젝트로 만들지 않아 향후 evidence와 문서가 애플리케이션 내부 구조에 종속되지 않게 한다.

현재 애플리케이션은 시작 가능한 skeleton과 context load test만 포함한다. MyBatis와 Oracle JDBC dependency는 포함했지만 DB credential과 DataSource 설정은 없다. synthetic target과 DB 사용 범위가 정해지기 전 자동 연결을 시도하지 않도록 기본 설정에서 `DataSourceAutoConfiguration`을 제외했다. 이 제외 설정을 언제 해제하고 어떤 안전한 설정 방식을 사용할지는 다음 설계 이후 결정한다.

## 재현성 원칙

- Java 21을 사용한다.
- 시스템 Maven 설치에 의존하지 않고 저장소의 Maven Wrapper를 사용한다.
- dependency와 plugin 버전은 Maven 설정으로 해석 가능하게 유지한다.
- build output, IDE 개인 설정, 로컬 환경 파일과 runtime log는 Git에서 제외한다.
- 실행 명령, 조건과 결과를 해당 STEP의 문서나 Journal에 기록한다.
- synthetic dataset은 이후 생성 규칙과 seed 등 재현 조건을 함께 기록한다.
- 실행하지 않은 검증이나 측정하지 않은 성능 수치를 결과로 기록하지 않는다.

현재 최소 검증 명령은 다음과 같다.

```powershell
cd app
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

Unix 계열 환경에서는 같은 Wrapper의 `./mvnw`를 사용할 수 있다. OS와 Oracle 설치 방식이 달라질 수 있으므로 Windows Oracle 환경과 동일하다고 가정하지 않는다.

## Oracle 실험환경의 한계

실험 DB는 Oracle AI Database 26ai Free다. 비교 대상이 될 수 있는 실무 Oracle 19c와 major release, optimizer, 제공 기능, 기본 설정과 patch 수준이 다르다. 따라서 이 Case에서 관찰한 실행계획, 대기 이벤트와 성능 특성을 Oracle 19c 운영 환경에 그대로 일반화하지 않는다. 구조적 경향을 설명할 때에도 버전 차이를 명시하고, 19c에서의 동일 결과를 검증한 것으로 표현하지 않는다.

Oracle AI Database Free에는 다음 리소스 한도가 있다.

- foreground processing 최대 2 CPU
- database memory 최대 2 GB RAM(SGA와 PGA 합계)
- user data 최대 12 GB

이 제한은 처리량, 병렬성, cache 효과, memory pressure, dataset 규모와 scaling 결과에 직접 영향을 줄 수 있다. 향후 baseline과 optimization 결과를 해석할 때 이 환경 제한을 실험 조건으로 함께 기록한다. Free edition에서 얻은 절대 성능 수치를 더 큰 실무 시스템의 용량이나 처리량 예측값으로 사용하지 않는다.

## Secret과 로컬 설정

Oracle 관리자 비밀번호와 이후 생성할 application 계정 credential은 저장소에 기록하지 않는다. `.env`, local/secret Spring configuration과 log 파일은 `.gitignore`에서 제외한다. 공개 예제가 필요해지면 실제 값이 없는 명시적인 placeholder 또는 별도 example 파일을 사용한다.

DBeaver connection profile과 저장된 credential도 로컬 도구 상태로만 관리한다. 실제 업무 DB 접속정보, 내부 hostname, IP와 데이터는 사용하지 않는다.

## 고려했지만 채택하지 않은 대안

- **시스템 Maven 설치:** Wrapper가 build tool 버전을 고정하므로 추가 설치하지 않았다.
- **Docker/Podman 기반 Oracle:** 이번 Case의 확정된 방향에 따라 설치하지 않았다.
- **H2 등 인메모리 DB:** Oracle 측 evidence와 연결하려는 연구 목적을 대체할 수 없어 사용하지 않았다.
- **Spring Boot 4.x:** 공식 Initializr의 현재 기본 계열이지만, 결정된 Spring Boot 3.5.x와 MyBatis 3.x 조합을 유지하기 위해 채택하지 않았다.
- **Spring Batch:** 실제 필요성이 아직 확인되지 않아 추가하지 않았다.
- **DB credential과 연결 설정 선행:** synthetic target과 계정 범위가 정해지지 않아 미뤘다.

## STEP 0에서 확인한 것

- Temurin Java와 `javac`가 21.0.12.1로 실행된다.
- Maven Wrapper가 Apache Maven 3.9.16과 Java 21을 사용한다.
- `clean verify`가 성공하고 JUnit context test 1개가 통과한다.
- Spring Boot 3.5.16 애플리케이션이 시작된 뒤 오류 없이 정상 종료한다.
- Oracle 서비스와 listener가 실행되고 `FREE`와 `freepdb1`이 listener에 `READY`로 등록된다.
- DBeaver Community 26.2.1이 설치되어 있다.

## 아직 결정하지 않은 것

- synthetic ETL target의 도메인과 처리 구조
- application 전용 Oracle schema와 최소 권한
- credential 주입 방식과 local example 형식
- dataset 형식, 규모와 생성 seed
- baseline 측정 항목과 반복 횟수
- 최소 진단 계측의 범위

이 항목들은 evidence 없이 미리 구현하지 않는다. STEP 1은 synthetic ETL target을 설계하는 단계이며 아직 시작하지 않았다.

## STEP 0에서 하지 않은 것

CSV 처리, Mapper, DAO, Service, Entity, dataset generator, 성능 병목, cache, batch optimization, 진단 도구와 transaction 실험을 구현하지 않았다. DB 성능 측정이나 개선 결과도 생성하지 않았다.
