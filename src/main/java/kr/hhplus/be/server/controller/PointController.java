package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.service.PointService;
import kr.hhplus.be.server.dto.PointChargeRequest;
import kr.hhplus.be.server.dto.PointChargeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/charge")
    public ResponseEntity<PointChargeResponse> chargePoints(@RequestBody PointChargeRequest request) {
        PointChargeResponse response = pointService.chargePoints(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID userId) {
        BigDecimal balance = pointService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
} 