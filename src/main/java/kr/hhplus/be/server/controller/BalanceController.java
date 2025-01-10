package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.dto.BalanceChargeRequest;
import kr.hhplus.be.server.dto.BalanceResponse;
import kr.hhplus.be.server.facade.BalanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceUseCase balanceUseCase;

    public BalanceController(BalanceUseCase balanceUseCase) {
        this.balanceUseCase = balanceUseCase;
    }

    /**
     * 사용자 잔액 조회
     */
    @GetMapping("/{userId}")
    @Operation(summary = "사용자 잔액 조회", description = "사용자의 현재 잔액을 조회합니다.")
    public ResponseEntity<BalanceResponse> getUserBalance(@PathVariable Long userId) {
        Integer balance = balanceUseCase.getUserBalance(userId);
        return ResponseEntity.ok(new BalanceResponse(userId, balance));
    }

    /**
     * 사용자 잔액 충전
     */
    @PostMapping("/{userId}/charge")
    @Operation(summary = "사용자 잔액 충전", description = "사용자의 잔액을 충전합니다.")
    public ResponseEntity<Void> chargeUserBalance(
            @PathVariable Long userId,
            @RequestBody BalanceChargeRequest request) {
        balanceUseCase.chargeUserBalance(userId, request.getAmount());
        return ResponseEntity.ok().build();
    }

}
