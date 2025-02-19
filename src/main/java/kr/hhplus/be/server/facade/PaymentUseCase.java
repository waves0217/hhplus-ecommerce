package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.springframework.stereotype.Component;

@Component
public class PaymentUseCase {

    private final PaymentService paymentService;

    public PaymentUseCase(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResponse createPayment(Long orderId, int amount) {
        Payment payment = paymentService.createPayment(orderId, amount);
        return PaymentResponse.fromEntity(payment);
    }

    public void completePayment(Long paymentId) {
        paymentService.completePayment(paymentId);
    }

    public void failPayment(Long paymentId) {
        paymentService.failPayment(paymentId);
    }
}
