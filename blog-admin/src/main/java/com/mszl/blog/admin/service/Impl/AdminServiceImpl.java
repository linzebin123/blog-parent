package com.mszl.blog.admin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszl.blog.admin.mapper.AdminMapper;
import com.mszl.blog.admin.pojo.Admin;
import com.mszl.blog.admin.pojo.Permission;
import com.mszl.blog.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findAdminByUsername(String username) {
        LambdaQueryWrapper<Admin> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        return admin;

    }

    @Override
    public List<Permission> findPermissionByAdminId(Long id) {

        return adminMapper.findPermissionByAdminId(id);
    }
}
