package com.delivery.delivery.service;

import com.delivery.delivery.entity.Order;
import com.delivery.delivery.mapper.OrderMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    @Autowired
    private OrderMapper orderMapper;

    // 移除 AiService 注入，打破循环依赖

    @Tool(description = "用于创建校园跑腿订单。当用户想要买东西、取快递时调用此工具。")
    public String createOrder(Long userId, String content, String category) {
        Order order = new Order();
        order.setUserId(userId);
        order.setContent(content);

        // 如果大模型没有提供分类，给个默认值
        if (category == null || category.isEmpty()) {
            category = "其他";
        }
        order.setCategory(category);

        orderMapper.insert(order);
        return "【系统自动执行】下单成功！订单ID：" + order.getId() + "，内容：" + content + "，分类：" + category;
    }
}