package kr.hhplus.be.server.concert.application.event;

import kr.hhplus.be.server.model.Seat;

/**
 * 좌석 관련 이벤트 클래스들
 */
public class SeatEvents {

    public static class SeatReserved {
        private final Seat seat;

        public SeatReserved(Seat seat) {
            this.seat = seat;
        }

        public Seat getSeat() {
            return seat;
        }
    }

    public static class SeatCancelled {
        private final Seat seat;

        public SeatCancelled(Seat seat) {
            this.seat = seat;
        }

        public Seat getSeat() {
            return seat;
        }
    }
} 