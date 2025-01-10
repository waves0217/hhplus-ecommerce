-- User 테이블
CREATE TABLE user (
                      user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL
);

-- Balance 테이블
CREATE TABLE balance (
                         user_id BIGINT NOT NULL PRIMARY KEY,
                         amount INT NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_balance_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- BalanceHistory 테이블
CREATE TABLE balance_history (
                                 amount_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 amount INT NOT NULL,
                                 transaction_type VARCHAR(50) NOT NULL,
                                 created_at DATETIME NOT NULL,
                                 updated_at DATETIME NOT NULL,
                                 CONSTRAINT fk_balance_history_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Coupon 테이블
CREATE TABLE coupon (
                        coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        amount INT NOT NULL,
                        discount_type VARCHAR(50) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        quantity INT NOT NULL,
                        expired_at DATETIME NOT NULL,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL
);

-- UserCoupon 테이블
CREATE TABLE user_coupon (
                             user_coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             coupon_id BIGINT NOT NULL,
                             status VARCHAR(50) NOT NULL,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             CONSTRAINT fk_user_coupon_user FOREIGN KEY (user_id) REFERENCES user(user_id),
                             CONSTRAINT fk_user_coupon_coupon FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id)
);

-- Product 테이블
CREATE TABLE product (
                         product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price INT NOT NULL,
                         stock INT NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL
);

-- Order 테이블
CREATE TABLE `order` (
                         order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         user_coupon_id BIGINT,
                         total_price INT NOT NULL,
                         discount_amount INT NOT NULL,
                         charged_amount INT NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user(user_id),
                         CONSTRAINT fk_order_user_coupon FOREIGN KEY (user_coupon_id) REFERENCES user_coupon(user_coupon_id)
);

-- OrderDetail 테이블
CREATE TABLE order_detail (
                              order_detail_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              order_id BIGINT NOT NULL,
                              product_id BIGINT NOT NULL,
                              quantity INT NOT NULL,
                              price INT NOT NULL,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              CONSTRAINT fk_order_detail_order FOREIGN KEY (order_id) REFERENCES `order`(order_id),
                              CONSTRAINT fk_order_detail_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Payment 테이블
CREATE TABLE payment (
                         payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         amount INT NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES `order`(order_id)
);
