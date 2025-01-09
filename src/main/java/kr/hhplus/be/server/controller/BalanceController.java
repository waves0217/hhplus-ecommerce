package kr.hhplus.be.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @PostMapping("/charge")
    public ResponseEntity<Map<String, Object>> chargeBalance(@RequestBody Map<String, Object> request) {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "userId", request.get("userId"),
                "balance", 20000
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int userId) {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "userId", userId,
                "balance", 15000
        ));
        return ResponseEntity.ok(response);
    }
}
