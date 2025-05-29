package space.akko.platform.audit.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.audit.model.dto.AuditLogDTO;
import space.akko.platform.audit.model.entity.AuditOperationLog;
import space.akko.platform.audit.model.request.AuditLogQueryRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface AuditLogRepository extends BaseMapper<AuditOperationLog> {

    /**
     * 分页查询审计日志
     */
    IPage<AuditLogDTO> selectAuditLogPage(Page<AuditLogDTO> page, @Param("query") AuditLogQueryRequest query);

    /**
     * 根据追踪ID查询审计日志
     */
    List<AuditLogDTO> selectAuditLogsByTraceId(@Param("traceId") String traceId);

    /**
     * 根据用户ID查询审计日志
     */
    IPage<AuditLogDTO> selectAuditLogsByUserId(Page<AuditLogDTO> page, 
                                              @Param("userId") Long userId, 
                                              @Param("query") AuditLogQueryRequest query);

    /**
     * 根据资源查询审计日志
     */
    IPage<AuditLogDTO> selectAuditLogsByResource(Page<AuditLogDTO> page,
                                                @Param("resourceType") String resourceType,
                                                @Param("resourceId") String resourceId,
                                                @Param("query") AuditLogQueryRequest query);

    /**
     * 清理过期的审计日志
     */
    int deleteExpiredAuditLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 获取操作类型统计
     */
    List<Map<String, Object>> selectOperationTypeStatistics(@Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户操作统计
     */
    List<Map<String, Object>> selectUserOperationStatistics(@Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime,
                                                           @Param("limit") Integer limit);

    /**
     * 获取资源操作统计
     */
    List<Map<String, Object>> selectResourceOperationStatistics(@Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime,
                                                               @Param("limit") Integer limit);

    /**
     * 获取IP访问统计
     */
    List<Map<String, Object>> selectIpAccessStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("limit") Integer limit);

    /**
     * 获取错误操作统计
     */
    List<Map<String, Object>> selectErrorOperationStatistics(@Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取慢操作列表
     */
    List<AuditLogDTO> selectSlowOperations(@Param("minExecutionTime") Long minExecutionTime,
                                         @Param("limit") Integer limit);

    /**
     * 获取审计日志趋势数据
     */
    List<Map<String, Object>> selectAuditLogTrend(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("granularity") String granularity);

    /**
     * 检查用户操作频率
     */
    int countUserOperations(@Param("userId") Long userId,
                           @Param("operationType") String operationType,
                           @Param("afterTime") LocalDateTime afterTime);

    /**
     * 检测异常操作
     */
    List<AuditLogDTO> selectAbnormalOperations(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 获取审计日志总体统计
     */
    Map<String, Object> selectAuditLogOverallStatistics(@Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);
}
