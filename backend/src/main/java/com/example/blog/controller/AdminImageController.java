package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 后台图片管理接口。
 *
 * 这次把图片分成两类：
 * 1. cover   封面图片：用于商品卡片、故事卡片、详情页顶部封面。
 * 2. content 正文插图：用于 Markdown 正文中的插图。
 *
 * 磁盘目录：
 * frontend/public/asset/images/covers
 * frontend/public/asset/images/content
 *
 * 前端访问路径：
 * /asset/images/covers/xxx.jpg
 * /asset/images/content/xxx.jpg
 *
 * 注意：
 * 不要把 Windows 绝对路径保存进数据库。数据库只保存 /asset/images/... 这种相对访问路径。
 */
@RestController
@RequestMapping("/api/admin/images")
public class AdminImageController {

    /**
     * 图片根目录。
     *
     * 本地默认：../frontend/public/asset/images
     * 生产示例：/data/farm-products/uploads/images
     */
    @Value("${app.upload.image-root-dir:${app.upload.image-dir:../frontend/public/asset/images}}")
    private String imageRootDir;

    /** 封面图片 URL 前缀。 */
    private static final String COVER_URL_PREFIX = "/asset/images/covers/";

    /** 正文插图 URL 前缀。 */
    private static final String CONTENT_URL_PREFIX = "/asset/images/content/";

    /** 允许上传和展示的图片后缀。 */
    private static final Set<String> ALLOWED_EXT = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "webp", "gif", "svg"
    ));

    /** 上传图片最大宽度。 */
    private static final int MAX_WIDTH = 1000;

    /** 上传图片最大高度。 */
    private static final int MAX_HEIGHT = 1000;

    /**
     * 查询图片列表。
     *
     * @param type cover 表示查询封面图；content 表示查询正文插图；默认 cover。
     */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listImages(@RequestParam(defaultValue = "cover") String type) throws Exception {
        ImageType imageType = parseType(type);
        Path dir = getUploadDir(imageType);
        Files.createDirectories(dir);

        List<Map<String, Object>> result = new ArrayList<>();
        File[] files = dir.toFile().listFiles();
        if (files == null) {
            return ApiResponse.ok(result);
        }

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            String fileName = file.getName();
            String ext = getExt(fileName).toLowerCase();
            if (!ALLOWED_EXT.contains(ext)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("name", fileName);
            item.put("path", getUrlPrefix(imageType) + fileName);
            item.put("type", imageType.value);
            item.put("size", file.length());
            result.add(item);
        }

        result.sort((a, b) -> String.valueOf(a.get("name")).compareTo(String.valueOf(b.get("name"))));
        return ApiResponse.ok(result);
    }

    /**
     * 上传图片。
     *
     * @param type cover 表示上传封面图；content 表示上传正文插图；默认 cover。
     */
    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "cover") String type
    ) throws Exception {
        ImageType imageType = parseType(type);

        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            return ApiResponse.fail(400, "文件名不能为空");
        }

        String ext = getExt(originalFilename).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            return ApiResponse.fail(400, "只支持 jpg、jpeg、png、webp、gif、svg 图片");
        }

        int width = 0;
        int height = 0;

        // SVG 是矢量图，ImageIO 默认读取不了宽高，所以不做尺寸校验。
        if (!"svg".equals(ext)) {
            try (InputStream inputStream = file.getInputStream()) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    return ApiResponse.fail(400, "上传文件不是有效图片，建议使用 jpg、jpeg、png、gif 或 svg");
                }

                width = image.getWidth();
                height = image.getHeight();

                if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                    return ApiResponse.fail(400, "上传失败：图片尺寸不能超过 1000 × 1000 px，当前图片为 " + width + " × " + height + " px");
                }
            }
        }

        Path uploadDir = getUploadDir(imageType);
        Files.createDirectories(uploadDir);

        String safeName = buildSafeFileName(imageType, ext);
        Path targetPath = uploadDir.resolve(safeName).normalize();

        if (!targetPath.startsWith(uploadDir)) {
            return ApiResponse.fail(400, "非法上传路径");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", safeName);
        data.put("path", getUrlPrefix(imageType) + safeName);
        data.put("type", imageType.value);
        data.put("width", width);
        data.put("height", height);
        data.put("size", file.getSize());
        return ApiResponse.ok(data);
    }

    /**
     * 删除图片。
     */
    @DeleteMapping
    public ApiResponse<Void> delete(
            @RequestParam("name") String name,
            @RequestParam(defaultValue = "cover") String type
    ) throws Exception {
        ImageType imageType = parseType(type);

        if (!StringUtils.hasText(name)) {
            return ApiResponse.fail(400, "图片名称不能为空");
        }

        // 只允许按文件名删除，不允许传入目录，避免路径穿越。
        if (name.contains("/") || name.contains("\\") || name.contains("..")) {
            return ApiResponse.fail(400, "非法文件名");
        }

        Path uploadDir = getUploadDir(imageType);
        Path targetPath = uploadDir.resolve(name).normalize();
        if (!targetPath.startsWith(uploadDir)) {
            return ApiResponse.fail(400, "非法删除路径");
        }

        Files.deleteIfExists(targetPath);
        return ApiResponse.ok();
    }

    /** 获取某类图片的真实保存目录。 */
    private Path getUploadDir(ImageType type) {
        Path configuredPath = Paths.get(imageRootDir);
        Path rootPath;

        if (configuredPath.isAbsolute()) {
            rootPath = configuredPath.toAbsolutePath().normalize();
        } else {
            Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
            rootPath = userDir.resolve(configuredPath).normalize();
        }

        return rootPath.resolve(type.dirName).normalize();
    }

    /** 根据图片类型返回 URL 前缀。 */
    private String getUrlPrefix(ImageType type) {
        return type == ImageType.CONTENT ? CONTENT_URL_PREFIX : COVER_URL_PREFIX;
    }

    /** 解析图片类型。 */
    private ImageType parseType(String type) {
        if ("content".equalsIgnoreCase(type)) {
            return ImageType.CONTENT;
        }
        return ImageType.COVER;
    }

    /** 获取文件后缀，不带点。 */
    private String getExt(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    /** 生成安全文件名。 */
    private String buildSafeFileName(ImageType type, String ext) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return type.filePrefix + "-" + time + "-" + uuid + "." + ext;
    }

    /** 图片类型枚举。 */
    private enum ImageType {
        COVER("cover", "covers", "cover"),
        CONTENT("content", "content", "content");

        private final String value;
        private final String dirName;
        private final String filePrefix;

        ImageType(String value, String dirName, String filePrefix) {
            this.value = value;
            this.dirName = dirName;
            this.filePrefix = filePrefix;
        }
    }
}
