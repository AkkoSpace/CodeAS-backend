package space.akko.platform.audit.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.akko.platform.audit.service.AuditLogService;

/**
 * 审计日志清理定时任务
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogCleanupTask {

    private final AuditLogService auditLogService;

    @Value("${platform.audit.log.retention-days:90}")
    private int retentionDays;

    /**
     * 清理过期的审计日志
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredAuditLogs() {
        try {
            log.info("开始清理过期的审计日志，保留天数: {}", retentionDays);
            auditLogService.cleanExpiredAuditLogs(retentionDays);
            log.info("清理过期的审计日志完成");
        } catch (Exception e) {
            log.error("清理过期的审计日志失败", e);
        }
    }
}
