package com.wunai.springbook1.service;

import com.wunai.springbook1.pojo.Announcement;
import java.util.List;

public interface AnnouncementService {
    
    // 查询所有公告（支持标题搜索）
    List<Announcement> findAll(String title);
    
    // 根据ID查询公告
    Announcement findById(Integer id);
    
    // 新增公告
    int insert(Announcement announcement);
    
    // 更新公告
    int update(Announcement announcement);
    
    // 删除公告
    int deleteById(Integer id);
    
    // 批量删除公告
    void deleteBatch(List<Integer> ids);
}
