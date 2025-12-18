package com.wunai.springbook1.service.impl;

import com.wunai.springbook1.mapper.BugMapper;
import com.wunai.springbook1.pojo.Bug;
import com.wunai.springbook1.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BugServiceImpl implements BugService {

    @Autowired
    private BugMapper bugMapper;

    @Override
    public int addBug(Bug bug) {
        // 补充时间字段（如果数据库需要）
        return bugMapper.addBug(bug);
    }
    @Override
    public List<Bug> bugAll() {
        return bugMapper.bugAll();
    }
    @Override
    public int updateBug(Bug bug) {
        return bugMapper.updateBug(bug);
    }
    @Override
    public int deleteId(Integer id) {
        return bugMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        for (Integer id: ids) {
            this.deleteId(id);
        }
    }

    @Override
    public Bug query_one_bug(Integer id) {

        return bugMapper.query_one_bug(id);
    }
    @Override
    public List<Bug> getBugsUserId(Integer user_id , String title) {

        return bugMapper.getBugsUserId(user_id ,title);
    }

    @Override
    public List<Bug> getBugs_all_title(Integer user_id ) {

        return bugMapper.getBugs_all_title(user_id);
    }

    @Override
    public Integer getUserLineData(Integer user_id,String date) {
        return bugMapper.getUserLineData(user_id, date);
    }

    @Override
    public void updateStatus(Integer id, Integer status, String feedback, Integer handlerId) {
        // 校验状态值是否在合法范围（1-4）
        if (status < 0 || status > 5) {
            throw new RuntimeException("无效的状态值，必须是0-5");
        }
        // 调用Mapper更新数据库
        bugMapper.updateStatus(id, status, feedback,handlerId,LocalDateTime.now());
    }



    @Override
    public Integer getLineData(String date){
        return bugMapper.getLineData(date);
    }
}