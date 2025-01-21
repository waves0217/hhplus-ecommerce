package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
}
