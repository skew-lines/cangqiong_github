package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入一条订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 动态条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据主键id查询订单信息
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Select(
            "select sum(case status when ${@com.sky.entity.Orders@TO_BE_CONFIRMED} then 1 else 0 end) as toBeConfirmed, sum(case status when ${@com.sky.entity.Orders@CONFIRMED} then 1 else 0 end) as confirmed, sum(case status when ${@com.sky.entity.Orders@DELIVERY_IN_PROGRESS} then 1 else 0 end) as deliveryInProgress from orders where status in (${@com.sky.entity.Orders@TO_BE_CONFIRMED},${@com.sky.entity.Orders@CONFIRMED},${@com.sky.entity.Orders@DELIVERY_IN_PROGRESS})"
    )
    OrderStatisticsVO getByStatus();
}
