package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.Payment;
import kr.hhplus.be.server.domain.enums.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentResponse {

    private final Long paymentId;
    private final Long orderId;
    private final int amount;
    private final PaymentStatus status;

    private PaymentResponse(Long paymentId, Long orderId, int amount, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
