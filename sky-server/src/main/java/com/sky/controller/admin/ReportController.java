package com.sky.controller.admin;

import com.aliyuncs.http.HttpResponse;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("订单统计接口")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("营业额数据统计：{} - {}",begin,end);
        //时间日期校验
        if(begin == null || end == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if(begin.isAfter(end)) {
            return Result.error("开始日期不能大于结束日期");
        }
        //业务逻辑处理
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }


    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("用户数据统计：{} - {}",begin,end);
        //时间日期校验
        if(begin == null || end == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if(begin.isAfter(end)) {
            return Result.error("开始日期不能大于结束日期");
        }
        //业务逻辑处理
        UserReportVO userReportVO = reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("订单数据统计：{} - {}",begin,end);
        //时间日期校验
        if(begin == null || end == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if(begin.isAfter(end)) {
            return Result.error("开始日期不能大于结束日期");
        }
        //业务逻辑处理
        OrderReportVO orderReportVO = reportService.ordersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> topTenStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("查询销量排名top10接口：{} - {}",begin,end);
        //时间日期校验
        if(begin == null || end == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if(begin.isAfter(end)) {
            return Result.error("开始日期不能大于结束日期");
        }
        //业务逻辑处理
        SalesTop10ReportVO salesTop10ReportVO = reportService.topTenStatistics(begin,end);
        return Result.success(salesTop10ReportVO);
    }

    /*
    通过Response，输出流下载文件到浏览器
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据报表excel")
    public void export(HttpServletResponse response) {
        reportService.exportBusinessData(response);

    }




}
