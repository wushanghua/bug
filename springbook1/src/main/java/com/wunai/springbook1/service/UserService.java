package com.wunai.springbook1.service;

import com.wunai.springbook1.pojo.User;

import java.util.List;

public interface UserService {
    //根据用户名查询查询用户
    User findByUserName(String username);
    //注册用户
    // void register(String username, String password);
    void register(String username, String password, String email);
    //用户信息更新
    void update(User user);

    //查询全部用户和管理员
    List<User> findAll(String username, String role);

    //更新用户头像
    void updateAvatar(String avatarUrl);
    //保存头像并返回url
    String saveAvatarAndReturnUrl(org.springframework.web.multipart.MultipartFile file) throws Exception;

    //更新用户名称以及密码
    void updateUser(String username, String password);
    ////超级管理员变更其他角色权限
    void updateUserRole(Integer userId, String role);

    int deleteById(Integer id);

    int deleteBatch(List<Integer> ids);

    User findById(Integer id);

    //管理员添加用户
    void adminAddUser(User user);

}
