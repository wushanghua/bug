package com.wunai.springbook1.mapper;

import com.wunai.springbook1.pojo.Bug;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FileMapper {

    // 添加bug
    @Insert("INSERT INTO bug(title, severity, priority, module, " +
            "user_id, create_time, remark, document) " +
            "VALUES(#{title},  #{severity}, #{priority}, #{module}, " +
            "#{user_id}, NOW(3), #{remark}, #{document})")
    int addBug(Bug bug);
}