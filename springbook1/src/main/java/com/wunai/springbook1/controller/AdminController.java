//package com.wunai.springbook1.controller;
//
//import com.wunai.springbook1.constant.RoleConstant;
//import com.wunai.springbook1.pojo.Result;
//import com.wunai.springbook1.pojo.User;
//import com.wunai.springbook1.service.UserService;
//import com.wunai.springbook1.utils.ThreadLocalUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//public class AdminController {
//
//
//    @RestController
//    @RequestMapping("/admin/user")
//    public class AdminController {
//        @Autowired
//        private UserService userService;
//
//        // 查看所有用户（仅超级管理员）
//        @GetMapping("/all")
//        public Result<List<User>> getAllUsers() {
//            Map<String, Object> claims = ThreadLocalUtil.get();
//            String role = (String) claims.get("role");
//
//            if (!RoleConstant.SUPER_ADMIN.equals(role)) {
//                return Result.error("没有权限访问");
//            }
//
//            return Result.success(userService.findAll());
//        }
//
//        // 修改用户角色（仅超级管理员）
//        @PatchMapping("/role")
//        public Result updateUserRole(@RequestParam Integer userId, @RequestParam String role) {
//            Map<String, Object> claims = ThreadLocalUtil.get();
//            if (!RoleConstant.SUPER_ADMIN.equals(claims.get("role"))) {
//                return Result.error("没有权限操作");
//            }
//
//            userService.updateUserRole(userId, role);
//            return Result.success("角色更新成功");
//        }
//    }
//}
