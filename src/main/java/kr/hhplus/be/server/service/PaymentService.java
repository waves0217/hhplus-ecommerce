package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.*;
import kr.hhplus.be.server.domain.enums.PaymentStatus;
import kr.hhplus.be.server.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, BalanceRepository balanceRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public Payment createPayment(Long orderId, int amount) {
        long requestStart = System.currentTimeMillis();
        log.info("[START] 결제 요청 - 주문 ID: {}, 금액: {}원, 시작 시간: {}ms", orderId, amount, requestStart);

        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        // 사용자 잔액 조회 및 차감
        Balance balance = balanceRepository.findBalanceForUpdate(order.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자의 잔액 정보를 찾을 수 없습니다."));
        log.info("[LOCK ACQUIRED] 사용자 ID: {}, 현재 잔액: {}원", order.getUser().getUserId(), balance.getAmount());

        balance.subtractAmount(amount);
        balanceRepository.save(balance);
        log.info("[BALANCE UPDATED] 사용자 ID: {}, 차감 후 잔액: {}원", order.getUser().getUserId(), balance.getAmount());

        // 결제 생성
        Payment payment = Payment.create(order, amount);
        long requestEnd = System.currentTimeMillis();
        log.info("[END] 결제 완료 - 주문 ID: {}, 금액: {}원, 종료 시간: {}ms", orderId, amount, requestEnd);

        return paymentRepository.save(payment);
    }

    @Transactional
    public void completePayment(Long paymentId) {
        // 결제 조회
        Payment payment = paymentRepository.findPaymentForUpdate(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제를 완료할 수 없는 상태입니다.");
        }

        // 상태 변경
        payment.completePayment();
        paymentRepository.save(payment);
    }

    @Transactional
    public void failPayment(Long paymentId) {
        // 결제 조회
        Payment payment = paymentRepository.findPaymentForUpdate(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제를 실패 처리할 수 없는 상태입니다.");
        }

        // 상태 변경
        payment.failPayment();
        paymentRepository.save(payment);
    }
}
