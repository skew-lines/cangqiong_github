package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//日期-销售额 VO
public class DailyTurnoverVO {

    private LocalDate date; //日期

    private Double turnover;//营业额
}
