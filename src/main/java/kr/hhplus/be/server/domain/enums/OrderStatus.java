package kr.hhplus.be.server.domain.enums;

public enum OrderStatus {
    PENDING,      // 대기 상태
    COMPLETED,    // 주문 확인 및 결제 완료 상태
    CANCELLED     // 취소 상태
}