package com.delivery.delivery.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Rider {
    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createTime;
}