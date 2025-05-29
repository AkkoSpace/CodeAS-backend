package space.akko.foundation.constant;

/**
 * 缓存相关常量
 * 
 * @author akko
 * @since 1.0.0
 */
public final class CacheConstants {

    private CacheConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 缓存键分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * 缓存键前缀
     */
    public static final String CACHE_KEY_PREFIX = "backend";

    /**
     * 用户缓存名称
     */
    public static final String USER_CACHE = "user";

    /**
     * 角色缓存名称
     */
    public static final String ROLE_CACHE = "role";

    /**
     * 权限缓存名称
     */
    public static final String PERMISSION_CACHE = "permission";

    /**
     * 菜单缓存名称
     */
    public static final String MENU_CACHE = "menu";

    /**
     * 字典缓存名称
     */
    public static final String DICTIONARY_CACHE = "dictionary";

    /**
     * 配置缓存名称
     */
    public static final String CONFIG_CACHE = "config";

    /**
     * 用户详情缓存键
     */
    public static final String USER_DETAIL_KEY = USER_CACHE + CACHE_KEY_SEPARATOR + "detail";

    /**
     * 用户权限缓存键
     */
    public static final String USER_PERMISSION_KEY = USER_CACHE + CACHE_KEY_SEPARATOR + "permission";

    /**
     * 用户角色缓存键
     */
    public static final String USER_ROLE_KEY = USER_CACHE + CACHE_KEY_SEPARATOR + "role";

    /**
     * 用户菜单缓存键
     */
    public static final String USER_MENU_KEY = USER_CACHE + CACHE_KEY_SEPARATOR + "menu";

    /**
     * 角色权限缓存键
     */
    public static final String ROLE_PERMISSION_KEY = ROLE_CACHE + CACHE_KEY_SEPARATOR + "permission";

    /**
     * 菜单树缓存键
     */
    public static final String MENU_TREE_KEY = MENU_CACHE + CACHE_KEY_SEPARATOR + "tree";

    /**
     * 字典项缓存键
     */
    public static final String DICTIONARY_ITEM_KEY = DICTIONARY_CACHE + CACHE_KEY_SEPARATOR + "item";

    /**
     * 系统配置缓存键
     */
    public static final String SYSTEM_CONFIG_KEY = CONFIG_CACHE + CACHE_KEY_SEPARATOR + "system";

    /**
     * 功能开关缓存键
     */
    public static final String FEATURE_TOGGLE_KEY = CONFIG_CACHE + CACHE_KEY_SEPARATOR + "feature";

    /**
     * 默认缓存过期时间（秒）
     */
    public static final long DEFAULT_CACHE_TTL = 1800L;

    /**
     * 短期缓存过期时间（秒）
     */
    public static final long SHORT_CACHE_TTL = 300L;

    /**
     * 长期缓存过期时间（秒）
     */
    public static final long LONG_CACHE_TTL = 3600L;

    /**
     * 永久缓存过期时间（秒）
     */
    public static final long PERMANENT_CACHE_TTL = -1L;

    /**
     * 缓存空值过期时间（秒）
     */
    public static final long NULL_CACHE_TTL = 60L;

    /**
     * L1缓存名称
     */
    public static final String L1_CACHE_NAME = "l1Cache";

    /**
     * L2缓存名称
     */
    public static final String L2_CACHE_NAME = "l2Cache";

    /**
     * 多级缓存名称
     */
    public static final String MULTI_LEVEL_CACHE_NAME = "multiLevelCache";
}
