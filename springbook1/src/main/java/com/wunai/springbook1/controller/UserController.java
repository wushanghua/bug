package com.wunai.springbook1.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.pojo.User;
import com.wunai.springbook1.service.UserService;
import com.wunai.springbook1.utils.JwtUtil;
import com.wunai.springbook1.utils.Md5Util;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import com.wunai.springbook1.constant.RoleConstant;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;
    private String User_Token ="";

    //登录
    @PostMapping("/login")
    public Result <String> login(
            @RequestParam("username")
            @Pattern(regexp = "^\\S{5,16}$") String username,
            @RequestParam("password")
            @Pattern(regexp = "^\\S{5,16}$") String password,
            @RequestParam("role") String role)
    {
        User loginUser = userService.findByUserName(username);
        //判断用户是否存在
        System.out.println(username+" "+password);
        if (loginUser == null) {
            return Result.error("用户名错误");
        }
        //判断密码是否正确
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword()) && role.equals(loginUser.getRole())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", loginUser.getUsername());
            claims.put("id", loginUser.getId());
            claims.put("role", loginUser.getRole());
            String token =  JwtUtil.genToken(claims);
            return Result.success(token);
        }

        return Result.error("信息错误");
    }



    //用户信息查询&验证用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(@RequestHeader(name = "Authorization")String token){
        if (token != null) {
            token = token.trim(); // 去除前后空格
            // 去除首尾可能的双引号（如前端传的是"eyJhbGciOiJIUzI1NiIs..."）
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
        }
        Map<String, Object> map = JwtUtil.parseToken(token);

        String  username = (String) map.get("username");
        User user = userService.findByUserName(username);

        System.out.println(user);
        System.out.println(token);
        User_Token = token;
        return Result.success(user);
    }


    //用户信息更新（支持管理员/超管更新任意用户，支持头像上传）
    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public Result update(
            @RequestParam(required = false) Integer id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String nickname,
            @RequestParam String email,
            @RequestParam String role,
            @RequestPart(value = "user_pic", required = false) MultipartFile avatarFile) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null || claims.get("id") == null) {
            return Result.error("未登录或身份失效");
        }

        // 权限控制：管理员/超管可指定 id，普通用户只能改自己
        String currentRole = (String) claims.get("role");
        Integer targetId;
        if (RoleConstant.ADMIN.equals(currentRole) || RoleConstant.SUPER_ADMIN.equals(currentRole)) {
            targetId = id != null ? id : (Integer) claims.get("id");
        } else {
            targetId = (Integer) claims.get("id");
        }

        // 仅超级管理员可变更角色，但不能设置为超级管理员
        boolean canChangeRole = RoleConstant.SUPER_ADMIN.equals(currentRole);
        
        // 禁止将角色设置为超级管理员
        if (RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("不允许将用户角色设置为超级管理员");
        }

        User user = new User();
        user.setId(targetId);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);

        // 头像保存
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileUrl = userService.saveAvatarAndReturnUrl(avatarFile);
                user.setUserPic(fileUrl);
            } catch (Exception e) {
                return Result.error("头像上传失败：" + e.getMessage());
            }
        }

        // 密码更新（可选）
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        // 获取原始用户，确保存在并锁定角色
        User oldUser = userService.findById(targetId);
        if (oldUser == null) {
            return Result.error("用户不存在");
        }
        user.setRole(canChangeRole ? role : oldUser.getRole());

        userService.update(user);
        // 如果需要同时改密码
        if (password != null && !password.isEmpty()) {
            userService.updateUser(username, password);
        }
        return Result.success("更新成功");
    }

    //用户头像更新
    @PatchMapping("updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null || claims.get("id") == null) {
            return Result.error("未登录或身份失效");
        }
        userService.updateAvatar(avatarUrl);
        System.out.println(avatarUrl);
        return Result.success("更新头像成功");
    }

    //查询
    @PostMapping("/all") // 新增的接口路径
    public Result getAllUsers(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestBody User user) {
        System.out.println(User_Token);
        Map<String, Object> map = JwtUtil.parseToken(User_Token);
        String  username = (String) map.get("username");

        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userService.findAll(user.getUsername(),user.getRole());
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return Result.success(pageInfo);
    }

    //注册
    @PostMapping("/register")
    public Result zhuce(@RequestBody User user) {
        User u = userService.findByUserName(user.getUsername());
        if (u == null) {
            //名称没被占用
            System.out.println(user);
            userService.register(user.getUsername(), user.getPassword(), user.getEmail());

            return Result.Return("注册成功",200,"success");
        } else {
            //名称被占用
            System.out.println(1);
            //return Result.fail("用户名已被注册");
            return Result.Return("用户名已被注册",500,"error");
        }
    }

    //管理员添加用户（支持multipart/form-data）
    @PostMapping(value = "/admin/add", consumes = "multipart/form-data")
    public Result adminAddUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nickname,
            @RequestParam String email,
            @RequestParam String role,
            @RequestPart(value = "user_pic", required = false) MultipartFile avatarFile) {
        
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String currentRole = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(currentRole) && !RoleConstant.SUPER_ADMIN.equals(currentRole)) {
            return Result.error("没有权限添加用户");
        }

        // 检查用户名是否已存在
        User existingUser = userService.findByUserName(username);
        if (existingUser != null) {
            return Result.error("用户名已被注册");
        }

        // 角色权限检查：禁止创建超级管理员
        if (RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("不允许创建超级管理员账户");
        }

        try {
            // 创建用户对象
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setNickname(nickname);
            newUser.setEmail(email);
            newUser.setRole(role);

            // 处理头像上传
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String fileUrl = userService.saveAvatarAndReturnUrl(avatarFile);
                newUser.setUserPic(fileUrl);
            }

            // 调用服务层添加用户
            userService.adminAddUser(newUser);
            return Result.success("添加用户成功");
        } catch (Exception e) {
            return Result.error("添加用户失败：" + e.getMessage());
        }
    }
    //修改密码
    @PostMapping("/update_password")
    public Result update_password(@RequestBody User u){
        // 1. 查询用户是否存在
        User user = userService.findByUserName(u.getUsername());
        if (user == null) {
            return Result.error("用户名不存在");
        }
        System.out.println("123"+Md5Util.getMD5String(u.getPassword()).equals(user.getPassword()));

        if (Md5Util.getMD5String(u.getPassword()).equals(user.getPassword())) {
            System.out.println(u.getUsername()+"  "+" "+u.getNewPassword());
            userService.updateUser(u.getUsername(),u.getNewPassword());
            return Result.ok("成功");
        }
        return Result.error("当前密码错误");
    }

    // 删除单个用户（管理员/超级管理员）
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限删除用户");
        }
        int rows = userService.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("删除失败或用户不存在");
    }

    // 批量删除用户（管理员/超级管理员）
    @DeleteMapping("/delete/batch")
    public Result<String> deleteUsers(@RequestBody List<Integer> ids) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限删除用户");
        }
        if (ids == null || ids.isEmpty()) {
            return Result.error("未选择用户");
        }
        userService.deleteBatch(ids);
        return Result.success("删除成功");
    }


}

