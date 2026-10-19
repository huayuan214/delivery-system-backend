package com.delivery.delivery.mapper;

import com.delivery.delivery.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    Order selectById(Long id);
    List<Order> selectPendingList();
    int grabOrder(Long orderId, Long riderId);
    int updateById(Order order);
    // 确认送达
    int completeOrder(Long orderId, Long riderId);

    // 查询所有超时的订单ID
    List<Long> selectTimeoutOrders();
    // 在 OrderMapper.java 里
    int releaseOrder(Long orderId);
}