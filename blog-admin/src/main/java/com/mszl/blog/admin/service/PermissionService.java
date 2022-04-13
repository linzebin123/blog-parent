package com.mszl.blog.admin.service;

import com.mszl.blog.admin.model.PageParam;
import com.mszl.blog.admin.pojo.Permission;
import com.mszl.blog.admin.vo.Result;


public interface PermissionService {
    Result listPermission(PageParam pageParam);

    Result add(Permission permission);

    Result update(Permission permission);

    Result delete(Long id);
}
