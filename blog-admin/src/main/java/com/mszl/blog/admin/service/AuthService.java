package com.mszl.blog.admin.service;

import com.mszl.blog.admin.pojo.Admin;
import com.mszl.blog.admin.pojo.Permission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AuthService {
    @Autowired
    private AdminService adminService;

    public boolean auth(HttpServletRequest request, Authentication authentication){
        //获得请求路径
        String requestURI = request.getRequestURI();
        Object principal = authentication.getPrincipal();
        if (principal==null||"anonymousUser".equals(principal)){
            //未登陆
            return false;
        }
        UserDetails userDetails= (UserDetails) principal;
        String username = userDetails.getUsername();
        Admin admin = adminService.findAdminByUsername(username);
        if (admin==null){
            return false;
        }
        Long id=admin.getId();
        if (1==id){
            return true;
        }
        List<Permission> permissionList=adminService.findPermissionByAdminId(id);
        requestURI= StringUtils.split(requestURI,"?")[0];
        for(Permission permission:permissionList){
            if (permission.getPath().equals(requestURI)){
                return true;
            }
        }
        return false;
    }
}
