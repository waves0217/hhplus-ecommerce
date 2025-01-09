package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
