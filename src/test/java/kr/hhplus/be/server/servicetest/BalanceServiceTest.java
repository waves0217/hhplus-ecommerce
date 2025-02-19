package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.balance.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 조회 성공")
    void testGetBalance_Success() {
        // Given
        Balance balance = new Balance(1L, 1000, null, null, null);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        // When
        Integer result = balanceService.getBalance(1L);

        // Then
        assertEquals(1000, result);
    }

    @Test
    @DisplayName("잔액 충전 성공")
    void testRechargeBalance_Success() {
        // Given
        Balance balance = new Balance(1L, 1000, null, null, null);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        // When
        balanceService.chargeBalance(1L, 500);

        // Then
        verify(balanceRepository).save(balance);
        assertEquals(1500, balance.getAmount());
    }

    @Test
    @DisplayName("잔액 부족 시 차감 실패")
    void testDeductBalance_Fail_InsufficientBalance() {
        // Given
        Balance balance = new Balance(1L, 500, null, null, null);
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));

        // When & Then
        assertThrows(IllegalStateException.class, () -> balanceService.deductBalance(1L, 1000));
    }
}
