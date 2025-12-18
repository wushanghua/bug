package com.wunai.springbook1.mapper;

import com.wunai.springbook1.pojo.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    @Insert("insert into category (" +
            "category_name, " +
            "category_alias, " +
            "create_user, " +
            "create_time, " +
            "update_time" +
            ") values (" +
            "#{categoryName}, " +  // 对应Category的categoryName字段
            "#{categoryAlias}, " + // 对应Category的categoryAlias字段
            "#{createUser}, " +    // 对应Category的createUser字段
            "#{createTime}, " +    // 对应Category的createTime字段
            "#{updateTime}" +      // 对应Category的updateTime字段
            ")")

    void add(Category category);


}
