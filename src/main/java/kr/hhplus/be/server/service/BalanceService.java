package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.Balance;
import kr.hhplus.be.server.domain.BalanceHistory;
import kr.hhplus.be.server.domain.enums.BalanceHistoryTransactionType;
import kr.hhplus.be.server.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public BalanceService(BalanceRepository balanceRepository, BalanceHistoryRepository balanceHistoryRepository) {
        this.balanceRepository = balanceRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
    }

    @Transactional(readOnly = true)
    public Integer getBalance(Long userId) {
        return balanceRepository.findById(userId)
                .map(Balance::getAmount)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액을 찾을 수 없습니다."));
    }

    @Transactional
    public void chargeBalance(Long userId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }

        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액을 찾을 수 없습니다."));
        balance.addAmount(amount);
        balanceRepository.save(balance);

        BalanceHistory balanceHistory = BalanceHistory.create(balance.getUser(), amount, BalanceHistoryTransactionType.CHARGE);
        balanceHistoryRepository.save(balanceHistory);
    }

    @Transactional
    public void deductBalance(Long userId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }

        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액을 찾을 수 없습니다."));
        balance.subtractAmount(amount);
        balanceRepository.save(balance);
    }
}
