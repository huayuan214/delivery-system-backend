package com.delivery.delivery.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    // 构造器注入
    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String categorizeOrder(String content) {
        // 提示词：让它只能从这四个选项里挑一个，不能废话
        String prompt = "你是一个校园跑腿系统的智能分类助手。请根据以下跑腿内容，从['代取快递', '代买餐饮', '文印服务', '其他']中选出一个最合适的分类词。只返回这一个词，不要任何标点符号，不要解释。内容：" + content;

        try {
            // 发起请求并获取文本
            String result = chatClient.prompt().user(prompt).call().content();
            System.out.println("========== 大模型原始返回内容: " + result + " ==========");
            // 去除大模型可能产生的空格
            return result != null ? result.trim() : "其他";
        } catch (Exception e) {
            e.printStackTrace();
            // 如果调用失败（比如断网、没额度），兜底返回“其他”，保证主业务不崩
            return "其他";
        }
    }
}