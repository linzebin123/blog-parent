package com.mszl.blog.service;

import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.params.CommentParam;

public interface CommentsService {
    /**
     * 根据文章Id查找评论列表
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    /**
     *
     * 写评论
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}
