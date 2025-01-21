package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.dto.TopSellingProductDto;
import kr.hhplus.be.server.dto.TopSellingProductResponse;
import kr.hhplus.be.server.service.TopSellingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopSellingProductsUseCase {

    private final TopSellingService topSellingService;

    public TopSellingProductsUseCase(TopSellingService topSellingService) {
        this.topSellingService = topSellingService;
    }

    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        List<TopSellingProductDto> topSellingProducts = topSellingService.getTopSellingProducts(limit);
        return topSellingProducts.stream()
                .map(TopSellingProductResponse::fromDto)
                .collect(Collectors.toList());
    }
}
