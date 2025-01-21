package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /*@Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);*/
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product " +
            "WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetailsAndProduct(@Param("orderId") Long orderId);
}
