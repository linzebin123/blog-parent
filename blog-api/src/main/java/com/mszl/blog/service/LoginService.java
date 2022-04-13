package com.mszl.blog.service;

import com.mszl.blog.dao.pojo.SysUser;
import com.mszl.blog.vo.Result;
import com.mszl.blog.vo.params.LoginParam;


public interface LoginService {
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);
    //退出登陆
    Result logout(String token);

    Result register(LoginParam loginParam);
}
