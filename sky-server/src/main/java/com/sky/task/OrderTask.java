package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
//定时任务类,处理超时订单
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每分钟触发一次,用户超过15min没有支付自动取消订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("定时处理超时订单，当前时间：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if(ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                 orders.setStatus(Orders.CANCELLED);
                 orders.setCancelReason("订单超时，自动取消");
                 orders.setCancelTime(LocalDateTime.now());
                 orderMapper.update(orders);
            });
        }
    }


    /**
     * 每天凌晨 0 点，处理昨天处于派送中的订单
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processDeliveryOrder() {

        LocalDate today = LocalDate.now();

        LocalDateTime begin = today.minusDays(1).atStartOfDay();
        LocalDateTime end = today.atStartOfDay();

        log.info("定时处理派送中订单，时间区间：[{}, {})", begin, end);

        List<Orders> ordersList =
                orderMapper.getByStatusAndOrderTime(
                        Orders.DELIVERY_IN_PROGRESS,
                        begin,
                        end
                );

        ordersList.forEach(orders -> {
            orders.setStatus(Orders.COMPLETED);
            orders.setDeliveryTime(
                    orders.getEstimatedDeliveryTime().plusMinutes(15)
            );
            orderMapper.update(orders);
        });
    }

}
