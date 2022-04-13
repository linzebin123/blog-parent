package com.mszl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mszl.blog.dao.mapper.CategoryMapper;
import com.mszl.blog.dao.pojo.Category;
import com.mszl.blog.service.CategoryService;
import com.mszl.blog.vo.CategoryVo;
import com.mszl.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;

    }

    @Override
    public Result findAll() {
        List<Category> categories = categoryMapper.selectList(null);

        return Result.success(copyList(categories));

    }

    @Override
    public Result categoryDetailById(Long id) {

        Category category = categoryMapper.selectById(id);
        return Result.success(category);
    }

    private List<CategoryVo> copyList(List<Category> categories) {
        List<CategoryVo> categoryVoList=new ArrayList<>();
        for(Category category:categories){
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }

    private CategoryVo copy(Category category) {

        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
