package com.delivery.delivery.mapper;

import com.delivery.delivery.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(Long id);
}