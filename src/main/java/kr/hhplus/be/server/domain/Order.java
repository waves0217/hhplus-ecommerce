package kr.hhplus.be.server.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order") // 테이블 이름을 복수형으로 변경 (Optional)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", nullable = true)
    private UserCoupon userCoupon;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer chargedAmount;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval 추가
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public static Order create(User user, UserCoupon userCoupon, Integer totalPrice, Integer discountAmount) {
        if (totalPrice < discountAmount) {
            throw new IllegalArgumentException("할인 금액은 총 가격보다 클 수 없습니다.");
        }
        Order order = new Order();
        order.user = user;
        order.userCoupon = userCoupon;
        order.totalPrice = totalPrice;
        order.discountAmount = discountAmount;
        order.chargedAmount = totalPrice - discountAmount;
        order.status = OrderStatus.PENDING;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        return order;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeOrder() {
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

}