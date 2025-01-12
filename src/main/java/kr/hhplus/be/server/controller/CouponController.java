package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.User;
import kr.hhplus.be.server.dto.UserCouponResponse;
import kr.hhplus.be.server.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.facade.CouponUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "Coupon API", description = "쿠폰 관련 API를 제공합니다.")
public class CouponController {

    private final CouponUseCase couponUseCase;

    public CouponController(final CouponUseCase couponUseCase) {this.couponUseCase = couponUseCase;}

    /**
     * 쿠폰 조회
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "쿠폰 조회", description = "사용자의 쿠폰을 조회합니다.")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@PathVariable Long userId) {
        List<UserCouponResponse> responses = couponUseCase.getUserCoupons(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 쿠폰 발급
     */
    @PostMapping("/issue")
    @Operation(summary = "쿠폰 발급", description = "특정 쿠폰을 사용자에게 발급합니다.")
    public ResponseEntity<UserCouponResponse> issueCoupon(@RequestBody CouponIssueRequest request) {
        // 사용자 생성 (DB에 저장된 User 객체가 필요함)
        User user = User.builder()
                .userId(request.getUserId())
                .build();

        UserCouponResponse response = couponUseCase.issueCoupon(request.getCouponId(), user);
        return ResponseEntity.ok(response);
    }
}
