package com.mszl.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszl.blog.dao.pojo.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsMapper extends BaseMapper<Comment> {
}
