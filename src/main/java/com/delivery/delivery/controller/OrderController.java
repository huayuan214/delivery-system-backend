package com.delivery.delivery.controller;

import com.delivery.delivery.entity.Order;
import com.delivery.delivery.mapper.OrderMapper;
import com.delivery.delivery.service.AiService;
import com.delivery.delivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AiService aiService; // 注入 AI 服务

    @PostMapping("/create")
    public String create(@RequestParam Long userId, @RequestParam String content) {
        Order order = new Order();
        order.setUserId(userId);
        order.setContent(content);

        // 调用大模型进行智能分类
        String category = aiService.categorizeOrder(content);
        order.setCategory(category);

        orderMapper.insert(order);
        return "下单成功，订单ID：" + order.getId() + "，AI自动归类为：" + category;
    }

    @GetMapping("/list")
    public List<Order> list() {
        return orderMapper.selectPendingList();
    }

    @GetMapping("/{id}")
    public Order detail(@PathVariable Long id) {
        return orderMapper.selectById(id);
    }

    @PostMapping("/grab/{orderId}")
    public String grab(@PathVariable Long orderId, @RequestParam Long riderId) {
        return orderService.grabOrder(orderId, riderId);
    }

    @PostMapping("/complete/{orderId}")
    public String complete(@PathVariable Long orderId, @RequestParam Long riderId) {
        return orderService.completeOrder(orderId, riderId);
    }

    @PostMapping("/ai-chat")
    public String aiChat(@RequestParam String message) {
        // 直接调用 AiService 的 Agent 方法
        return aiService.chatAndExecute(message);
    }
}