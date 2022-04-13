package com.mszl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszl.blog.dao.mapper.ArticleMapper;
import com.mszl.blog.dao.mapper.CommentsMapper;
import com.mszl.blog.dao.pojo.Article;
import com.mszl.blog.dao.pojo.Comment;
import com.mszl.blog.dao.pojo.SysUser;
import com.mszl.blog.service.ArticleService;
import com.mszl.blog.service.CommentsService;
import com.mszl.blog.service.SysUserService;
import com.mszl.blog.utils.UserThreadLocal;
import com.mszl.blog.vo.CommentVo;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.UserVo;
import com.mszl.blog.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result commentsByArticleId(Long id) {
        /**
         * 1.根据文章id查询评论列表 从comments表中查找
         * 2.从comments表中获取作者id后再根据作者id查找作者的相关信息
         * 3.判断level是否为1 要查询是否有没有子评论
         * 4.如果有要根据评论id进行查询（parent_id）
         *
         */
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,id);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> commentList=commentsMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList=copyList(commentList);
        Collections.reverse(commentVoList);
        return Result.success(commentVoList);

    }

    @Override
    public Result comment(CommentParam commentParam) {

        SysUser sysUser= UserThreadLocal.get();
        Comment comment=new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setContent(commentParam.getContent());
        comment.setAuthorId(sysUser.getId());

        comment.setCreateDate(System.currentTimeMillis());
        Long parent=commentParam.getParent();
        if (parent==null||parent==0){
            comment.setLevel(1);
        }else{
            comment.setLevel(2);

        }
        comment.setParentId(parent==null ? 0:parent);
        Long toUserId=commentParam.getToUserId();
        comment.setToUid(toUserId==null ? 0:toUserId);
        commentsMapper.insert(comment);
        //评论增加后对应文章的评论数量也要增加
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,commentParam.getArticleId());
        queryWrapper.eq(Comment::getLevel,1);
        Integer commentCount = commentsMapper.selectCount(queryWrapper);
        Article article = articleMapper.selectById(commentParam.getArticleId());
        article.setCommentCounts(commentCount);
        articleMapper.updateById(article);

        return Result.success(null);

    }

    private List<CommentVo> copyList(List<Comment> commentList) {

        List<CommentVo> commentVoList=new ArrayList<>();
        for (Comment comment : commentList) {
            commentVoList.add(copy(comment));
        }
        return  commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if (level==1){
            Long id=comment.getId();
            List<CommentVo> commentVoList=findComentsByparentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to user 给谁评论
        if (level>1){
            Long toUid=comment.getToUid();
            UserVo toUserVo=sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);

        }
        return commentVo;

    }

    private List<CommentVo> findComentsByparentId(Long id) {

        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        return copyList(commentsMapper.selectList(queryWrapper));

    }
}
