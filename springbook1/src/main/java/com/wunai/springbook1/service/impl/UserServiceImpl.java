package com.wunai.springbook1.service.impl;

import com.wunai.springbook1.constant.RoleConstant;
import com.wunai.springbook1.mapper.UserMapper;
import com.wunai.springbook1.pojo.User;
import com.wunai.springbook1.service.UserService;
import com.wunai.springbook1.utils.FileUtil;
import com.wunai.springbook1.utils.Md5Util;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${file.upload.base-path}")
    private String basePath;
    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

//    @Override
//    public void register(String username, String password) {
//        //密码加密
//         String md5String=Md5Util.getMD5String(password);
//        //查询
//        userMapper.add(username,md5String);
//    }

    @Override
    public void register(String username, String password, String email) {
        //密码加密
        String md5String=Md5Util.getMD5String(password);
        //查询
        userMapper.add(username,md5String,email, RoleConstant.USER);
    }

    @Override
    public List<User> findAll(String username,String role) {
        return userMapper.all(username,role); // 调用 mapper 中已定义的 all() 方法
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
      userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {

        Map<String,Object>  map = ThreadLocalUtil.get();

       Integer id=(Integer)map.get("id");
        System.out.println(id);
       userMapper.updateAvatar(avatarUrl,id);

    }
    @Override
    public String saveAvatarAndReturnUrl(MultipartFile file) throws Exception {
        return FileUtil.saveAvatar(file, basePath);
    }
    @Override
    public void updateUser(String username, String password) {

        String md5String=Md5Util.getMD5String(password);
        // 调用Mapper层执行更新操作
        userMapper.updateUser(username, md5String);
    }



    @Override
    public void updateUserRole(Integer userId, String role) {
        userMapper.updateRole(userId, role);
    }

    @Override
    public int deleteById(Integer id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int deleteBatch(List<Integer> ids) {
        return userMapper.deleteBatch(ids);
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public void adminAddUser(User user) {
        // 密码加密
        String md5String = Md5Util.getMD5String(user.getPassword());
        user.setPassword(md5String);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 调用mapper添加用户
        userMapper.adminAdd(user);
    }

}
