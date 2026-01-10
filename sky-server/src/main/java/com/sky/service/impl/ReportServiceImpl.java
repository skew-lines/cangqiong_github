package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

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

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //构建日期列表
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate d = begin; !d.isAfter(end); d = d.plusDays(1)) {
            dateList.add(d);
        }

        //构建用户列表
        LocalDateTime beginTime = begin.atStartOfDay(); //转化成当天的localdatetime
        LocalDateTime endTime = end.plusDays(1).atStartOfDay(); //转换成end+1天的0:0:0
        //每一天新用户（当天注册）列表
        List<DailyUserCountVO> dailyUserCountVOList = userMapper.getByBeginAndEnd(beginTime,endTime);
        //截止到每一天的用户列表
        List<TotalUserCountVO> totalUserCountVOList = userMapper.getToEndTime(endTime);
        //转换成map(steam流)
        Map<LocalDate,Long> dailyUserCountVOMap = dailyUserCountVOList.stream().collect(Collectors.toMap(
                DailyUserCountVO::getDate,
                DailyUserCountVO::getCount
        ));

        Map<LocalDate,Long> totalUserCountMap = totalUserCountVOList.stream().collect(Collectors.toMap(
                TotalUserCountVO::getEndDate,
                TotalUserCountVO::getTotalCount
        ));

        //列表补0
        long lastTotal = 0L;
        List<Long> newUserList = new ArrayList<>();
        List<Long> totalUserList = new ArrayList<>();
        for(LocalDate date :  dateList) {
            //没有新用户注册则设置为0
            newUserList.add(dailyUserCountVOMap.getOrDefault(date,0L));
            //获取到截止到当天注册的用户总数
            Long todayTotal = totalUserCountMap.get(date);
            //如果当天没有用户注册，那么当天的数据不会存在map中
            if (todayTotal != null) {
                lastTotal = todayTotal;
            }
            //没有新用户注册则设置为前一天
            totalUserList.add(lastTotal);
        }

        //封装对象
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(StringUtils.join(dateList,","));
        userReportVO.setNewUserList(StringUtils.join(newUserList,","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList,","));
        return userReportVO;
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //构建日期列表
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate d = begin; !d.isAfter(end); d = d.plusDays(1)) {
            dateList.add(d);
        }

        LocalDateTime beginTime = begin.atStartOfDay(); //转化成当天的localdatetime
        LocalDateTime endTime = end.plusDays(1).atStartOfDay(); //转换成end+1天的0:0:0
        //构建每日订单数列表
        List<DailyOrderNumberVO> dailyOrderNumberVOList = orderMapper.getNumberByBeginAndEnd(beginTime,endTime);
        //构建每日有效订单列表
        List<DailyCompletionOrderNumberVO> dailyCompletionOrderNumberVOList = orderMapper.getCompletionNumberByBeginAndEnd(beginTime,endTime,Orders.COMPLETED);
        //转换成map(steam流)
        Map<LocalDate,Integer> dailyOrderNumberVOMap = dailyOrderNumberVOList.stream().collect(Collectors.toMap(
                DailyOrderNumberVO::getDate,
                DailyOrderNumberVO::getTotalNumber
        ));

        Map<LocalDate,Integer> dailyCompletionOrderNumberVOMap = dailyCompletionOrderNumberVOList.stream().collect(Collectors.toMap(
                DailyCompletionOrderNumberVO::getDate,
                DailyCompletionOrderNumberVO::getCompletionNumber
        ));

        //列表补0
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            int tmp = dailyOrderNumberVOMap.getOrDefault(date,0);
            totalOrderCount += tmp;
            orderCountList.add(tmp);
            tmp = dailyCompletionOrderNumberVOMap.getOrDefault(date,0);
            validOrderCount += tmp;
            validOrderCountList.add(tmp);
        }


        //封装对象
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(StringUtils.join(dateList,","));
        orderReportVO.setOrderCountList(StringUtils.join(orderCountList,","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList,","));
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(validOrderCount);
        orderReportVO.setOrderCompletionRate(totalOrderCount.equals(0) ? 0 :  validOrderCount.doubleValue() /totalOrderCount);


        return orderReportVO;
    }

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO topTenStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin.atStartOfDay(); //转化成当天的localdatetime
        LocalDateTime endTime = end.plusDays(1).atStartOfDay(); //转换成end+1天的0:0:0
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop(beginTime,endTime);

        //构建列表
        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        //封装数据
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(nameList,","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList,","));

        return salesTop10ReportVO;
    }
}
