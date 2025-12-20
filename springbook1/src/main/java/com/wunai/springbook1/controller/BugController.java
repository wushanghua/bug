package com.wunai.springbook1.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wunai.springbook1.constant.RoleConstant;
import com.wunai.springbook1.pojo.Bug;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.service.BugService;
import com.wunai.springbook1.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bugs")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BugController {

    @Autowired
    private BugService bugService;

    // 从配置文件获取上传基础路径
    @Value("${file.upload.base-path}")
    private String basePath;

    private static final String DOCUMENT_SUB_DIR = "document_files";

    // 允许的文件MIME类型（仅图片+视频）
    private static final String[] ALLOWED_TYPES = {
            // 图片类型
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
            // 视频类型
            "video/mp4", "video/avi", "video/mpeg", "video/quicktime", "video/x-msvideo",
            "video/webm", "video/ogg"
    };

    //bug状态更新
    @PatchMapping("/{id}/status")
    public Result updateBugStatus(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestParam(required = false) String feedback) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");

        // 只有管理员和超级管理员可以更新状态
        if (!RoleConstant.ADMIN.equals(role) && !RoleConstant.SUPER_ADMIN.equals(role)) {
            return Result.error("没有权限更新漏洞状态");
        }
        if (status < 0 || status > 5) {
            return Result.error("状态值无效，必须是0-5");
        }

        bugService.updateStatus(id, status, feedback, (Integer) claims.get("id"));
        return Result.success("状态更新成功");
    }


    /**
     * 提交bug表单（支持多文件上传）
     */
    @PostMapping(value = "/submit", consumes = "multipart/form-data")
    public Result addBug(
            @ModelAttribute Bug bug,
            @RequestParam(value = "file", required = false) MultipartFile[] files) { // 接收多文件
        try {
            // 获取当前登录用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = (Integer) claims.get("id");
            bug.setUser_id(userId);

            // 处理文件上传
            if (files != null && files.length > 0) {
                List<String> fileUrls = new ArrayList<>();
                // 构建document文件夹路径：basePath/document_files
                File documentDir = new File(basePath, DOCUMENT_SUB_DIR);
                // 确保文件夹存在（不存在则创建）
                if (!documentDir.exists()) {
                    documentDir.mkdirs(); // 递归创建目录
                }

                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        continue; // 跳过空文件
                    }

                    // 1. 校验文件类型
                    String contentType = file.getContentType();
                    boolean isAllowed = false;
                    for (String type : ALLOWED_TYPES) {
                        if (type.equals(contentType)) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        throw new RuntimeException("不支持的文件类型！仅支持图片(jpg/jpeg/png/gif/bmp/webp)和视频(mp4/avi/mpeg/quicktime/webm/ogg)");
                    }

                    // 2. 生成唯一文件名（时间戳+原文件名，防止重名）
                    String originalFilename = file.getOriginalFilename();
                    String fileName = System.currentTimeMillis() + "_" + originalFilename;

                    // 3. 构建完整存储路径：basePath/document_files/文件名
                    File storageFile = new File(documentDir, fileName);

                    // 4. 保存文件到document文件夹
                    file.transferTo(storageFile);

                    // 5. 生成文件访问URL（包含document_files子路径，与FileController下载接口匹配）
                    // 对文件名进行URL编码，支持中文和特殊符号
                    // 例如：http://localhost:8080/files/download/document_files/1620000000000_test.png
                    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                    String fileUrl = "http://localhost:8080/files/download/" + DOCUMENT_SUB_DIR + "/" + encodedFileName;
                    fileUrls.add(fileUrl);
                }

                // 6. 多个文件URL用逗号分隔存入document字段
                bug.setDocument(String.join(",", fileUrls));
            }


            // 保存bug信息
            int rows = bugService.addBug(bug);
            return rows > 0 ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error("提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/bugAll")
    public Result bugAll() {
        List<Bug> bugList = bugService.bugAll();
        return Result.success(bugList);
    }

    /**
     * 用户自己的查询
     */
    @PostMapping("/query")
    public Result getBugsUserId(@RequestBody Bug bug) {
        Integer pageNum = bug.getPageNum() == null ? 1 : bug.getPageNum();
        Integer pageSize = bug.getPageSize() == null ? 10 : bug.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        System.out.println(bug.getUser_id() + bug.getTitle());
        if (bug.getTitle() == null) {
            List<Bug> bugList1 = bugService.getBugs_all_title(bug.getUser_id());
            System.out.println(bugList1);
            PageInfo<Bug> pageInfo = new PageInfo<>(bugList1);
            return Result.success(pageInfo);
        } else {
            List<Bug> bugList2 = bugService.getBugsUserId(bug.getUser_id(), bug.getTitle());
            PageInfo<Bug> pageInfo = new PageInfo<>(bugList2);
            return Result.success(pageInfo);
        }
    }

    /**
     * 查询单个bug
     */
    @GetMapping("/onebug/{id}")
    public Result query_one_bug(@PathVariable Integer id) {
        Bug onebug = bugService.query_one_bug(id);
        return Result.success(onebug);
    }

    /**
     * 编辑（支持多文件上传，保留原有附件）
     */
    @PutMapping(value = "/edit", consumes = "multipart/form-data")
    public Result updateBug(
            @ModelAttribute Bug bug,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "oldDocument", required = false) String oldDocument) {
        try {
            System.out.println(bug);
            if (bug.getId() == null) {
                return Result.fail("编辑失败：bug ID不能为空");
            }

            // 处理文件上传：合并原有附件和新上传的文件
            List<String> allFileUrls = new ArrayList<>();
            
            // 1. 保留原有附件（如果有）
            if (oldDocument != null && !oldDocument.trim().isEmpty()) {
                String[] oldUrls = oldDocument.split(",");
                for (String url : oldUrls) {
                    if (url != null && !url.trim().isEmpty()) {
                        allFileUrls.add(url.trim());
                    }
                }
            }
            
            // 2. 处理新上传的文件
            if (files != null && files.length > 0) {
                // 构建document文件夹路径：basePath/document_files
                File documentDir = new File(basePath, DOCUMENT_SUB_DIR);
                // 确保文件夹存在（不存在则创建）
                if (!documentDir.exists()) {
                    documentDir.mkdirs(); // 递归创建目录
                }

                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        continue; // 跳过空文件
                    }

                    // 1. 校验文件类型
                    String contentType = file.getContentType();
                    boolean isAllowed = false;
                    for (String type : ALLOWED_TYPES) {
                        if (type.equals(contentType)) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        throw new RuntimeException("不支持的文件类型！仅支持图片(jpg/jpeg/png/gif/bmp/webp)和视频(mp4/avi/mpeg/quicktime/webm/ogg)");
                    }

                    // 2. 生成唯一文件名（时间戳+原文件名，防止重名）
                    String originalFilename = file.getOriginalFilename();
                    String fileName = System.currentTimeMillis() + "_" + originalFilename;

                    // 3. 构建完整存储路径：basePath/document_files/文件名
                    File storageFile = new File(documentDir, fileName);

                    // 4. 保存文件到document文件夹
                    file.transferTo(storageFile);

                    // 5. 生成文件访问URL（包含document_files子路径，与FileController下载接口匹配）
                    // 对文件名进行URL编码，支持中文和特殊符号
                    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                    String fileUrl = "http://localhost:8080/files/download/" + DOCUMENT_SUB_DIR + "/" + encodedFileName;
                    allFileUrls.add(fileUrl);
                }
            }

            // 3. 合并所有文件URL（原有的 + 新上传的）
            if (!allFileUrls.isEmpty()) {
                bug.setDocument(String.join(",", allFileUrls));
            } else {
                // 如果没有任何附件，设置为空字符串
                bug.setDocument("");
            }

            int rows = bugService.updateBug(bug);
            return  Result.ok("编辑成功") ;
        } catch (Exception e) {
            return Result.error("编辑失败：" + e.getMessage());
        }
    }


    /**
     * 单个删除
     */
    @DeleteMapping("/deleteId/{id}")
    public Result deleteById(@PathVariable Integer id) {
        int rows = bugService.deleteId(id);
        return rows > 0 ? Result.ok("删除成功") : Result.fail("删除失败");
    }

    /**
     * 多个删除
     */
    @DeleteMapping("/deleteIds")
    public Result DeleteBatch(@RequestBody List<Integer> ids) {
        bugService.deleteBatch(ids); // 批量删除
        return Result.ok("批量删除成功");
    }

    /**
     * 用户和总bug图表
     */
    @GetMapping("/lineData")
    public Result getLineData(@RequestParam(value = "user_id", required = false) Integer userId) {
        Map<String, Object> map = new HashMap<>();
        Date today = new Date();
        // 计算近7天日期范围（含今天）
        DateTime startDate = DateUtil.offsetDay(today, -6); // 修正为-6确保返回7天数据
        List<DateTime> dateTimeList = DateUtil.rangeToList(startDate, today, DateField.DAY_OF_YEAR);

        // 处理x轴日期（格式：MM月dd日）
        List<String> xAxisDates = dateTimeList.stream()
                .map(dateTime -> DateUtil.format(dateTime, "MM月dd日"))
                .collect(Collectors.toList()); // 天然有序，无需额外排序
        map.put("date", xAxisDates);

        // 处理y轴数据（根据是否有userId区分个人/总数据）
        List<Integer> countList = new ArrayList<>();
        for (DateTime dateTime : dateTimeList) {
            String dbDate = DateUtil.formatDate(dateTime); // 数据库查询用日期格式（如yyyy-MM-dd）
            Integer count;
            if (userId != null) {
                // 若传入user_id，查询个人数据
                count = bugService.getUserLineData(userId, dbDate);
            } else {
                // 若未传入user_id，查询总数据
                count = bugService.getLineData(dbDate);
            }
            countList.add(count);
        }
        map.put("count", countList);

        return Result.success(map);
    }


    /**
     * =各个用户的权限
     */


}