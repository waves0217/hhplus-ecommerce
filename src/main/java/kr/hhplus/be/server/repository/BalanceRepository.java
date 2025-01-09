package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
