package kr.hhplus.be.server.domain;


import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.BalanceHistoryTransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance_history")
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BalanceHistoryTransactionType transactionType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static BalanceHistory create(User user, Integer amount, BalanceHistoryTransactionType transactionType) {
        BalanceHistory history = new BalanceHistory();
        history.user = user;
        history.amount = amount;
        history.transactionType = transactionType;
        history.createdAt = LocalDateTime.now();
        history.updatedAt = LocalDateTime.now();
        return history;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

}
