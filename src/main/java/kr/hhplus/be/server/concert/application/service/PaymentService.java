package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.model.Payment;

/**
 * PaymentService 인터페이스
 * - 외부 결제 시스템과의 연동 추상화
 * - 클린 아키텍처의 UseCase에서 사용
 */
public interface PaymentService {

    /**
     * 결제 처리
     * @param payment 결제 정보
     * @return 거래 ID (성공시)
     * @throws RuntimeException 결제 실패시
     */
    String processPayment(Payment payment);

    /**
     * 결제 취소
     * @param transactionId 취소할 거래 ID
     * @return 취소 성공 여부
     */
    boolean cancelPayment(String transactionId);

    /**
     * 결제 상태 조회
     * @param transactionId 거래 ID
     * @return 결제 상태
     */
    String getPaymentStatus(String transactionId);
}