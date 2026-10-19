package com.delivery.delivery.mapper;

import com.delivery.delivery.entity.Rider;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiderMapper {
    Rider selectById(Long id);
}