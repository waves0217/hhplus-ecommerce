package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.*;
import kr.hhplus.be.server.domain.enums.DiscountType;
import kr.hhplus.be.server.domain.enums.OrderStatus;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import kr.hhplus.be.server.dto.OrderItemRequest;
import kr.hhplus.be.server.repository.BalanceRepository;
import kr.hhplus.be.server.repository.OrderRepository;
import kr.hhplus.be.server.repository.ProductRepository;
import kr.hhplus.be.server.repository.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final BalanceRepository balanceRepository;
    private final UserCouponRepository userCouponRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        BalanceRepository balanceRepository,
                        UserCouponRepository userCouponRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.balanceRepository = balanceRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional
    public Order createOrder(Long userId, List<OrderItemRequest> items, Long couponId) {
        // 사용자 잔액 조회
        Balance balance = balanceRepository.findBalanceForUpdate(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액 정보를 찾을 수 없습니다."));

        // 쿠폰 조회
        UserCoupon userCoupon = null;
        if (couponId != null) {
            userCoupon = userCouponRepository.findUserCouponForUpdate(couponId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
            if (userCoupon.getStatus() == UserCouponStatus.USED) {
                throw new IllegalStateException("이미 사용된 쿠폰입니다.");
            }
        }

        // 상품 처리
        List<OrderDetail> orderDetails = new ArrayList<>();
        int totalPrice = 0;
        for (OrderItemRequest item : items) {
            Product product = productRepository.findProductForUpdate(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("재고가 부족합니다: " + product.getName());
            }

            // 상품 재고 차감
            product.reduceStock(item.getQuantity());
            productRepository.save(product);

            // 총 가격 계산
            totalPrice += product.getPrice() * item.getQuantity();
        }

        // 쿠폰 할인 적용
        int discount = 0;
        if (userCoupon != null) {
            if (userCoupon.getCoupon().getDiscountType() == DiscountType.FIXED) {
                discount = userCoupon.getCoupon().getAmount();
            } else if (userCoupon.getCoupon().getDiscountType() == DiscountType.PERCENT) {
                discount = (totalPrice * userCoupon.getCoupon().getAmount()) / 100;
            }
            userCoupon.markAsUsed();
            userCouponRepository.save(userCoupon);
        }

        // 최종 금액 계산
        int finalPrice = totalPrice - discount;
        if (finalPrice > balance.getAmount()) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        // 잔액 차감
        balance.subtractAmount(finalPrice);
        balanceRepository.save(balance);

        // 주문 생성
        Order order = Order.create(balance.getUser(), userCoupon, totalPrice, discount);

        // 주문 상세 추가 (Order와 연관 설정)
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));
            OrderDetail orderDetail = OrderDetail.create(order, product, item.getQuantity(), product.getPrice());
            orderDetails.add(orderDetail);
        }

        // Order의 orderDetails 동기화
        order.getOrderDetails().addAll(orderDetails);

        // 주문 저장
        return orderRepository.save(order);
    }


    @Transactional(readOnly = true)
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findByIdWithDetailsAndProduct(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));
    }
}
