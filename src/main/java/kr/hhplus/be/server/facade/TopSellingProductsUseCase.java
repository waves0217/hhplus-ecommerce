package kr.hhplus.be.server.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.dto.TopSellingProductDto;
import kr.hhplus.be.server.dto.TopSellingProductResponse;
import kr.hhplus.be.server.domain.product.TopSellingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
/*

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
*/

@Component
public class TopSellingProductsUseCase {

    private final TopSellingService topSellingService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ 추가

    public TopSellingProductsUseCase(TopSellingService topSellingService) {
        this.topSellingService = topSellingService;
    }

    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        List<?> topSellingProducts = topSellingService.getTopSellingProducts(limit); // ✅ `List<?>`로 받아오기

        // 🔽 LinkedHashMap을 TopSellingProductDto로 변환
        List<TopSellingProductDto> convertedList = topSellingProducts.stream()
                .map(obj -> objectMapper.convertValue(obj, TopSellingProductDto.class)) // ✅ 필수 변환
                .collect(Collectors.toList());

        return convertedList.stream()
                .map(TopSellingProductResponse::fromDto)
                .collect(Collectors.toList());
    }
}