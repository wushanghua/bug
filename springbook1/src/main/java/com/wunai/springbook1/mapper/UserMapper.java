package com.wunai.springbook1.mapper;


import com.wunai.springbook1.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);
    // 查找全部用户
    @Select("<script>" +
            "SELECT * FROM user " +
            "<where>" +
            "   <if test='username != null and username != \"\"'>" +
            "       AND username LIKE CONCAT('%', #{username}, '%')" +
            "   </if>" +
            "   <if test='role != null and role != \"\"'>" +
            "       AND role = #{role}" +
            "   </if>" +
            "</where>" +
            "</script>")
    List<User> all(@Param("username") String username, @Param("role") String role);

    @Select("select id from user where id=#{id}")
    Integer  findByUserId(int id);

    //添加
    @Insert("insert into user(username, password,  email,role, create_time, update_time) values(#{username}, #{password}, #{email}, #{role}, now(), now())")
    void add(String username, String password,  String email,String role);

    //更新个人信息
    @Update("update `user` set " +
            "nickname = #{nickname}," +
            "email = #{email}," +
            "role = #{role}," +
            "user_pic = #{userPic}," +
            "update_time = #{updateTime} " +
            "where id = #{id}")
    void update(User user);

    //修改头像
    @Update("update user set " +
            "user_pic= #{avatarUrl}," +
            "update_time=now() " +
            "where id=#{id}")
    void updateAvatar(String avatarUrl,Integer id);


    //修改密码
    @Update("update user set" +
            " password = #{password} " +  //
            "where username = #{username}")  // 按用户名定位用户
    void updateUser(String username, String password);

    //超级管理员变更其他角色权限
    @Update("update user set " +
            "role = #{role}, " +
            "update_time = now() " +
            "where id = #{userId}")
    void updateRole(@Param("userId") Integer userId, @Param("role") String role);

    @Select("select * from user where id = #{id}")
    User findById(Integer id);

    // 删除单个用户
    @Delete("delete from user where id = #{id}")
    int deleteById(Integer id);

    // 批量删除用户
    @Delete({
            "<script>",
            "delete from user where id in ",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "   #{id}",
            "</foreach>",
            "</script>"
    })
    int deleteBatch(@Param("ids") List<Integer> ids);

    //管理员添加用户（完整信息）
    @Insert("insert into user(username, password, nickname, email, role, user_pic, create_time, update_time) " +
            "values(#{username}, #{password}, #{nickname}, #{email}, #{role}, #{userPic}, #{createTime}, #{updateTime})")
    void adminAdd(User user);

}
