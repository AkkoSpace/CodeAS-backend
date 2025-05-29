package space.akko.foundation.constant;

/**
 * 安全相关常量
 * 
 * @author akko
 * @since 1.0.0
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * JWT令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT令牌请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌类型 - 访问令牌
     */
    public static final String ACCESS_TOKEN = "ACCESS";

    /**
     * JWT令牌类型 - 刷新令牌
     */
    public static final String REFRESH_TOKEN = "REFRESH";

    /**
     * 用户ID声明
     */
    public static final String USER_ID_CLAIM = "userId";

    /**
     * 用户名声明
     */
    public static final String USERNAME_CLAIM = "username";

    /**
     * 角色声明
     */
    public static final String ROLES_CLAIM = "roles";

    /**
     * 权限声明
     */
    public static final String AUTHORITIES_CLAIM = "authorities";

    /**
     * 令牌类型声明
     */
    public static final String TOKEN_TYPE_CLAIM = "tokenType";

    /**
     * 会话ID声明
     */
    public static final String SESSION_ID_CLAIM = "sessionId";

    /**
     * 密码加密算法
     */
    public static final String PASSWORD_ENCODER = "bcrypt";

    /**
     * 默认密码强度
     */
    public static final int DEFAULT_PASSWORD_STRENGTH = 10;

    /**
     * 登录失败缓存键前缀
     */
    public static final String LOGIN_FAIL_CACHE_KEY = "login:fail:";

    /**
     * 用户会话缓存键前缀
     */
    public static final String USER_SESSION_CACHE_KEY = "user:session:";

    /**
     * 用户权限缓存键前缀
     */
    public static final String USER_PERMISSION_CACHE_KEY = "user:permission:";

    /**
     * 令牌黑名单缓存键前缀
     */
    public static final String TOKEN_BLACKLIST_CACHE_KEY = "token:blacklist:";

    /**
     * 验证码缓存键前缀
     */
    public static final String CAPTCHA_CACHE_KEY = "captcha:";

    /**
     * 默认会话超时时间（秒）
     */
    public static final long DEFAULT_SESSION_TIMEOUT = 1800L;

    /**
     * 默认令牌过期时间（秒）
     */
    public static final long DEFAULT_TOKEN_EXPIRATION = 3600L;

    /**
     * 默认刷新令牌过期时间（秒）
     */
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 86400L;

    /**
     * 最大登录失败次数
     */
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * 账户锁定时间（秒）
     */
    public static final long ACCOUNT_LOCK_DURATION = 900L;

    /**
     * 匿名用户
     */
    public static final String ANONYMOUS_USER = "anonymous";

    /**
     * 系统用户
     */
    public static final String SYSTEM_USER = "system";

    /**
     * 权限分隔符
     */
    public static final String AUTHORITY_SEPARATOR = ",";

    /**
     * 角色前缀
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 权限前缀
     */
    public static final String AUTHORITY_PREFIX = "AUTH_";
}
