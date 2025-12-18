package com.wunai.springbook1.service;

import com.wunai.springbook1.pojo.Category;
import com.wunai.springbook1.pojo.Result;
import org.springframework.web.bind.annotation.RequestBody;

public interface CategoryService {
    Result add(Category category);

}
