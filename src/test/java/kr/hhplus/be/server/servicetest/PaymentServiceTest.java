package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.Order;
import kr.hhplus.be.server.domain.Payment;
import kr.hhplus.be.server.domain.enums.PaymentStatus;
import kr.hhplus.be.server.dto.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import kr.hhplus.be.server.facade.PaymentUseCase;
import kr.hhplus.be.server.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Test
    @DisplayName("결제 생성 성공")
    void testCreatePayment_Success() {
        // Given
        Long orderId = 1L;
        int amount = 5000;

        Payment payment = Payment.builder()
                .paymentId(1L)
                .order(Order.builder().orderId(orderId).build())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentService.createPayment(orderId, amount)).thenReturn(payment);

        // When
        PaymentResponse response = paymentUseCase.createPayment(orderId, amount);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals(orderId, response.getOrderId());
        assertEquals(amount, response.getAmount());
        assertEquals(PaymentStatus.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("결제 완료 성공")
    void testCompletePayment_Success() {
        // Given
        Long paymentId = 1L;

        // Mock 동작 설정
        doNothing().when(paymentService).completePayment(paymentId);

        // When
        paymentUseCase.completePayment(paymentId);

        // Then
        verify(paymentService).completePayment(paymentId);
    }

    @Test
    @DisplayName("결제 실패 처리 성공")
    void testFailPayment_Success() {
        // Given
        Long paymentId = 1L;

        // Mock 동작 설정
        doNothing().when(paymentService).failPayment(paymentId);

        // When
        paymentUseCase.failPayment(paymentId);

        // Then
        verify(paymentService).failPayment(paymentId);
    }
}
