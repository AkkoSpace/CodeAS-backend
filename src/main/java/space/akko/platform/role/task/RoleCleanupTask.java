package space.akko.platform.role.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.akko.platform.role.service.RoleService;

/**
 * 角色清理定时任务
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleCleanupTask {

    private final RoleService roleService;

    /**
     * 清理过期的角色关联
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredRoleMappings() {
        try {
            log.info("开始清理过期的角色关联");
            roleService.cleanExpiredRoleMappings();
            log.info("清理过期的角色关联完成");
        } catch (Exception e) {
            log.error("清理过期的角色关联失败", e);
        }
    }
}
