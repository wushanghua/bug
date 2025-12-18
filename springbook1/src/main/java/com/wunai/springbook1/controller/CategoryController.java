package com.wunai.springbook1.controller;


import com.wunai.springbook1.pojo.Category;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    public CategoryService categoryService;

    @PostMapping
    public Result add(@RequestBody Category category) {

        System.out.println(category);
        return categoryService.add(category);
    }
}
