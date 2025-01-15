package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.dto.PaymentResponse;
import kr.hhplus.be.server.dto.request.PaymentRequest;
import kr.hhplus.be.server.facade.PaymentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment API", description = "결제 관련 API를 제공합니다.")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    /**
     * 결제 생성
     */
    @PostMapping
    @Operation(summary = "결제 생성", description = "주문 ID와 금액을 기반으로 결제를 생성합니다.")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentUseCase.createPayment(request.getOrderId(), request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 완료 처리
     */
    @PatchMapping("/{paymentId}/complete")
    @Operation(summary = "결제 완료 처리", description = "특정 결제를 완료 상태로 변경합니다.")
    public ResponseEntity<Void> completePayment(@PathVariable Long paymentId) {
        paymentUseCase.completePayment(paymentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 결제 실패 처리
     */
    @PatchMapping("/{paymentId}/fail")
    @Operation(summary = "결제 실패 처리", description = "특정 결제를 실패 상태로 변경합니다.")
    public ResponseEntity<Void> failPayment(@PathVariable Long paymentId) {
        paymentUseCase.failPayment(paymentId);
        return ResponseEntity.ok().build();
    }
}
