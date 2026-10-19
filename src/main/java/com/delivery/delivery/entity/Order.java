package com.delivery.delivery.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private Long riderId;
    private Integer status;
    private String content;
    private String category;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}