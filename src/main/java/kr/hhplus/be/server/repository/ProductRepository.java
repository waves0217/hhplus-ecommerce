package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}