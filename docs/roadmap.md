# CASE #01 Roadmap — ETL Performance Troubleshooting

이 문서는 CASE #01 전체 연구의 위치와 진행 방향을 보여주는 살아있는 지도다. 특정 STEP의 결과를 확정하는 문서가 아니며, 새로운 evidence와 판단에 따라 갱신한다.

이 Case는 원인을 알 수 없는 ETL/Batch 성능 문제에서 증거를 수집하고 병목을 좁혀 가는 과정을 검증하기 위해 시작했다. 최종적으로 확인하려는 것은 특정 최적화 기법의 성공 여부만이 아니다. 문제를 관찰하고 측정하며, 가설을 세우고 실험한 뒤, 다시 측정해 결론을 검증하는 전 과정을 다른 사람이 추적할 수 있는 형태로 남길 수 있는지 확인하려 한다.

## 핵심 연구 흐름

```text
UNKNOWN PERFORMANCE PROBLEM
              ↓
           Observe
              ↓
           Measure
              ↓
          Decompose
              ↓
         Drill-down
              ↓
          Evidence
              ↓
         Hypothesis
              ↓
         Experiment
              ↓
        Re-measure
              ↓
        Verification
              ↓
         Reflection
```

이 흐름은 절대적인 일방향 절차가 아니다. 실험 결과가 가설을 지지하지 않거나 새로운 단서가 나타나면 Observation, Measurement 또는 Hypothesis 단계로 돌아간다. 되돌아감과 계획 수정도 연구 과정의 일부다.

## 현재 위치

```text
STEP -1  Origin                       [COMPLETE]
   ↓
STEP  0  Project Foundation           [COMPLETE]
   ↓
STEP  1  Synthetic ETL Target Design  [COMPLETE]
   ↓
STEP  2  Baseline Implementation      [COMPLETE]  ← LATEST COMPLETE
   ↓
  ...
   ↓
STEP 16  Public Proof of Work         [NOT STARTED]
```

사용하는 상태는 `NOT STARTED`, `IN PROGRESS`, `COMPLETE`, `REVISED`다. `REVISED`는 evidence나 실제 진행 결과에 따라 기존 계획이나 산출물의 중요한 부분을 수정했음을 뜻한다.

## STEP 전체 지도

### STEP -1 — Origin `[COMPLETE]`

- **하는 일:** 이 Case를 시작한 이유, Career Hypothesis, Proof of Work 철학, Human + AI 협업 원칙을 기록한다.
- **하지 않는 일:** 구현, 환경 구축, 성능 실험과 결과 해석을 앞당겨 수행하지 않는다.
- **연결:** 연구의 출발점과 기록 원칙을 STEP 0의 환경·재현성 결정에 제공한다.

### STEP 0 — Project Foundation `[COMPLETE]`

- **하는 일:** 연구할 실험 환경, 개발환경, 기술 스택, 최소 Repository 구조와 재현성 원칙을 정한다.
- **하지 않는 일:** ETL target을 설계하거나 baseline 코드와 dataset을 미리 구현하지 않는다.
- **연결:** 이후 설계와 실험이 같은 조건에서 재현될 수 있도록 STEP 1의 토대를 만든다.

### STEP 1 — Synthetic ETL Target Design `[COMPLETE]`

- **하는 일:** 공개 가능한 synthetic 환경에서 어떤 ETL 문제 구조를 재현할지 설계한다.
- **하지 않는 일:** 병목 원인이나 최적화 효과를 미리 결론 내리지 않는다.
- **연결:** 구현할 동작과 관찰 지점을 정의해 STEP 2의 baseline 범위를 정한다.

### STEP 2 — Baseline Implementation `[COMPLETE]`

- **하는 일:** 평범하지만 데이터 증가 시 성능 문제가 드러날 수 있는 baseline ETL을 구현한다.
- **하지 않는 일:** 진단 전에 cache, batch 등 최적화를 적용하지 않는다.
- **Review activity:** Human Verification / Code Walkthrough / Runtime Mapping으로 실행 지도와 observation point 선택 전 이해 과정을 검토했다. 이는 새로운 STEP이 아니라 STEP 2 review다.
- **연결:** 실행 가능한 target을 STEP 3의 dataset과 STEP 4의 baseline 실험에 제공한다.

### STEP 3 — Dataset Generator `[NOT STARTED]`

- **하는 일:** 크기가 다르고 동일 조건에서 다시 만들 수 있는 synthetic dataset을 생성한다.
- **하지 않는 일:** 실제 업무 데이터나 식별 가능한 구조를 복제하지 않는다.
- **연결:** STEP 4에서 조건별 baseline을 비교할 수 있는 입력을 제공한다.

### STEP 4 — Baseline Experiment `[NOT STARTED]`

- **하는 일:** 최적화하지 않은 target을 실행해 최초 baseline evidence를 확보한다.
- **하지 않는 일:** 측정 전에 원인을 단정하거나 개선 실험을 섞지 않는다.
- **연결:** 관찰이 부족한 구간을 확인해 STEP 5의 최소 계측 범위를 정한다.

### STEP 5 — Diagnostic Instrumentation `[NOT STARTED]`

- **하는 일:** 시간이 소비되는 위치를 관찰하기 위한 Case-specific 최소 계측을 추가한다.
- **하지 않는 일:** 범용 진단 toolkit을 전제로 과도한 계측 체계를 만들지 않는다.
- **연결:** STEP 6에서 큰 구간부터 작은 구간으로 병목을 좁힐 근거를 만든다.

### STEP 6 — Drill-down Diagnosis `[NOT STARTED]`

- **하는 일:** 큰 실행 구간에서 작은 구간으로 병목 후보를 단계적으로 좁힌다.
- **하지 않는 일:** 단일 지표나 직관만으로 원인을 확정하지 않는다.
- **연결:** Application 측 후보와 필요한 DB 측 확인 항목을 STEP 7로 넘긴다.

### STEP 7 — Oracle Diagnostic Evidence `[NOT STARTED]`

- **하는 일:** Application 측 관찰과 Oracle 측 evidence를 연결한다.
- **하지 않는 일:** 특정 Oracle 원인이 반드시 발견될 것이라고 가정하지 않는다.
- **연결:** 서로 다른 계층의 evidence를 STEP 8의 명시적인 가설로 통합한다.

### STEP 8 — Hypothesis #1 `[NOT STARTED]`

- **하는 일:** 지금까지 수집한 evidence를 바탕으로 최초 성능 병목 가설과 반증 조건을 명시한다.
- **하지 않는 일:** 아직 측정하지 않은 사실을 근거로 사용하거나 가설을 결론처럼 표현하지 않는다.
- **연결:** 가설을 구분할 수 있는 첫 최적화 실험을 STEP 9에서 설계한다.

### STEP 9 — Experiment #1: Cache `[NOT STARTED]`

- **하는 일:** 앞선 evidence가 지지할 경우 cache 방향의 실험을 수행하고 다시 측정한다.
- **하지 않는 일:** cache가 반드시 효과적이라고 전제하지 않는다.
- **연결:** 결과를 baseline과 비교하고, 필요하면 이전 단계로 돌아가며, 다음 실험의 근거로 사용한다.

### STEP 10 — Experiment #2: Batch `[NOT STARTED]`

- **하는 일:** 앞선 evidence가 지지할 경우 DB 호출 구조를 변경하는 batch 실험을 수행한다.
- **하지 않는 일:** batch가 반드시 효과적이라고 전제하거나 다른 변화의 효과를 섞어 해석하지 않는다.
- **연결:** 독립 실험 결과를 STEP 11의 결합 비교에 제공한다.

### STEP 11 — Combined Optimization `[NOT STARTED]`

- **하는 일:** 개별 실험과 결합 실험을 비교해 어떤 변화가 어떤 효과를 만들었는지 분리해 검증한다.
- **하지 않는 일:** 결합 결과만으로 개별 변화의 기여를 추정하지 않는다.
- **연결:** 확인된 성능 구조를 STEP 12의 신뢰성 검증 대상으로 넘긴다.

### STEP 12 — Transaction / Reliability `[NOT STARTED]`

- **하는 일:** 성능 개선 이후 실패 상황을 주입해 빠른 시스템과 올바른 시스템의 차이를 검증하고, evidence가 요구하면 file-level atomicity를 연구한다.
- **하지 않는 일:** 특정 transaction 문제가 반드시 발생한다고 가정하지 않는다.
- **연결:** 성능과 정확성 조건을 함께 만족하는 후보를 STEP 13의 규모 실험에 사용한다.

### STEP 13 — Scale Experiment `[NOT STARTED]`

- **하는 일:** 데이터 규모 증가에 따른 baseline과 optimized 구조의 scaling 특성을 비교한다.
- **하지 않는 일:** 한 데이터 크기의 개선 결과를 전체 규모에 일반화하지 않는다.
- **연결:** 규모별 evidence를 STEP 14의 재사용성 검토와 STEP 15의 Case 정리에 제공한다.

### STEP 14 — Diagnostic Code Review `[NOT STARTED]`

- **하는 일:** Case #01에서 실제로 만들어진 진단 코드 중 재사용 가치가 있는 것이 있는지 검토한다.
- **하지 않는 일:** 범용 toolkit의 존재나 분리를 미리 가정하지 않는다.
- **연결:** 재사용 가능성과 한계를 STEP 15의 reflection에 반영한다.

### STEP 15 — Case Report `[NOT STARTED]`

- **하는 일:** Problem, Observation, Measurement, Decomposition, Evidence, Hypothesis, Experiment, Failure / Revision, Action, Result, Verification, Reflection을 기준으로 전체 연구 결과를 정리한다.
- **하지 않는 일:** 실패한 가설이나 계획 변경을 삭제해 과정이 직선적이었던 것처럼 만들지 않는다.
- **연결:** 완결된 연구 서사를 STEP 16의 공개 검토 대상으로 제공한다.

### STEP 16 — Public Proof of Work `[NOT STARTED]`

- **하는 일:** GitHub Repository를 하나의 완결된 Case로 정리하고, 다른 사람이 문제에서 검증까지의 과정을 추적할 수 있는지 최종 점검한다.
- **하지 않는 일:** 검증되지 않은 주장이나 공개할 수 없는 업무 정보를 완결성을 위해 추가하지 않는다.
- **연결:** Case #01을 마무리하고 이후 독립적인 Case에서 검토할 질문과 반복 패턴을 남긴다.

## 계획 변경 원칙

STEP 이름과 순서는 현재의 연구 계획이며 각본이 아니다. 실제 병목 원인, 성능 개선 배율, cache와 batch의 효과, Oracle 측 원인, transaction 문제의 발생 여부를 미리 확정하지 않는다. STEP 9의 cache와 STEP 10의 batch도 현재의 experiment candidate일 뿐이다. STEP 4~8의 evidence가 계획된 실험을 지지하지 않으면 해당 STEP을 수정하거나 다른 검증으로 교체한다.

```text
Plan → Evidence → Plan Revision
```

Roadmap 변경도 Proof of Work의 일부다. 중요한 구조 변경이 생기면 이 문서의 상태와 내용을 갱신하고, 변경한 이유와 당시의 판단을 [JOURNAL.md](../JOURNAL.md)에 시간순으로 기록한다.

## 문서의 역할

- [README.md](../README.md): 이 프로젝트가 무엇인지 설명하는 입구
- `docs/roadmap.md`: 전체 연구에서 지금 어디에 있는지 보여주는 지도
- [JOURNAL.md](../JOURNAL.md): 실제 생각과 판단의 변화를 기록하는 시간순 연구노트
- [`docs/origin/`](origin/): 이 연구를 시작한 이유
- [`docs/foundation/`](foundation/): 어떤 환경과 원칙 위에서 연구하는지 기록하는 영역
- [`docs/design/`](design/): 현재 STEP에서 확정한 설계 결정과 명시적인 미결 사항

아직 시작하지 않은 실험과 진단을 위한 폴더는 미리 만들지 않는다. 실제 STEP이 시작될 때 필요한 문서 구조를 결정한다.
