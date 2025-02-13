DB Index 적용 보고서
---

## **인기 상품 조회**
```sql
SELECT d.product_id, [p.name](http://p.name/), SUM(d.quantity)
FROM order_detail d
JOIN product p ON d.product_id = p.product_id
WHERE d.created_at >= NOW() - INTERVAL 3 DAY
GROUP BY d.product_id, [p.name](http://p.name/)
ORDER BY SUM(d.quantity) DESC
LIMIT 5;
```
### **문제점: 집계 연산 (GROUP BY + ORDER BY) 성능 저하**

- `order_detail` 테이블에서 `GROUP BY` 후 `SUM(d.quantity)` 정렬을 수행.
- 많은 데이터가 쌓이면 **인덱스가 없을 경우 전체 테이블을 스캔**(Full Table Scan)하게 됨.

### **해결 방법**

- `created_at`과 `product_id`에 인덱스를 추가하여 **최근 3일 데이터를 빠르게 필터링**하고 **JOIN 연산 최적화**.

### **적용 인덱스**

```sql
CREATE INDEX idx_order_detail_created_at ON order_detail(created_at);
CREATE INDEX idx_order_detail_product ON order_detail(product_id);
```

**주의할 점**

- 너무 자주 업데이트되는 필드(`created_at`)에 인덱스를 추가하면 **인덱스 유지 비용이 증가**할 수 있음.

| 적용 전  | 적용 후  |
|-------|-------|
| 10.7초 | 0.58초 |
---

## **사용자 쿠폰 조회**
```sql
SELECT * FROM user_coupon WHERE user_id = ?;
```
### **문제점: 특정 유저의 쿠폰 조회 속도 저하**

- 특정 `user_id`로 쿠폰을 조회할 때, 테이블이 커지면 **Full Table Scan** 발생 가능성 있음.

### **해결 방법**

- `user_id`에 인덱스를 추가하여 조회 성능 개선.

### **적용 인덱스**

```sql
CREATE INDEX idx_user_coupon_user_id ON user_coupon(user_id);
```

**주의할 점**

- `user_coupon` 테이블이 자주 업데이트되는 경우, **인덱스가 자주 재작성될 위험**이 있음.
- 조회가 잦고 업데이트가 적다면 **인덱스 유지 비용이 적고 효과적**.

| 적용 전 | 적용 후  |
|------|-------|
| 1.1초 | 0.04초 |
---

## **주문 상세 정보 조회**
```sql
SELECT o.*, od.*, p.*
FROM orders o
LEFT JOIN order_detail od ON o.order_id = od.order_id
LEFT JOIN product p ON od.product_id = p.product_id
WHERE o.order_id = ?;
```
### **문제점: 복잡한 JOIN으로 인한 조회 속도 저하**

- `orders`, `order_detail`, `product`를 `LEFT JOIN`하여 주문 상세 정보를 조회하는 쿼리.
- `order_id` 기준으로 조회하는데, **인덱스가 없으면 Full Table Scan 발생**.

### **해결 방법**

- `order_id`, `order_detail.order_id`, `order_detail.product_id`에 인덱스를 추가하여 **JOIN 성능 최적화**.

### **적용 인덱스**

```sql
CREATE INDEX idx_order_id ON orders(order_id);
CREATE INDEX idx_order_detail_order_id ON order_detail(order_id);
CREATE INDEX idx_order_detail_product_id ON order_detail(product_id);
```

| 적용 전 | 적용 후  |
|------|-------|
| 5.76초 | 0.14초 |
