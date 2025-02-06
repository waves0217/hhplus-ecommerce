package kr.hhplus.be.server.service;

import kr.hhplus.be.server.dto.TopSellingProductDto;
import kr.hhplus.be.server.repository.OrderDetailRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopSellingService {

    private final OrderDetailRepository orderDetailRepository;

    public TopSellingService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Cacheable(value = "topSellingProducts", key = "#limit", cacheManager = "cacheManager")
    public List<TopSellingProductDto> getTopSellingProducts(int limit) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Pageable pageable = PageRequest.of(0, limit);
        return orderDetailRepository.findTopSellingProducts(threeDaysAgo, pageable);
    }
}
