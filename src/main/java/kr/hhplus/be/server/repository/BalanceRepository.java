package kr.hhplus.be.server.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.userId = :userId")
    Optional<Balance> findBalanceForUpdate(@Param("userId") Long userId);
}
