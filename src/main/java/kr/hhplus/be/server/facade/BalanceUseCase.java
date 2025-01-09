package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.service.BalanceService;
import org.springframework.stereotype.Component;

@Component
public class BalanceUseCase {

    private final BalanceService balanceService;

    public BalanceUseCase(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public Integer getUserBalance(Long userId) {
        return balanceService.getBalance(userId);
    }

    public void rechargeUserBalance(Long userId, Integer amount) {
        balanceService.rechargeBalance(userId, amount);
    }

    public void deductUserBalance(Long userId, Integer amount) {
        balanceService.deductBalance(userId, amount);
    }
}

