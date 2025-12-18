package com.wunai.springbook1.mapper;

import com.wunai.springbook1.pojo.Bug;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface BugMapper {


        //查询全部
        @Select("SELECT b.*, " +
                "u1.nickname AS handler_name, " +
                "u2.nickname AS submitter_name " +
                "FROM bug b " +
                "LEFT JOIN user u1 ON b.handler_id = u1.id " +
                "LEFT JOIN user u2 ON b.user_id = u2.id")
        List<Bug> bugAll();

        // 添加bug
        @Insert("INSERT INTO bug(title," +
                " severity, " +
                "priority, " +
                "module, " +
                "user_id, " +
                "create_time, " +
                "remark, " +
                "document) " +
                "VALUES(#{title},  #{severity}, #{priority}, #{module}, " +
                "#{user_id}, NOW(3), #{remark}, #{document})")
        int addBug(Bug bug);

        // 新增编辑（更新）方法
        @Update("UPDATE bug " +
                "SET title = #{title}, " +
                "    severity = #{severity}, " +
                "    priority = #{priority}, " +
                "    module = #{module}, " +
                "    remark = #{remark}, " +
                "    document = #{document}, " +
                "    user_id = #{user_id} " +
                "WHERE id = #{id}")
        int updateBug(Bug bug);

        //查找特定用户
        @Select("SELECT b.*, " +
                "u1.nickname AS handler_name, " +
                "u2.nickname AS submitter_name " +
                "FROM bug b " +
                "LEFT JOIN user u1 ON b.handler_id = u1.id " +
                "LEFT JOIN user u2 ON b.user_id = u2.id " +
                "WHERE b.user_id = #{user_id} AND b.title LIKE CONCAT('%', #{title}, '%')")
        List<Bug> getBugsUserId(Integer user_id , String title);
        @Select("SELECT b.*, " +
                "u1.nickname AS handler_name, " +
                "u2.nickname AS submitter_name " +
                "FROM bug b " +
                "LEFT JOIN user u1 ON b.handler_id = u1.id " +
                "LEFT JOIN user u2 ON b.user_id = u2.id " +
                "WHERE b.id =#{id}")
        Bug query_one_bug(Integer id );

        // bug状态更新
        @Update("UPDATE bug " +
                "SET status = #{status}, " +
                "feedback = #{feedback}, " +
                "handler_id = #{handlerId}, "+
                "update_time = #{updateTime} " +
                "WHERE id = #{id}")
        void updateStatus(
                @Param("id") Integer id,
                @Param("status") Integer status,
                @Param("feedback") String feedback,
                @Param("handlerId") Integer handlerId,
                @Param("updateTime") LocalDateTime updateTime
        );

        //查询用户的所有 Bug 数据
        @Select("SELECT b.*, " +
                "u1.nickname AS handler_name, " +
                "u2.nickname AS submitter_name " +
                "FROM bug b " +
                "LEFT JOIN user u1 ON b.handler_id = u1.id " +
                "LEFT JOIN user u2 ON b.user_id = u2.id " +
                "WHERE b.user_id = #{user_id} ")
        List<Bug> getBugs_all_title(Integer user_id );

        //根据 ID 删除指定 Bug 数据
        @Delete("DELETE FROM bug WHERE id = #{id}")
        int deleteById(Integer id);

        //用户bug图表
        @Select("select count(*) from bug where  user_id = #{user_id} and create_time like CONCAT('%', #{date}, '%')")
        int getUserLineData(Integer user_id ,String date);

        //bug图表
        @Select("select count(*) from bug where create_time like CONCAT('%', #{date}, '%')")
        int getLineData(String data);





    }
