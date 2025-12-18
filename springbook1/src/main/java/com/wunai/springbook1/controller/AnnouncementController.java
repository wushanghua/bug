package com.wunai.springbook1.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wunai.springbook1.constant.RoleConstant;
import com.wunai.springbook1.pojo.Announcement;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.service.AnnouncementService;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notices")
@CrossOrigin(
    origins = "http://localhost:5173", 
    allowCredentials = "true",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 查询公告列表（支持分页和搜索）
     */
    @GetMapping
    public Result getAnnouncements(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 权限检查：只有管理员和超级管理员可以查看
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限访问");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Announcement> list = announcementService.findAll(title);
        PageInfo<Announcement> pageInfo = new PageInfo<>(list);
        
        return Result.success(pageInfo);
    }

    /**
     * 根据ID查询公告
     */
    @GetMapping("/{id}")
    public Result getAnnouncementById(@PathVariable Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限访问");
        }

        Announcement announcement = announcementService.findById(id);
        return announcement != null ? Result.success(announcement) : Result.error("公告不存在");
    }

    /**
     * 新增公告
     */
    @PostMapping
    public Result addAnnouncement(@RequestBody Announcement announcement) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限添加公告");
        }

        int rows = announcementService.insert(announcement);
        return rows > 0 ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限编辑公告");
        }

        announcement.setId(id);
        int rows = announcementService.update(announcement);
        return rows > 0 ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除公告（支持单个和批量删除）
     */
    @DeleteMapping("/{ids}")
    public Result deleteAnnouncements(@PathVariable String ids) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null) {
            return Result.error("未登录");
        }
        String role = (String) claims.get("role");
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限删除公告");
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            
            if (idList.isEmpty()) {
                return Result.error("未选择要删除的公告");
            }
            
            if (idList.size() == 1) {
                int rows = announcementService.deleteById(idList.get(0));
                return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
            } else {
                announcementService.deleteBatch(idList);
                return Result.success("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
