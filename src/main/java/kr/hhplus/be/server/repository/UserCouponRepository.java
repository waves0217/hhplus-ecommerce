package kr.hhplus.be.server.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUser_UserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userCouponId = :userCouponId")
    Optional<UserCoupon> findUserCouponForUpdate(@Param("userCouponId") Long userCouponId);
}
