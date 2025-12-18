package com.wunai.springbook1.service.impl;

import com.wunai.springbook1.mapper.AnnouncementMapper;
import com.wunai.springbook1.pojo.Announcement;
import com.wunai.springbook1.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> findAll(String title) {
        return announcementMapper.findAll(title);
    }

    @Override
    public Announcement findById(Integer id) {
        return announcementMapper.findById(id);
    }

    @Override
    public int insert(Announcement announcement) {
        return announcementMapper.insert(announcement);
    }

    @Override
    public int update(Announcement announcement) {
        return announcementMapper.update(announcement);
    }

    @Override
    public int deleteById(Integer id) {
        return announcementMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            announcementMapper.deleteById(id);
        }
    }
}
