package com.mszl.blog.service;

import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.params.ArticleParam;
import com.mszl.blog.vo.params.PageParams;

public interface ArticleService {
    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    Result hotArticle(int limit);

    Result newArticles(int limit);

    Result listArchives();

    /**
     *
     * 查看文章详情
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    Result publish(ArticleParam articleParam);
}
