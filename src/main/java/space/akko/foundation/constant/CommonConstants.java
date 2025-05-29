package space.akko.foundation.constant;

/**
 * 通用常量
 * 
 * @author akko
 * @since 1.0.0
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 系统默认编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 1000;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 成功状态
     */
    public static final String SUCCESS = "success";

    /**
     * 失败状态
     */
    public static final String FAIL = "fail";

    /**
     * 是
     */
    public static final String YES = "Y";

    /**
     * 否
     */
    public static final String NO = "N";

    /**
     * 启用
     */
    public static final String ENABLED = "1";

    /**
     * 禁用
     */
    public static final String DISABLED = "0";

    /**
     * 删除标记 - 已删除
     */
    public static final Boolean DELETED = true;

    /**
     * 删除标记 - 未删除
     */
    public static final Boolean NOT_DELETED = false;

    /**
     * 默认排序字段
     */
    public static final String DEFAULT_SORT_FIELD = "created_at";

    /**
     * 升序
     */
    public static final String ASC = "asc";

    /**
     * 降序
     */
    public static final String DESC = "desc";

    /**
     * 超级管理员角色编码
     */
    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    /**
     * 系统管理员角色编码
     */
    public static final String SYSTEM_ADMIN_ROLE = "SYSTEM_ADMIN";

    /**
     * 普通用户角色编码
     */
    public static final String NORMAL_USER_ROLE = "NORMAL_USER";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 系统用户ID
     */
    public static final Long SYSTEM_USER_ID = 0L;

    /**
     * 根节点ID
     */
    public static final Long ROOT_NODE_ID = 0L;

    /**
     * 树形结构根节点父ID
     */
    public static final Long TREE_ROOT_PARENT_ID = -1L;
}
