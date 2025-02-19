package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance")
public class Balance {

    @Id
    private Long userId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public void addAmount(Integer amountToAdd) {
        this.amount += amountToAdd;
    }

    public void subtractAmount(Integer amountToSubtract) {
        if (this.amount < amountToSubtract) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        this.amount -= amountToSubtract;
    }
    public void setAmount(Integer newAmount) {
        if (newAmount < 0) {
            throw new IllegalArgumentException("잔액은 0 이상이어야 합니다.");
        }
        this.amount = newAmount;
        this.updatedAt = LocalDateTime.now();
    }
}

