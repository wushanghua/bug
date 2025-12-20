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
    @PostMapping(value = "/admin/update", consumes = "multipart/form-data")
    public Result update(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestPart(value = "user_pic", required = false) MultipartFile avatarFile) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null || claims.get("id") == null) {
            return Result.error("未登录或身份失效");
        }

        // 权限控制：管理员/超管可指定 id，普通用户只能改自己
        String currentRole = (String) claims.get("role");
        
        // 检查是否有管理员权限
        if (!RoleConstant.ADMIN.equals(currentRole) && !RoleConstant.SUPER_ADMIN.equals(currentRole)) {
            return Result.error("没有权限执行此操作");
        }
        
        // 确定目标用户ID
        Integer targetId;
        if (id != null) {
            targetId = id;
        } else {
            return Result.error("用户ID不能为空");
        }

        // 获取原始用户信息
        User oldUser = userService.findById(targetId);
        if (oldUser == null) {
            return Result.error("用户不存在");
        }

        // 构建更新对象，保留原有值
        User user = new User();
        user.setId(targetId);
        user.setUsername(username != null && !username.isEmpty() ? username : oldUser.getUsername());
        user.setNickname(nickname != null && !nickname.isEmpty() ? nickname : oldUser.getNickname());
        user.setEmail(email != null && !email.isEmpty() ? email : oldUser.getEmail());

        // 头像处理
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileUrl = userService.saveAvatarAndReturnUrl(avatarFile);
                user.setUserPic(fileUrl);
            } catch (Exception e) {
                return Result.error("头像上传失败：" + e.getMessage());
            }
        } else {
            user.setUserPic(oldUser.getUserPic());
        }

        // 角色处理：管理员和超级管理员都可以变更角色
        if (role != null && !role.isEmpty() && !role.equals(oldUser.getRole())) {
            // 有角色变更请求
            
            // 获取当前登录用户ID
            Integer currentUserId = (Integer) claims.get("id");
            
            // 禁止修改自己的角色
            if (targetId.equals(currentUserId)) {
                return Result.error("不能修改自己的角色");
            }
            
            // 禁止将角色设置为超级管理员
            if (RoleConstant.SUPER_ADMIN.equals(role)) {
                return Result.error("不允许将用户角色设置为超级管理员");
            }
            
            // 权限检查
            if (RoleConstant.SUPER_ADMIN.equals(currentRole)) {
                // 超级管理员可以修改其他人的角色（除了设置为超级管理员）
                user.setRole(role);
            } else if (RoleConstant.ADMIN.equals(currentRole)) {
                // 管理员只能修改普通用户的角色
                if (RoleConstant.ADMIN.equals(oldUser.getRole()) || RoleConstant.SUPER_ADMIN.equals(oldUser.getRole())) {
                    return Result.error("管理员不能修改其他管理员或超级管理员的角色");
                }
                user.setRole(role);
            } else {
                return Result.error("没有权限修改用户角色");
            }
        } else {
            // 没有角色变更请求，保留原角色
            user.setRole(oldUser.getRole());
        }

        // 密码更新（可选）
        if (password != null && !password.isEmpty()) {
            userService.updateUser(user.getUsername(), password);
        }

        userService.update(user);
        return Result.success("更新成功");
    }

    //用户信息更新（JSON格式，用于前端个人信息页面）
    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody User user) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null || claims.get("id") == null) {
            return Result.error("未登录或身份失效");
        }

        // 获取当前登录用户ID
        Integer currentUserId = (Integer) claims.get("id");
        
        // 普通用户只能更新自己的信息
        user.setId(currentUserId);
        
        // 获取原始用户信息，保持角色和用户名不变
        User oldUser = userService.findById(currentUserId);
        if (oldUser == null) {
            return Result.error("用户不存在");
        }
        
        user.setUsername(oldUser.getUsername());
        user.setRole(oldUser.getRole());
        user.setUserPic(oldUser.getUserPic()); // 保持头像不变
        
        // 只更新昵称和邮箱
        userService.update(user);
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
            @RequestParam @Pattern(regexp = "^[a-zA-Z0-9]{5,16}$", message = "用户名必须是5-16位的英文字母或数字") String username,
            @RequestParam @Pattern(regexp = "^\\S{5,16}$", message = "密码必须是5-16位的非空字符") String password,
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

        // 验证昵称
        if (nickname == null || nickname.trim().isEmpty()) {
            return Result.error("昵称不能为空");
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            return Result.error("昵称长度必须在2-20个字符之间");
        }

        // 验证邮箱格式
        if (email == null || email.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            return Result.error("邮箱格式不正确");
        }

        // 验证角色
        if (role == null || role.trim().isEmpty()) {
            return Result.error("角色不能为空");
        }

        // 检查用户名是否已存在
        User existingUser = userService.findByUserName(username);
        if (existingUser != null) {
            return Result.error("用户名已被注册");
        }

        // 角色权限检查
        // 1. 禁止创建超级管理员
        if (RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("不允许创建超级管理员账户");
        }
        
        // 2. 普通管理员只能创建普通用户，不能创建管理员
        if (RoleConstant.ADMIN.equals(currentRole)) {
            if (RoleConstant.ADMIN.equals(role)) {
                return Result.error("管理员只能创建普通用户，不能创建其他管理员");
            }
        }

        try {
            // 创建用户对象
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(password); // 密码会在service层加密
            newUser.setNickname(nickname.trim());
            newUser.setEmail(email.trim());
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
            log.error("添加用户失败：{}", e.getMessage());
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
        String currentRole = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(currentRole) && !RoleConstant.SUPER_ADMIN.equals(currentRole)) {
            return Result.error("没有权限删除用户");
        }
        
        // 获取要删除的用户信息
        User targetUser = userService.findById(id);
        if (targetUser == null) {
            return Result.error("用户不存在");
        }
        
        // 禁止删除超级管理员（任何人都不能删除）
        if (RoleConstant.SUPER_ADMIN.equals(targetUser.getRole())) {
            return Result.error("不允许删除超级管理员账号");
        }
        
        // 权限检查：管理员只能删除普通用户
        if (RoleConstant.ADMIN.equals(currentRole)) {
            if (RoleConstant.ADMIN.equals(targetUser.getRole())) {
                return Result.error("管理员只能删除普通用户，不能删除其他管理员");
            }
        }
        
        // 超级管理员可以删除普通用户和管理员（但不能删除超级管理员，已在上面检查）
        
        try {
            int rows = userService.deleteById(id);
            return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            // 捕获外键约束异常
            log.error("删除用户失败，用户ID: {}, 错误: {}", id, e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                return Result.error("无法删除该用户，该用户还有关联的数据（文章、分类等），请先删除相关数据");
            }
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    // 批量删除用户（管理员/超级管理员）
    @DeleteMapping("/delete/batch")
    public Result<String> deleteUsers(@RequestBody List<Integer> ids) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String currentRole = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(currentRole) && !RoleConstant.SUPER_ADMIN.equals(currentRole)) {
            return Result.error("没有权限删除用户");
        }
        if (ids == null || ids.isEmpty()) {
            return Result.error("未选择用户");
        }
        
        // 权限检查：检查每个要删除的用户
        for (Integer id : ids) {
            User targetUser = userService.findById(id);
            if (targetUser == null) {
                continue; // 跳过不存在的用户
            }
            
            // 禁止删除超级管理员
            if (RoleConstant.SUPER_ADMIN.equals(targetUser.getRole())) {
                return Result.error("不允许删除超级管理员账号");
            }
            
            // 管理员只能删除普通用户
            if (RoleConstant.ADMIN.equals(currentRole)) {
                if (RoleConstant.ADMIN.equals(targetUser.getRole())) {
                    return Result.error("管理员只能删除普通用户，不能删除其他管理员");
                }
            }
        }
        
        try {
            userService.deleteBatch(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            // 捕获外键约束异常
            log.error("批量删除用户失败，用户IDs: {}, 错误: {}", ids, e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                return Result.error("无法删除选中的用户，部分用户还有关联的数据（文章、分类等），请先删除相关数据");
            }
            return Result.error("删除失败：" + e.getMessage());
        }
    }


}

