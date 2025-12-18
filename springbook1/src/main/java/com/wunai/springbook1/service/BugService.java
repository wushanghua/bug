package com.wunai.springbook1.service;

import com.wunai.springbook1.mapper.BugMapper;
import com.wunai.springbook1.pojo.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface BugService {
    //添加新的 Bug
    int addBug(Bug bug);//添加
    //更新 Bug 信息
    int updateBug(Bug bug); // 更新bug
    //查询指定用户的所有 Bug
    List<Bug> bugAll();//查询全部
    //根据 ID 查询单个 Bug 详情
    Bug query_one_bug(Integer id);
    //按用户 ID + 标题筛选 Bug
    List<Bug> getBugsUserId(Integer user_id , String title);
    //查询指定用户的所有 Bug
    List<Bug> getBugs_all_title(Integer userId);

    //根据 ID 删除单个 Bug
    int deleteId(Integer id);
    //批量删除 Bug
    void deleteBatch(List<Integer> ids);
    //按用户 ID + 日期获取统计数据
    Integer getUserLineData(Integer user_id ,String date);

    //bug状态更新
    void updateStatus(Integer id, Integer status, String feedback, Integer handlerId);

    //获取bug图表的数据
    Integer getLineData(String date);
}