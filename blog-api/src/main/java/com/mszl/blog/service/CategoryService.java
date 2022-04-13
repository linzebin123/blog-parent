package com.mszl.blog.service;

import com.mszl.blog.vo.CategoryVo;
import com.mszl.blog.vo.Result;


public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result categoryDetailById(Long id);
}
