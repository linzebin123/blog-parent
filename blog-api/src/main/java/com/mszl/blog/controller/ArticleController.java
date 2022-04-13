package com.mszl.blog.controller;


import com.mszl.blog.comment.aop.LogAnnotation;
import com.mszl.blog.service.ArticleService;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.params.ArticleParam;
import com.mszl.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//表示返回的为json数据
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module="文章",operator="获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }


    /**
     *
     * 首页最热文章
     */
    @PostMapping("hot")
    public Result hotArticle(){
        int limit=5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     *
     */
    @PostMapping("new")
    public Result newArticles(){
        int limit=5;
        return articleService.newArticles(limit);
    }

    /**
     *
     * 首页文章归档
     */
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     *
     * 文章详情
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    /**
     * 文章发布
     * @param articleParam
     * @return
     */
    @PostMapping("publish")

    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }
}
