package com.mszl.blog.admin.service;

import com.mszl.blog.admin.pojo.Admin;
import com.mszl.blog.admin.pojo.Permission;

import java.util.List;

public interface AdminService {
    Admin findAdminByUsername(String username);

    List<Permission> findPermissionByAdminId(Long id);
}
