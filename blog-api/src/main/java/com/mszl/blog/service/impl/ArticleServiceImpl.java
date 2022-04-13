package com.mszl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszl.blog.dao.mapper.ArticleBodyMapper;
import com.mszl.blog.dao.mapper.ArticleMapper;
import com.mszl.blog.dao.mapper.ArticleTagMapper;
import com.mszl.blog.dao.mapper.CommentsMapper;
import com.mszl.blog.dao.pojo.*;
import com.mszl.blog.dos.Archives;
import com.mszl.blog.service.*;
import com.mszl.blog.utils.UserThreadLocal;
import com.mszl.blog.vo.ArticleBodyVo;
import com.mszl.blog.vo.ArticleVo;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.TagVo;
import com.mszl.blog.vo.params.ArticleParam;
import com.mszl.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private CommentsMapper commentsMapper;



//    @Override
//    public Result listArticle(PageParams pageParams) {
//
//        /**
//         * 分页查询article数据库表
//         */
//
//        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
//        //文章分类列表
//        if (pageParams.getCategoryId()!=null){
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        //文章标签列表
//        List<Long> articleIdList=new ArrayList<>();
//        if (pageParams.getTagId()!=null){
//            LambdaQueryWrapper<ArticleTag> queryWrapper1=new LambdaQueryWrapper<>();
//            queryWrapper1.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(queryWrapper1);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList.size()!=0){
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//        }
//        //先进行置顶然后再按时间排序
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records=articlePage.getRecords();
//        //此时不能直接返回数据，因为页面所需要的数据与数据库中的Article数据不一致
//        List<ArticleVo> articleVoList=copyList(records,true,true);
//
//        return Result.success(articleVoList);
//    }

    @Override
    public Result listArticle(PageParams pageParams) {

        Page<Article> articlePage=new Page<>(pageParams.getPage(),pageParams.getPageSize());

        IPage<Article> articleIPage = articleMapper.listArticle(articlePage, pageParams.getCategoryId(), pageParams.getTagId(),
                pageParams.getYear(), pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records,true,true));
    }

    /**
     *
     * 1.最热文章
     * 2.对文章中viewcount进行排序取limit
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Article::getViewCounts);
        lambdaQueryWrapper.select(Article::getId,Article::getTitle);
        lambdaQueryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(lambdaQueryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    /**
     * 1.最新文章
     * 2.对文章中的createDate字段进行排序取limit
     * @param limit
     * @return
     */
    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     *
     * 1.首页文章归档
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList=articleMapper.listArchives();
        return Result.success(archivesList);
    }

    /**
     * 查询文章详情
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1.根据id查询文章信息
         * 2.根据bodyId和categoryId去关联查询
         *
         */
        Article article = articleMapper.selectById(articleId);

        ArticleVo articleVo = copy(article, true, true,true,true);
        //查看完文章后本应该直接返回数据，但是此时做了一个更新操作（阅读数+1），更新时加写锁，阻塞了其他的读操作
        //性能降低，如果更新阅读次数出现问题时不能影响我们的读操作
        //此时使用线程池，将更新操作扔到线程池执行，这样就与主线程隔离开来
        threadService.updateArticleViewCount(articleMapper,article);

        return Result.success(articleVo);



    }

    @Override
    @Transactional
    public Result publish(ArticleParam articleParam) {
            //此接口要加入拦截器当中，否者不能获取到用户信息
        /**
         *
         * 1.发布文章，目的构建article对象
         * 2.需要获取作者id，从当前登陆的用户中获取
         * 3.标签  要将标签加入到关联列表中
         * 4.articleBody 内容存储
         */
        SysUser sysUser = UserThreadLocal.get();
        Article article=new Article();
        article.setAuthorId(sysUser.getId());

        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setWeight(Article.Article_Common);
        //插入后生成文章id
        articleMapper.insert(article);
        //Tag
        List<TagVo> tagList=articleParam.getTags();
        if (tagList!=null){
            for(TagVo tagVo:tagList){
                Long articleId = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagVo.getId());
                articleTagMapper.insert(articleTag);
            }

        }
        //body
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        ArticleVo articleVo=new ArticleVo();
        articleVo.setId(article.getId());
        return Result.success(articleVo);

    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for(Article article:records){
            articleVoList.add(copy(article,true,true,false,false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for(Article article:records){
            articleVoList.add(copy(article,true,true,isBody,isCategory));
        }
        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;
    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo=new ArticleVo();

        BeanUtils.copyProperties(article,articleVo);//将article中相同的属性copy给articlevo
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的接口都需要标签、作者
        if(isTag){
            articleVo.setTags(tagService.findTagsByArticleId(articleVo.getId()));
        }
        if(isAuthor){
            Long authorId=article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody){
            Long bodyId=article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory){
            Long categoryId=article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;

    }
}
