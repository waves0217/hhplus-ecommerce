부하 테스트 시나리오
---

## 1. 개요

- **테스트 목적**

본 부하 테스트는 MSA 환경에서 특정 API가 높은 동시 요청을 처리할 수 있는지 평가하고, Redis 캐시 및 분산 락(Redisson) 적용의 성능 개선 효과를 검증하는 것을 목적으로 한다.

- **테스트 대상**
  - **테스트 대상 API**: `인기 상품 조회 API (/top-selling-products)`
  - **테스트 도구**: `k6`
  - **테스트 환경**:
    - Spring Boot (`3.4.1`)
    - MySQL (`8.0`)
    - Redis (`7.0`)
    - Testcontainers (테스트 환경 구성)

---

## 2. 부하 테스트 대상 선정 이유

- **자주 호출되는 API**:
  - 메인 페이지에서 매번 호출되는 API이므로 높은 요청량이 예상됨.
- **DB 부하 감소를 위한 Redis 캐시 적용 여부 확인**:
  - 캐싱 적용 후 DB 조회 요청이 줄어드는지 확인.
- **성능 병목 가능성 검토**:
  - 동시 요청 시 Redis 캐시가 정상적으로 동작하는지, API 응답 속도가 일관적인지 테스트 필요.

### **테스트 목표**

- **TPS(초당 트랜잭션 수)**: 동시 요청을 처리하는 속도 확인
- **응답 시간**: API 응답 속도가 일정하게 유지되는지 확인
- **Redis 캐시 히트율**: 캐싱된 데이터가 얼마나 활용되는지 분석
- **데이터베이스 부하 감소 확인**: Redis를 활용하여 DB 조회가 줄어드는지 검토

---

## 3. 부하 테스트 시나리오 및 설정

- **테스트 시나리오**
1. 사용자가 `/top-selling-products?limit=10` API를 요청
2. Redis 캐시에서 데이터를 조회 (미스 발생 시 DB 조회)
3. Redis에 결과를 캐싱
4. API 응답 반환
5. 동일 요청을 여러 개의 동시 클라이언트가 요청 (부하 테스트)
- **테스트 설정**
  - **초당 요청 수 (RPS)**: 10, 50, 100, 500, 1000 증가시키며 테스트
  - **동시 사용자 수**: 50명 → 100명 → 500명 → 1000명 순차 증가
  - **테스트 실행 시간**: 60초
  - **실제 데이터 환경 반영**: 테스트 환경에서 충분한 주문 데이터 삽입 후 실행
  - **Redis 캐시 TTL(Time-To-Live)**: 10분
  

- **K6 설정**
```javascript
import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 100 }, // 10초 동안 100명의 유저 증가
        { duration: '30s', target: 100 }, // 30초 동안 유지
        { duration: '10s', target: 0 }, // 10초 동안 유저 감소
    ],
};

export default function () {
    let res = http.get('http://localhost:8080/top-selling-products?limit=10');
    console.log(`Response time: ${res.timings.duration} ms`);
    sleep(1);
}

```
---

## 5. 기대 결과

1. **성능 향상 여부**
  - 캐시 적용 후 응답 시간이 `300ms → 30ms` 로 개선되었는지 확인
  - TPS 증가 확인 (`50 → 500 요청/초`)
2. **캐시 효과**
  - Redis 캐시 히트율이 90% 이상인지 확인
3. **DB 부하 감소**
  - `DB 조회 횟수`가 줄었는지 검토