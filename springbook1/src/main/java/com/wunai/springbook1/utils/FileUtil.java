package com.wunai.springbook1.utils;//package com.wunai.springbook1.utils;
//
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.UUID;
//
//public class FileUploadUtil {
//
//    /**
//     * 处理上传文件（视频/图片）
//     * @param basePath 基础存储路径（从配置文件获取）
//     * @param file 上传的文件
//     * @param allowedMimeTypes 允许的MIME类型（视频/图片分别定义）
//     * @return 相对存储路径（用于拼接访问URL）
//     * @throws IOException 存储失败时抛出
//     */
//    public static String uploadFile(String basePath, MultipartFile file, String[] allowedMimeTypes) throws IOException {
//        // 1. 校验文件是否为空
//        if (file.isEmpty()) {
//            throw new RuntimeException("上传文件不能为空");
//        }
//
//        // 2. 校验文件类型（MIME类型）
//        String fileMimeType = file.getContentType();
//        boolean isAllowed = false;
//        for (String allowedType : allowedMimeTypes) {
//            if (allowedType.equals(fileMimeType)) {
//                isAllowed = true;
//                break;
//            }
//        }
//        if (!isAllowed) {
//            throw new RuntimeException("不支持的文件类型！允许的类型：" + String.join(", ", allowedMimeTypes));
//        }
//
//        // 3. 生成唯一文件名（避免重复覆盖）
//        String originalFilename = file.getOriginalFilename();
//        String fileSuffix = null; // 如 .mp4 .png
//        if (originalFilename != null) {
//            fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
//        String uniqueFileName = UUID.randomUUID().toString() + fileSuffix;
//
//        // 4. 按日期分目录存储（便于管理，如 2024/11/10/xxx.mp4）
//        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//        File dateDirFile = new File(basePath + dateDir);
//        if (!dateDirFile.exists()) {
//            dateDirFile.mkdirs(); // 递归创建目录
//        }
//
//        // 5. 保存文件到磁盘
//        File destFile = new File(dateDirFile, uniqueFileName);
//        file.transferTo(destFile); // 大文件会自动写入磁盘（基于配置的threshold）
//
//        // 6. 返回相对路径（用于拼接访问URL）
//        return dateDir + "/" + uniqueFileName;
//    }
//}
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作工具类
 * 包含文件/目录的创建、复制、移动、删除、读写等常用操作
 */
public final class FileUtil {

    // 私有构造方法，禁止实例化
    private FileUtil() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 创建文件（若父目录不存在则自动创建）
     *
     * @param filePath 文件路径
     * @return 创建成功返回true，已存在返回true，失败返回false
     */
    public static boolean createFile(String filePath) {
        if (isFileExists(filePath)) {
            return true;
        }
        File file = new File(filePath);
        // 创建父目录
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建目录（支持多级目录）
     *
     * @param dirPath 目录路径
     * @return 创建成功返回true，已存在返回true，失败返回false
     */
    public static boolean createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException 复制失败时抛出异常
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        if (!isFileExists(sourcePath)) {
            throw new FileNotFoundException("源文件不存在: " + sourcePath);
        }
        // 确保目标文件父目录存在
        createDir(new File(targetPath).getParent());

        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);
        // 使用NIO复制，自动处理大文件
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 复制目录（包含子目录和文件）
     *
     * @param sourceDir 源目录路径
     * @param targetDir 目标目录路径
     * @throws IOException 复制失败时抛出异常
     */
    public static void copyDir(String sourceDir, String targetDir) throws IOException {
        if (!isDirExists(sourceDir)) {
            throw new FileNotFoundException("源目录不存在: " + sourceDir);
        }

        File source = new File(sourceDir);
        File target = new File(targetDir);
        // 创建目标目录
        if (!target.exists()) {
            target.mkdirs();
        }

        // 遍历源目录文件
        File[] files = source.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            File targetFile = new File(target, file.getName());
            if (file.isFile()) {
                // 复制文件
                copyFile(file.getPath(), targetFile.getPath());
            } else if (file.isDirectory()) {
                // 递归复制子目录
                copyDir(file.getPath(), targetFile.getPath());
            }
        }
    }

    /**
     * 移动文件/目录（支持跨文件系统）
     *
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @throws IOException 移动失败时抛出异常
     */
    public static void move(String sourcePath, String targetPath) throws IOException {
        if (!isFileExists(sourcePath)) {
            throw new FileNotFoundException("源路径不存在: " + sourcePath);
        }

        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);
        // 确保目标父目录存在
        createDir(target.getParent().toString());

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 删除文件/目录（目录会递归删除所有内容）
     *
     * @param path 要删除的路径
     * @return 删除成功返回true，失败返回false
     */
    public static boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            // 递归删除目录内容
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!delete(f.getPath())) {
                        return false;
                    }
                }
            }
            return file.delete();
        }
    }

    /**
     * 读取文件内容（按行读取，UTF-8编码）
     *
     * @param filePath 文件路径
     * @return 文件内容列表（每行一个元素）
     * @throws IOException 读取失败时抛出异常
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        if (!isFileExists(filePath)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容（一次性读取为字符串，UTF-8编码，适合小文件）
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 读取失败时抛出异常（Java 11+支持）
     */
    public static String readString(String filePath) throws IOException {
        if (!isFileExists(filePath)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * 写入内容到文件（覆盖模式，UTF-8编码）
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @throws IOException 写入失败时抛出异常
     */
    public static void writeString(String filePath, String content) throws IOException {
        writeString(filePath, content, false);
    }

    /**
     * 写入内容到文件（支持追加模式，UTF-8编码）
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @param append   是否追加（true:追加，false:覆盖）
     * @throws IOException 写入失败时抛出异常
     */
    public static void writeString(String filePath, String content, boolean append) throws IOException {
        createFile(filePath); // 确保文件存在
        StandardOpenOption option = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, option);
    }

    /**
     * 获取文件大小（字节数）
     *
     * @param filePath 文件路径
     * @return 文件大小（字节），文件不存在返回-1
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() ? file.length() : -1;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 存在且是文件返回true，否则返回false
     */
    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 判断目录是否存在
     *
     * @param dirPath 目录路径
     * @return 存在且是目录返回true，否则返回false
     */
    public static boolean isDirExists(String dirPath) {
        File dir = new File(dirPath);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * 获取文件扩展名（不含点）
     *
     * @param fileName 文件名或文件路径
     * @return 扩展名（如"txt"），无扩展名返回空字符串
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        int lastSepIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (lastDotIndex > lastSepIndex) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }


    /**
     * 获取文件名（不含路径和扩展名）
     *
     * @param filePath 文件路径
     * @return 文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        // 先获取纯文件名（含扩展名）
        String fileName = new File(filePath).getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    /**
     * 列出目录下的所有文件（不含子目录）
     *
     * @param dirPath 目录路径
     * @return 文件路径列表
     */
    public static List<String> listFiles(String dirPath) {
        List<String> fileList = new ArrayList<>();
        if (!isDirExists(dirPath)) {
            return fileList;
        }
        File dir = new File(dirPath);
        File[] files = dir.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                fileList.add(file.getPath());
            }
        }
        return fileList;
    }

    /**
     * 保存头像文件到 basePath，并返回可访问URL
     */
    public static String saveAvatar(MultipartFile file, String basePath) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + (originalFilename == null ? "avatar" : originalFilename);
        File dir = new File(basePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("创建目录失败：" + basePath);
        }
        File dest = new File(dir, fileName);
        file.transferTo(dest);
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return "http://localhost:8080/files/download/" + encoded;
    }
}