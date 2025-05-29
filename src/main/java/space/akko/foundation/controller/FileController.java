package space.akko.foundation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.common.Result;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.exception.BusinessException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "文件管理", description = "文件上传下载相关接口")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${platform.file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${platform.file.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${platform.file.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx}")
    private String allowedTypes;

    /**
     * 允许的文件类型
     */
    private List<String> getAllowedTypes() {
        return Arrays.asList(allowedTypes.split(","));
    }

    @Operation(summary = "上传文件", description = "上传单个文件")
    @PostMapping("/upload")
    @OperationLog(operationType = "UPLOAD", operationName = "上传文件", resourceType = "FILE")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "上传的文件", required = true) @RequestParam("file") MultipartFile file) {
        
        try {
            // 验证文件
            validateFile(file);
            
            // 生成文件名和路径
            String originalFilename = file.getOriginalFilename();
            String fileExtension = FileUtil.extName(originalFilename);
            String fileName = IdUtil.fastSimpleUUID() + "." + fileExtension;
            
            // 按日期创建目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = dateDir + "/" + fileName;
            String fullPath = uploadPath + "/" + relativePath;
            
            // 创建目录
            File targetFile = new File(fullPath);
            FileUtil.mkParentDirs(targetFile);
            
            // 保存文件
            file.transferTo(targetFile);
            
            // 构建响应
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("originalName", originalFilename);
            fileInfo.put("fileName", fileName);
            fileInfo.put("filePath", relativePath);
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("fileType", fileExtension);
            fileInfo.put("uploadTime", System.currentTimeMillis());
            
            log.info("文件上传成功: {} -> {}", originalFilename, relativePath);
            return Result.success("文件上传成功", fileInfo);
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED, "文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量上传文件", description = "批量上传多个文件")
    @PostMapping("/batch-upload")
    @OperationLog(operationType = "UPLOAD", operationName = "批量上传文件", resourceType = "FILE")
    public Result<Map<String, Object>> batchUploadFiles(
            @Parameter(description = "上传的文件列表", required = true) @RequestParam("files") MultipartFile[] files) {
        
        if (files == null || files.length == 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要上传的文件");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", files.length);
        result.put("success", 0);
        result.put("failed", 0);
        result.put("files", new HashMap<String, Object>());
        
        for (MultipartFile file : files) {
            try {
                Result<Map<String, Object>> uploadResult = uploadFile(file);
                if (uploadResult.isSuccess()) {
                    ((Map<String, Object>) result.get("files")).put(file.getOriginalFilename(), uploadResult.getData());
                    result.put("success", (Integer) result.get("success") + 1);
                } else {
                    ((Map<String, Object>) result.get("files")).put(file.getOriginalFilename(), 
                        Map.of("error", uploadResult.getMessage()));
                    result.put("failed", (Integer) result.get("failed") + 1);
                }
            } catch (Exception e) {
                ((Map<String, Object>) result.get("files")).put(file.getOriginalFilename(), 
                    Map.of("error", e.getMessage()));
                result.put("failed", (Integer) result.get("failed") + 1);
            }
        }
        
        return Result.success("批量上传完成", result);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要上传的文件");
        }
        
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED, 
                String.format("文件大小超出限制，最大允许 %d MB", maxFileSize / 1024 / 1024));
        }
        
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件名不能为空");
        }
        
        String fileExtension = FileUtil.extName(originalFilename).toLowerCase();
        if (!getAllowedTypes().contains(fileExtension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED, 
                String.format("不支持的文件类型: %s，允许的类型: %s", fileExtension, allowedTypes));
        }
    }
}
