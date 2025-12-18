package com.wunai.springbook1.controller;

import cn.hutool.core.io.FileUtil;
import com.wunai.springbook1.pojo.Result;
import com.wunai.springbook1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件相关的接口
 */
@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FileController {
    @Autowired
    private UserService UserService;
    
    // 从配置文件获取上传基础路径
    @Value("${file.upload.base-path}")
    private String basePath;
    
    // 头像文件存储路径（在 basePath 下）
    private String getAvatarPath() {
        return basePath + "/";
    }


    /**
     * 文件上传
     *
     */

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {  // 文件流的形式接收前端发送过来的文件
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空，请选择文件后上传");
        }
        String originalFilename = file.getOriginalFilename();  // xxx.png
        // 头像文件存储在 basePath 根目录下
        String avatarDir = getAvatarPath();
        if (!FileUtil.isDirectory(avatarDir)) {  // 如果目录不存在 需要先创建目录
            FileUtil.mkdir(avatarDir);  // 创建目录
        }
        // 提供文件存储的完整的路径
        // 给文件名 加一个唯一的标识
        String fileName = System.currentTimeMillis() + "_" + originalFilename;  // 156723232322_xxx.png
        String realPath = avatarDir + fileName;  // 完整的文件路径
        try {
            FileUtil.writeBytes(file.getBytes(),realPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回文件的访问路径（对文件名进行URL编码，支持中文和特殊符号）
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String fileUrl = "http://localhost:8080/files/download/" + encodedFileName;
        // 从ThreadLocal获取当前用户ID并更新头像
        UserService.updateAvatar(fileUrl); // 调用用户服务更新头像

        return Result.success(fileUrl);
//        System.out.println(fileUrl);
//        return Result.success(fileUrl);
    }

    /**
     * 文件下载/预览
     * 支持图片预览和文件下载
     * 支持子路径，如：/files/download/document_files/文件名
     */
    @GetMapping("/download/**")
    public void download(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取请求路径，例如：/files/download/document_files/xxx.jpg 或 /files/download/xxx.jpg
            String requestPath = request.getRequestURI();
            // 提取 /files/download/ 之后的部分
            String pathAfterDownload = requestPath.substring("/files/download/".length());
            
            // URL解码，支持中文和特殊符号
            try {
                pathAfterDownload = URLDecoder.decode(pathAfterDownload, StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                // 如果解码失败，使用原始路径
                // 可能是已经解码过的或者格式不正确
            }
            
            // 构建完整的文件路径
            // 如果包含子路径（如 document_files/），使用配置文件中的 basePath
            // 否则使用 basePath 根目录（头像文件）
            String realPath;
            if (pathAfterDownload.contains("/")) {
                // 包含子路径（如 document_files/xxx.jpg），使用配置文件中的 basePath
                // 实际路径：basePath/document_files/文件名
                realPath = basePath + "/" + pathAfterDownload;
            } else {
                // 直接文件名（头像文件），存储在 basePath 根目录
                realPath = getAvatarPath() + pathAfterDownload;
            }
            
            // 检查文件是否存在
            if (!FileUtil.exist(realPath)) {
                response.setStatus(404);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":404,\"message\":\"文件不存在: " + realPath + "\"}");
                return;
            }
            
            // 从路径中提取文件名（处理子路径情况，如 document_files/xxx.jpg）
            String fileName = pathAfterDownload.substring(pathAfterDownload.lastIndexOf("/") + 1);
            
            // 根据文件扩展名确定 Content-Type
            String contentType = getContentType(fileName);
            response.setContentType(contentType);
            
            // 如果是图片，设置为 inline（浏览器内显示），否则设置为 attachment（下载）
            if (contentType.startsWith("image/")) {
                response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            } else {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            }
            
            // 设置缓存控制，允许浏览器缓存图片
            response.addHeader("Cache-Control", "public, max-age=31536000");
            
            OutputStream os = response.getOutputStream();
            // 获取到文件的字节数组
            byte[] bytes = FileUtil.readBytes(realPath);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 根据文件名获取 Content-Type
     */
    private String getContentType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerFileName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerFileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerFileName.endsWith(".zip")) {
            return "application/zip";
        } else {
            return "application/octet-stream";
        }
    }
}
