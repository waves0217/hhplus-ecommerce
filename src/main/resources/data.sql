-- User 초기 데이터
INSERT INTO user (user_id, name, created_at, updated_at) VALUES
                                                             (1, 'Alice', NOW(), NOW()),
                                                             (2, 'Bob', NOW(), NOW());

-- Balance 초기 데이터
INSERT INTO balance (user_id, amount, created_at, updated_at) VALUES
                                                                  (1, 10000, NOW(), NOW()),
                                                                  (2, 5000, NOW(), NOW());

-- Coupon 초기 데이터
INSERT INTO coupon (coupon_id, name, amount, discount_type, status, quantity, expired_at, created_at, updated_at) VALUES
                                                                                                                      (1, '10% Off', 10, 'PERCENT', 'ACTIVE', 100, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
                                                                                                                      (2, '1000 Won Off', 1000, 'FIXED', 'ACTIVE', 50, DATE_ADD(NOW(), INTERVAL 15 DAY), NOW(), NOW());

-- UserCoupon 초기 데이터
INSERT INTO user_coupon (user_coupon_id, user_id, coupon_id, status, created_at, updated_at) VALUES
                                                                                                 (1, 1, 1, 'UNUSED', NOW(), NOW()),
                                                                                                 (2, 2, 2, 'UNUSED', NOW(), NOW());

-- Product 초기 데이터
INSERT INTO product (product_id, name, price, stock, status, created_at, updated_at) VALUES
                                                                                         (1, 'Product A', 1000, 100, 'AVAILABLE', NOW(), NOW()),
                                                                                         (2, 'Product B', 2000, 50, 'AVAILABLE', NOW(), NOW());

-- Order 초기 데이터
INSERT INTO `order` (order_id, user_id, user_coupon_id, total_price, discount_amount, charged_amount, status, created_at, updated_at) VALUES
    (1, 1, 1, 2000, 200, 1800, 'COMPLETED', NOW(), NOW());

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_id, order_id, product_id, quantity, price, created_at, updated_at) VALUES
    (1, 1, 1, 2, 1000, NOW(), NOW());

-- Payment 초기 데이터
INSERT INTO payment (payment_id, order_id, amount, status, created_at, updated_at) VALUES
    (1, 1, 1800, 'COMPLETED', NOW(), NOW());
