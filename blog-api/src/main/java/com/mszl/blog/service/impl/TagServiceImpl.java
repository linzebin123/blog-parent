package com.mszl.blog.service.impl;

import com.mszl.blog.dao.mapper.TagMapper;
import com.mszl.blog.dao.pojo.Tag;
import com.mszl.blog.service.TagService;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        List<Tag> tagList=tagMapper.findTagsByArticleId(articleId);

        return copyList(tagList);
    }

    @Override
    public Result hots(int limit) {
        /**
         *
         * 1.标签所拥有的文章最多的就是最热标签
         * 2.查询tag_id然后分组计数降序取limit
         * select tag_id from ms_article_tag group by tag_id ORDER BY count(*) desc
         */
        List<Long> tagIds= tagMapper.findHotsTagIds(limit);
        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.EMPTY_LIST);
        }
        List<Tag> tagList=tagMapper.findTagsByTagIds(tagIds);
        return Result.success(tagList);
    }

    @Override
    public Result findAll() {
        List<Tag> tagList = tagMapper.selectList(null);
        return Result.success(copyList(tagList));

    }

    @Override
    public Result tagDetailById(Long id) {

        Tag tag = tagMapper.selectById(id);
        return Result.success(tag);
    }

    private List<TagVo> copyList(List<Tag> tagList) {
        List<TagVo> tagVoList=new ArrayList<>();
        for(Tag tag:tagList){

            tagVoList.add(copy(tag));
        }
        return tagVoList;



    }

    private TagVo copy(Tag tag) {

        TagVo tagVo=new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
}
