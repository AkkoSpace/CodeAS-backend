package space.akko.platform.audit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.annotation.RequirePermission;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.Result;
import space.akko.platform.audit.model.request.AuditLogQueryRequest;
import space.akko.platform.audit.model.vo.AuditLogVO;
import space.akko.platform.audit.service.AuditLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "审计日志", description = "审计日志管理相关接口")
@RestController
@RequestMapping("/api/audit/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "分页查询审计日志", description = "根据条件分页查询审计日志")
    @GetMapping
    @RequirePermission("AUDIT_LOG_LIST")
    @OperationLog(operationType = "QUERY", operationName = "分页查询审计日志", resourceType = "AUDIT_LOG")
    public Result<PageResult<AuditLogVO>> getAuditLogPage(AuditLogQueryRequest request) {
        PageResult<AuditLogVO> result = auditLogService.getAuditLogPage(request);
        return Result.success(result);
    }

    @Operation(summary = "获取审计日志详情", description = "根据日志ID获取审计日志详细信息")
    @GetMapping("/{logId}")
    @RequirePermission("AUDIT_LOG_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取审计日志详情", resourceType = "AUDIT_LOG")
    public Result<AuditLogVO> getAuditLogById(
            @Parameter(description = "日志ID", required = true) @PathVariable Long logId) {
        AuditLogVO auditLog = auditLogService.getAuditLogById(logId);
        return Result.success(auditLog);
    }

    @Operation(summary = "根据追踪ID查询日志", description = "根据追踪ID查询相关的所有审计日志")
    @GetMapping("/trace/{traceId}")
    @RequirePermission("AUDIT_LOG_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "根据追踪ID查询日志", resourceType = "AUDIT_LOG")
    public Result<List<AuditLogVO>> getAuditLogsByTraceId(
            @Parameter(description = "追踪ID", required = true) @PathVariable String traceId) {
        List<AuditLogVO> auditLogs = auditLogService.getAuditLogsByTraceId(traceId);
        return Result.success(auditLogs);
    }

    @Operation(summary = "根据用户查询日志", description = "根据用户ID查询用户的操作日志")
    @GetMapping("/user/{userId}")
    @RequirePermission("AUDIT_LOG_LIST")
    @OperationLog(operationType = "QUERY", operationName = "根据用户查询日志", resourceType = "AUDIT_LOG")
    public Result<PageResult<AuditLogVO>> getAuditLogsByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            AuditLogQueryRequest request) {
        PageResult<AuditLogVO> result = auditLogService.getAuditLogsByUserId(userId, request);
        return Result.success(result);
    }

    @Operation(summary = "根据资源查询日志", description = "根据资源类型和ID查询相关的操作日志")
    @GetMapping("/resource")
    @RequirePermission("AUDIT_LOG_LIST")
    @OperationLog(operationType = "QUERY", operationName = "根据资源查询日志", resourceType = "AUDIT_LOG")
    public Result<PageResult<AuditLogVO>> getAuditLogsByResource(
            @Parameter(description = "资源类型", required = true) @RequestParam String resourceType,
            @Parameter(description = "资源ID", required = true) @RequestParam String resourceId,
            AuditLogQueryRequest request) {
        PageResult<AuditLogVO> result = auditLogService.getAuditLogsByResource(resourceType, resourceId, request);
        return Result.success(result);
    }

    @Operation(summary = "删除审计日志", description = "根据日志ID删除审计日志")
    @DeleteMapping("/{logId}")
    @RequirePermission("AUDIT_LOG_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "删除审计日志", resourceType = "AUDIT_LOG")
    public Result<Void> deleteAuditLog(
            @Parameter(description = "日志ID", required = true) @PathVariable Long logId) {
        auditLogService.deleteAuditLog(logId);
        return Result.success("审计日志删除成功");
    }

    @Operation(summary = "批量删除审计日志", description = "根据日志ID列表批量删除审计日志")
    @DeleteMapping("/batch")
    @RequirePermission("AUDIT_LOG_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "批量删除审计日志", resourceType = "AUDIT_LOG")
    public Result<Void> batchDeleteAuditLogs(
            @Parameter(description = "日志ID列表", required = true) @RequestBody List<Long> logIds) {
        auditLogService.batchDeleteAuditLogs(logIds);
        return Result.success("审计日志批量删除成功");
    }

    @Operation(summary = "清理过期日志", description = "清理指定天数之前的审计日志")
    @PostMapping("/clean")
    @RequirePermission("AUDIT_LOG_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "清理过期日志", resourceType = "AUDIT_LOG")
    public Result<Void> cleanExpiredAuditLogs(
            @Parameter(description = "保留天数", required = true) @RequestParam int retentionDays) {
        auditLogService.cleanExpiredAuditLogs(retentionDays);
        return Result.success("过期审计日志清理成功");
    }

    @Operation(summary = "获取审计统计", description = "获取指定时间范围内的审计日志统计信息")
    @GetMapping("/statistics")
    @RequirePermission("AUDIT_LOG_STATISTICS")
    @OperationLog(operationType = "QUERY", operationName = "获取审计统计", resourceType = "AUDIT_LOG")
    public Result<Map<String, Object>> getAuditLogStatistics(
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        Map<String, Object> statistics = auditLogService.getAuditLogStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    @Operation(summary = "获取操作类型统计", description = "获取操作类型的统计数据")
    @GetMapping("/statistics/operation-type")
    @RequirePermission("AUDIT_LOG_STATISTICS")
    public Result<Map<String, Long>> getOperationTypeStatistics(
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        Map<String, Long> statistics = auditLogService.getOperationTypeStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    @Operation(summary = "获取用户操作统计", description = "获取用户操作的统计数据")
    @GetMapping("/statistics/user-operation")
    @RequirePermission("AUDIT_LOG_STATISTICS")
    public Result<Map<String, Long>> getUserOperationStatistics(
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        Map<String, Long> statistics = auditLogService.getUserOperationStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    @Operation(summary = "获取慢操作列表", description = "获取执行时间较长的操作列表")
    @GetMapping("/slow-operations")
    @RequirePermission("AUDIT_LOG_STATISTICS")
    public Result<List<AuditLogVO>> getSlowOperations(
            @Parameter(description = "最小执行时间（毫秒）") @RequestParam(defaultValue = "1000") Long minExecutionTime,
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "100") int limit) {
        List<AuditLogVO> slowOperations = auditLogService.getSlowOperations(minExecutionTime, limit);
        return Result.success(slowOperations);
    }

    @Operation(summary = "导出审计日志", description = "根据查询条件导出审计日志")
    @PostMapping("/export")
    @RequirePermission("AUDIT_LOG_EXPORT")
    @OperationLog(operationType = "EXPORT", operationName = "导出审计日志", resourceType = "AUDIT_LOG")
    public Result<String> exportAuditLogs(@RequestBody AuditLogQueryRequest request) {
        String exportData = auditLogService.exportAuditLogs(request);
        return Result.success("审计日志导出成功", exportData);
    }

    @Operation(summary = "生成审计报告", description = "生成指定时间范围的审计报告")
    @GetMapping("/report")
    @RequirePermission("AUDIT_LOG_REPORT")
    @OperationLog(operationType = "QUERY", operationName = "生成审计报告", resourceType = "AUDIT_LOG")
    public Result<Map<String, Object>> generateAuditReport(
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        Map<String, Object> report = auditLogService.generateAuditReport(startTime, endTime);
        return Result.success(report);
    }
}
