Redis 적용 보고서: 인기 상품 조회 & 선착순 쿠폰 발급
---

## **1. 개요**

**인기 상품 조회 API**와 **선착순 쿠폰 발급 API**에 Redis를 적용하는 이유, 기존 문제점 및 기대 효과를 분석합니다.

---

## **2. 캐시(Cache)란?**

### **2.1 캐시(Cache)의 정의**

캐시는 **자주 사용되는 데이터나 연산 결과를 임시로 저장하여 성능을 개선하는 기법**입니다.

기본적으로 **CPU, 메모리, 네트워크, 데이터베이스(DB) 등 다양한 레벨에서 활용**됩니다.

### **2.2 캐싱 전략**

- **Write-Through**: 데이터베이스에 쓰는 동시에 캐시에도 저장
- **Write-Back**: 데이터베이스에 즉시 쓰지 않고 캐시에 먼저 저장 후 일정 시간 뒤에 반영
- **Write-Around**: 캐시에 저장하지 않고 데이터베이스에만 저장 (초기 캐시 오염 방지)
- **Read-Through**: 캐시를 통해 데이터베이스의 데이터를 가져옴
- **Cache Aside (Lazy Loading)**: 필요할 때만 캐시에 저장 (이번 프로젝트에서 사용)

---

## **3. 기존 시스템의 문제점**

### **3.1 인기 상품 조회의 문제점**

**현재 인기 상품 조회 API의 문제점은 다음과 같습니다.**

- **조회가 많지만 자주 변경되지 않음**→ 매 요청마다 **DB를 조회**하는 것은 불필요한 비용 발생
- **인기 상품 통계 연산이 무거움**→ `SUM(quantity)`, `GROUP BY` 연산이 포함되어 있어 **조회 성능 저하**
- **트래픽 급증 시 DB 부하 초과 가능성**
  → 트래픽 급증 시 **동일한 쿼리가 DB에 반복적으로 실행됨**

**해결책: Redis 캐싱을 적용하여 조회 성능 개선**

---

### **3.2 선착순 쿠폰 발급의 문제점**

선착순 쿠폰 발급 API는 **다수의 사용자가 동시에 접근하는 경쟁 환경**입니다.

현재 시스템의 주요 문제점은 다음과 같습니다.

- **동시 요청 시 초과 발급 가능성**
  → `쿠폰 재고 감소` 로직이 동시 실행될 경우 **여러 사용자가 동시에 쿠폰을 발급받는 문제 발생**
- **DB Lock(Pessimistic Lock) 사용 시 성능 저하**
  → `@Lock(LockModeType.PESSIMISTIC_WRITE)`을 통해 **비관적 락(Pessimistic Lock)**을 적용했지만, **다음과 같은 단점 존재**
    - 트랜잭션을 오래 유지해야 함 (Deadlock 위험 증가)
    - 성능 저하 발생 (락이 걸려 있는 동안 다른 요청이 대기)
- **트래픽 급증 시 병목 발생**

**해결책: Redis 분산 락(Redisson) 적용하여 쿠폰 발급 경쟁 문제 해결**

---

## **4. 기존 Lock 기반 동시성 제어 방식**

현재 프로젝트에서는 **MySQL의 비관적 락(Pessimistic Lock)**을 적용하여 동시성을 관리하고 있습니다.

### **4.1 기존 락 적용 방식**

**(1) Pessimistic Lock (비관적 락)**

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Coupon c WHERE c.couponId = :couponId")
Optional<Coupon> findCouponForUpdate(@Param("couponId") Long couponId);

```

- **장점**:
    - 동시 접근 시 데이터 정합성 유지 가능
    - 다중 트랜잭션 환경에서도 안전한 데이터 변경 보장
- **단점**:
    - **트랜잭션을 오래 유지해야 함** → 데이터베이스 부하 증가
    - **Deadlock 발생 가능성** → 여러 트랜잭션이 서로 대기하는 상황 발생 가능
    - **성능 저하** → 락을 획득한 트랜잭션이 끝날 때까지 다른 요청이 대기해야 함

### **4.2 캐시 미사용 시 발생하는 문제점 (캐시 스탬피드)**

- *캐시 스탬피드(Cache Stampede)**는 캐시가 만료되었을 때 **여러 요청이 동시에 DB로 향하면서 부하가 급증하는 현상**입니다.

**해결책: 캐시 만료를 분산시키거나, 캐시 리프레시 정책을 적용하여 완화**

---

## **5. Redis 적용을 통한 기대 효과**

### **5.1 인기 상품 조회 성능 개선**

- **조회 요청 시 캐싱된 데이터를 활용**하여 **DB 조회 횟수를 줄임**
- 인기 상품 목록이 **주기적으로 변경**되므로, **TTL(Time-To-Live) 10분 설정**하여 최신 데이터 유지
- 트래픽 급증 시에도 **DB 부하 감소** → 대량 트래픽 환경에서도 빠른 응답 가능

### **5.2 선착순 쿠폰 발급 동시성 문제 해결**

- **Redis 분산 락(Redisson) 적용하여 다중 서버 환경에서도 락 유지**
- **쿠폰 발급 경쟁을 Redis에서 처리** → **DB 부하 감소**
- 기존의 DB 락보다 빠르게 동작하여 **대량 요청 처리 가능**
- 락 만료 시간을 설정하여 **Deadlock 방지**

---

## **6. 결론**

- **Redis를 활용하여 캐싱과 분산 락을 적용하면 다음과 같은 이점이 있습니다.**
1. **조회 성능 개선**
    - 인기 상품 조회 시 **DB 부하 감소, 응답 속도 개선**
    - 트래픽 급증에도 **안정적인 성능 유지**
2. **쿠폰 발급 동시성 제어**
    - 기존 DB 락보다 **성능 우수**, **Deadlock 방지**
    - **선착순 쿠폰 발급 경쟁을 효율적으로 처리**
3. **데이터 정합성 유지**
    - Redis TTL을 활용하여 최신 데이터 유지
    - 락을 사용해 **중복 쿠폰 발급 방지**