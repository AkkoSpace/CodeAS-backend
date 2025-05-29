package space.akko.foundation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.akko.foundation.common.Result;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息控制器
 *
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "系统信息", description = "系统信息相关接口")
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Value("${spring.application.name:backend}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Operation(summary = "获取系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();

        systemInfo.put("applicationName", applicationName);
        systemInfo.put("version", "1.0.0");
        systemInfo.put("activeProfile", activeProfile);
        systemInfo.put("serverTime", LocalDateTime.now());
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArch", System.getProperty("os.arch"));

        return Result.success(systemInfo);
    }

    @Operation(summary = "获取服务器时间", description = "获取服务器当前时间")
    @GetMapping("/time")
    public Result<LocalDateTime> getServerTime() {
        return Result.success(LocalDateTime.now());
    }

    @Operation(summary = "获取版本信息", description = "获取应用版本信息")
    @GetMapping("/version")
    public Result<Map<String, String>> getVersion() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("version", "0.0.1");
        versionInfo.put("buildTime", "2025-05-28 21:00:00");
        versionInfo.put("gitCommit", "latest");
        versionInfo.put("gitBranch", "main");

        return Result.success(versionInfo);
    }
}
