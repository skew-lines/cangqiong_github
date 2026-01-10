package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//每日订单VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyOrderNumberVO {
    private LocalDate date;//日期
    private Integer totalNumber;//每日总订单数
}
