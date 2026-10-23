package com.delivery.delivery.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final OrderTools orderTools;

    // 构造器注入
    public AiService(ChatClient.Builder builder, OrderTools orderTools) {
        this.orderTools = orderTools;
        // 2.0 版本使用 defaultTools 注册 POJO 工具
        this.chatClient = builder
                .defaultTools(this.orderTools)
                .build();
    }

    // 普通的分类方法（保留）
    public String categorizeOrder(String content) {
        String prompt = "你是一个分类助手。从['代取快递', '代买餐饮', '文印服务', '其他']中选出一个最合适的分类词。只返回一个词，不要解释。内容：" + content;
        try {
            return chatClient.prompt().user(prompt).call().content().trim();
        } catch (Exception e) {
            return "其他";
        }
    }

    // Agent 智能对话方法
    public String chatAndExecute(String userMessage) {
        String systemPrompt = "你是一个校园跑腿智能管家。你可以帮用户创建订单。" +
                "如果用户要求买东西或取快递，请立刻调用 createOrder 工具帮用户下单。" +
                "当前登录的固定用户ID为 1。";

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }
}