package com.wunai.springbook1.service.impl;

import com.wunai.springbook1.mapper.CategoryMapper;
import com.wunai.springbook1.mapper.UserMapper;
import com.wunai.springbook1.pojo.Category;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.service.CategoryService;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class CategoryServiceimpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result add(Category category) {

        category.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        category.setUpdateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

//        Map<String,Object> map = ThreadLocalUtil.get();

//        Integer userId = (Integer) map.get("Id");
        Integer userId = category.getCreateUser();

        category.setCreateUser(userId);

        try {
            System.out.println(userMapper.findByUserId(category.getCreateUser()));
            if(userMapper.findByUserId(category.getCreateUser()) == null)
            {
                //Result.Return("没有该用户id",500,null);
                return Result.Return("没有该用户id",500, null);
                //throw new RuntimeException("没有该用户id");
            }
            else
            {
                System.out.println(category);
                categoryMapper.add(category);
            }
        }
        catch (Exception e)
        {
            return Result.Return("处理失败：{}",500, e.getMessage());
        }
        return Result.Return("添加成功",200, String.valueOf(category));
    }
}
