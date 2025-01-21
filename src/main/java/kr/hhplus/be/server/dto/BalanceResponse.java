package kr.hhplus.be.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BalanceResponse {
    private Long userId;
    private Integer amount;
}
