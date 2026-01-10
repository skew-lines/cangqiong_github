package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.DailyTurnoverVO;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //构建对应的营业额列表，当前没有营业额的不在列表中
        LocalDateTime beginTime = begin.atStartOfDay(); //转化成当天的localdatetime
        LocalDateTime endTime = end.plusDays(1).atStartOfDay(); //转换成end+1天的0:0:0
        List<DailyTurnoverVO> dailyTurnoverVOList = orderMapper.getByBeginAndEnd(beginTime, endTime, Orders.COMPLETED);

        //构建日期列表
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate d = begin; !d.isAfter(end); d = d.plusDays(1)) {
            dateList.add(d);
        }

        // 转Map(stream流)
        Map<LocalDate, Double> turnoverMap = dailyTurnoverVOList.stream()
                .collect(Collectors.toMap(
                        DailyTurnoverVO::getDate,//方法引用
                        DailyTurnoverVO::getTurnover
                ));

        //构建对应的营业额列表，补0
        List<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            turnoverList.add(turnoverMap.getOrDefault(date,0.0));
        });



        //封装对象
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(dateList,","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList,","));

        return turnoverReportVO;
    }
}
