package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalUserCountVO {
    private LocalDate endDate;   // 截止日期（包含当天）
    private Long totalCount;     // 累计用户数
}
