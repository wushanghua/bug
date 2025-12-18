package com.wunai.springbook1.mapper;

import com.wunai.springbook1.pojo.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    // 查询所有公告（支持标题搜索）
    @Select("<script>" +
            "SELECT id, title, content, " +
            "create_time AS createTime, " +
            "update_time AS updateTime " +
            "FROM announcement " +
            "<where>" +
            "<if test='title != null and title != \"\"'>" +
            "title LIKE CONCAT('%', #{title}, '%')" +
            "</if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Announcement> findAll(@Param("title") String title);

    // 根据ID查询公告
    @Select("SELECT id, title, content, " +
            "create_time AS createTime, " +
            "update_time AS updateTime " +
            "FROM announcement WHERE id = #{id}")
    Announcement findById(Integer id);

    // 新增公告
    @Insert("INSERT INTO announcement(title, content, create_time, update_time) " +
            "VALUES(#{title}, #{content}, NOW(3), NOW(3))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    // 更新公告
    @Update("UPDATE announcement SET title = #{title}, content = #{content}, " +
            "update_time = NOW() WHERE id = #{id}")
    int update(Announcement announcement);

    // 删除公告
    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(Integer id);

    // 批量删除公告
    @Delete("<script>" +
            "DELETE FROM announcement WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteBatch(@Param("ids") List<Integer> ids);
}
