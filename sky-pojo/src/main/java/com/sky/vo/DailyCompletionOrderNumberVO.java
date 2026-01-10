package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//每日有效订单VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCompletionOrderNumberVO {
    private LocalDate date;//日期
    private Integer completionNumber;//每日总订单数
}
