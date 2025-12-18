package com.wunai.springbook1.interceptors;

import com.wunai.springbook1.constant.RoleConstant;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理CORS预检请求（OPTIONS请求），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 获取当前用户角色
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token无效\"}");
            return false;
        }
        String role = (String) claims.get("role");

        // 获取请求路径
        String requestURI = request.getRequestURI();


        // 超级管理员拥有所有权限
        if (RoleConstant.SUPER_ADMIN.equals(role)) {
            return true;
        }


        // 管理员权限控制
        if (RoleConstant.ADMIN.equals(role)) {
            // 禁止管理员访问用户管理接口
            if (requestURI.startsWith("/admin/user/")) {
                response.setStatus(403);
                return false;
            }
            return true;
        }

        // 普通用户权限控制
        if (RoleConstant.USER.equals(role)) {
            // 禁止普通用户访问管理员接口
            if (requestURI.startsWith("/admin/") ||
                    requestURI.startsWith("/bugs/update/status") ||  // 修改状态
                    requestURI.startsWith("/user/all")) {  // 查看所有用户
                response.setStatus(403);
                return false;
            }
            return true;
        }

        // 未授权角色
        response.setStatus(403);
        return false;
    }
}
//        // 管理员接口权限控制
//        if (requestURI.startsWith("/admin/") &&
//                !RoleConstant.ADMIN.equals(role) &&
//                !RoleConstant.SUPER_ADMIN.equals(role)) {
//            response.setStatus(403);
//            return false;
//        }
//
//        // 超级管理员接口权限控制
//        if (requestURI.startsWith("/superadmin/") &&
//                !RoleConstant.SUPER_ADMIN.equals(role)) {
//            response.setStatus(403);
//            return false;
//        }



