package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.Balance;
import kr.hhplus.be.server.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional(readOnly = true)
    public Integer getBalance(Long userId) {
        return balanceRepository.findById(userId)
                .map(Balance::getAmount)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액을 찾을 수 없습니다."));
    }

    @Transactional
    public void rechargeBalance(Long userId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }

        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 잔액을 찾을 수 없습니다."));
        balance.addAmount(amount);
        balanceRepository.save(balance);
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
