package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUser_UserId(Long userId);
}
