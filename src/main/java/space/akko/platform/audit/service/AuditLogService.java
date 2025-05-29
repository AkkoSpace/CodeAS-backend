package space.akko.platform.audit.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.audit.model.dto.AuditLogDTO;
import space.akko.platform.audit.model.entity.AuditOperationLog;
import space.akko.platform.audit.model.request.AuditLogQueryRequest;
import space.akko.platform.audit.model.vo.AuditLogVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface AuditLogService {

    /**
     * 记录操作日志
     */
    void recordOperationLog(AuditOperationLog operationLog);

    /**
     * 异步记录操作日志
     */
    void recordOperationLogAsync(AuditOperationLog operationLog);

    /**
     * 根据ID获取审计日志
     */
    AuditLogVO getAuditLogById(Long logId);

    /**
     * 分页查询审计日志
     */
    PageResult<AuditLogVO> getAuditLogPage(AuditLogQueryRequest request);

    /**
     * 根据追踪ID查询审计日志
     */
    List<AuditLogVO> getAuditLogsByTraceId(String traceId);

    /**
     * 根据用户ID查询审计日志
     */
    PageResult<AuditLogVO> getAuditLogsByUserId(Long userId, AuditLogQueryRequest request);

    /**
     * 根据资源查询审计日志
     */
    PageResult<AuditLogVO> getAuditLogsByResource(String resourceType, String resourceId, 
                                                 AuditLogQueryRequest request);

    /**
     * 删除审计日志
     */
    void deleteAuditLog(Long logId);

    /**
     * 批量删除审计日志
     */
    void batchDeleteAuditLogs(List<Long> logIds);

    /**
     * 清理过期的审计日志
     */
    void cleanExpiredAuditLogs(int retentionDays);

    /**
     * 获取审计日志统计信息
     */
    Map<String, Object> getAuditLogStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取操作类型统计
     */
    Map<String, Long> getOperationTypeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户操作统计
     */
    Map<String, Long> getUserOperationStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取资源操作统计
     */
    Map<String, Long> getResourceOperationStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取IP访问统计
     */
    Map<String, Long> getIpAccessStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取错误操作统计
     */
    Map<String, Long> getErrorOperationStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取慢操作统计
     */
    List<AuditLogVO> getSlowOperations(Long minExecutionTime, int limit);

    /**
     * 导出审计日志
     */
    String exportAuditLogs(AuditLogQueryRequest request);

    /**
     * 获取审计日志趋势
     */
    Map<String, Object> getAuditLogTrend(LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 检查用户操作频率
     */
    boolean checkUserOperationFrequency(Long userId, String operationType, int maxCount, int timeWindowMinutes);

    /**
     * 获取异常操作检测
     */
    List<AuditLogVO> detectAbnormalOperations(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成审计报告
     */
    Map<String, Object> generateAuditReport(LocalDateTime startTime, LocalDateTime endTime);
}
