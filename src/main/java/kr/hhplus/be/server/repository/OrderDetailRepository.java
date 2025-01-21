package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.OrderDetail;
import kr.hhplus.be.server.dto.TopSellingProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT new kr.hhplus.be.server.dto.TopSellingProductDto(d.product.productId, d.product.name, SUM(d.quantity)) " +
            "FROM OrderDetail d " +
            "WHERE d.order.createdAt >= :startDate " +
            "GROUP BY d.product.productId, d.product.name " +
            "ORDER BY SUM(d.quantity) DESC")
    List<TopSellingProductDto> findTopSellingProducts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

}
