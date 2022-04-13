package com.mszl.blog.service;

import com.mszl.blog.dao.pojo.SysUser;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.UserVo;

public interface SysUserService {
    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String acount, String password);
    //根据token查询用户信息
    Result findUserByToken(String token);
    //根据账户查询用户
    SysUser finduserByAccount(String account);
    //保存注册的用户
    void save(SysUser sysUser);
}
