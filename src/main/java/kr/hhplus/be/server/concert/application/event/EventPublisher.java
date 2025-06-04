package kr.hhplus.be.server.concert.application.event;

/**
 * EventPublisher 인터페이스
 * - 도메인 이벤트 발행 추상화
 * - 알림, 로깅 등 부가 기능을 위한 이벤트 처리
 */
public interface EventPublisher {

    /**
     * 이벤트 발행
     * @param event 발행할 이벤트 객체
     */
    void publishEvent(Object event);
}