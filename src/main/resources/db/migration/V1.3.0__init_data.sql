-- 初始化基础数据

-- 插入权限动作
INSERT INTO platform_schema.permission_action (action_code, action_name, description) VALUES
('CREATE', '创建', '创建资源的权限'),
('READ', '查看', '查看资源的权限'),
('UPDATE', '更新', '更新资源的权限'),
('DELETE', '删除', '删除资源的权限'),
('EXPORT', '导出', '导出数据的权限'),
('IMPORT', '导入', '导入数据的权限'),
('APPROVE', '审批', '审批操作的权限'),
('REJECT', '拒绝', '拒绝操作的权限');

-- 插入权限资源
INSERT INTO platform_schema.permission_resource (resource_code, resource_name, resource_type, resource_url, http_method, description) VALUES
-- 用户管理
('USER_MANAGEMENT', '用户管理', 'MENU', '/user', 'GET', '用户管理菜单'),
('USER_LIST', '用户列表', 'API', '/api/users', 'GET', '获取用户列表'),
('USER_CREATE', '创建用户', 'API', '/api/users', 'POST', '创建新用户'),
('USER_UPDATE', '更新用户', 'API', '/api/users/*', 'PUT', '更新用户信息'),
('USER_DELETE', '删除用户', 'API', '/api/users/*', 'DELETE', '删除用户'),
('USER_DETAIL', '用户详情', 'API', '/api/users/*', 'GET', '获取用户详情'),

-- 角色管理
('ROLE_MANAGEMENT', '角色管理', 'MENU', '/role', 'GET', '角色管理菜单'),
('ROLE_LIST', '角色列表', 'API', '/api/roles', 'GET', '获取角色列表'),
('ROLE_CREATE', '创建角色', 'API', '/api/roles', 'POST', '创建新角色'),
('ROLE_UPDATE', '更新角色', 'API', '/api/roles/*', 'PUT', '更新角色信息'),
('ROLE_DELETE', '删除角色', 'API', '/api/roles/*', 'DELETE', '删除角色'),
('ROLE_DETAIL', '角色详情', 'API', '/api/roles/*', 'GET', '获取角色详情'),

-- 权限管理
('PERMISSION_MANAGEMENT', '权限管理', 'MENU', '/permission', 'GET', '权限管理菜单'),
('PERMISSION_LIST', '权限列表', 'API', '/api/permissions', 'GET', '获取权限列表'),
('PERMISSION_CREATE', '创建权限', 'API', '/api/permissions', 'POST', '创建新权限'),
('PERMISSION_UPDATE', '更新权限', 'API', '/api/permissions/*', 'PUT', '更新权限信息'),
('PERMISSION_DELETE', '删除权限', 'API', '/api/permissions/*', 'DELETE', '删除权限'),

-- 菜单管理
('MENU_MANAGEMENT', '菜单管理', 'MENU', '/menu', 'GET', '菜单管理菜单'),
('MENU_LIST', '菜单列表', 'API', '/api/menus', 'GET', '获取菜单列表'),
('MENU_CREATE', '创建菜单', 'API', '/api/menus', 'POST', '创建新菜单'),
('MENU_UPDATE', '更新菜单', 'API', '/api/menus/*', 'PUT', '更新菜单信息'),
('MENU_DELETE', '删除菜单', 'API', '/api/menus/*', 'DELETE', '删除菜单'),

-- 系统配置
('CONFIG_MANAGEMENT', '系统配置', 'MENU', '/config', 'GET', '系统配置菜单'),
('CONFIG_LIST', '配置列表', 'API', '/api/configs', 'GET', '获取配置列表'),
('CONFIG_UPDATE', '更新配置', 'API', '/api/configs/*', 'PUT', '更新系统配置'),

-- 操作日志
('AUDIT_MANAGEMENT', '操作日志', 'MENU', '/audit', 'GET', '操作日志菜单'),
('AUDIT_LIST', '日志列表', 'API', '/api/audits', 'GET', '获取操作日志列表'),
('AUDIT_EXPORT', '导出日志', 'API', '/api/audits/export', 'POST', '导出操作日志'),

-- 数据字典
('DICTIONARY_MANAGEMENT', '数据字典', 'MENU', '/dictionary', 'GET', '数据字典菜单'),
('DICTIONARY_LIST', '字典列表', 'API', '/api/dictionaries', 'GET', '获取字典列表'),
('DICTIONARY_CREATE', '创建字典', 'API', '/api/dictionaries', 'POST', '创建新字典'),
('DICTIONARY_UPDATE', '更新字典', 'API', '/api/dictionaries/*', 'PUT', '更新字典信息'),
('DICTIONARY_DELETE', '删除字典', 'API', '/api/dictionaries/*', 'DELETE', '删除字典');

-- 插入角色
INSERT INTO platform_schema.role_definition (role_code, role_name, description, role_level, is_system) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 1, true),
('SYSTEM_ADMIN', '系统管理员', '系统管理员，负责系统配置和用户管理', 2, true),
('USER_ADMIN', '用户管理员', '用户管理员，负责用户和角色管理', 3, false),
('NORMAL_USER', '普通用户', '普通用户，基础权限', 4, false);

-- 插入菜单
INSERT INTO platform_schema.menu_item (menu_code, menu_name, menu_type, parent_id, menu_path, component_path, icon, sort_order, description) VALUES
-- 一级菜单
('SYSTEM', '系统管理', 'DIRECTORY', NULL, '/system', NULL, 'system', 1, '系统管理目录'),
('MONITOR', '系统监控', 'DIRECTORY', NULL, '/monitor', NULL, 'monitor', 2, '系统监控目录'),

-- 系统管理子菜单
('USER_MENU', '用户管理', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/user', 'system/user/index', 'user', 1, '用户管理页面'),
('ROLE_MENU', '角色管理', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/role', 'system/role/index', 'role', 2, '角色管理页面'),
('PERMISSION_MENU', '权限管理', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/permission', 'system/permission/index', 'permission', 3, '权限管理页面'),
('MENU_MENU', '菜单管理', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/menu', 'system/menu/index', 'menu', 4, '菜单管理页面'),
('CONFIG_MENU', '系统配置', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/config', 'system/config/index', 'config', 5, '系统配置页面'),
('DICTIONARY_MENU', '数据字典', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'SYSTEM'), '/system/dictionary', 'system/dictionary/index', 'dictionary', 6, '数据字典页面'),

-- 系统监控子菜单
('AUDIT_MENU', '操作日志', 'MENU', (SELECT id FROM platform_schema.menu_item WHERE menu_code = 'MONITOR'), '/monitor/audit', 'monitor/audit/index', 'audit', 1, '操作日志页面');

-- 创建默认管理员用户
INSERT INTO platform_schema.user_profile (asid, username, email, real_name, is_active, is_email_verified) VALUES
('admin_001', 'admin', 'admin@example.com', '系统管理员', true, true);

-- 创建默认管理员密码 (admin123 的BCrypt加密)
INSERT INTO platform_schema.user_credential (user_id, credential_type, credential_value, is_active) VALUES
((SELECT id FROM platform_schema.user_profile WHERE username = 'admin'), 'PASSWORD', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKnvL6fNlIgJlSIKTZ9Pu', true);

-- 给管理员分配超级管理员角色
INSERT INTO platform_schema.user_role_mapping (user_id, role_id, is_active) VALUES
((SELECT id FROM platform_schema.user_profile WHERE username = 'admin'),
 (SELECT id FROM platform_schema.role_definition WHERE role_code = 'SUPER_ADMIN'),
 true);

-- 给超级管理员角色分配所有权限
INSERT INTO platform_schema.role_permission_mapping (role_id, resource_id, action_id, is_granted)
SELECT
    (SELECT id FROM platform_schema.role_definition WHERE role_code = 'SUPER_ADMIN'),
    pr.id,
    pa.id,
    true
FROM platform_schema.permission_resource pr
CROSS JOIN platform_schema.permission_action pa;

-- 插入系统配置
INSERT INTO foundation_schema.config_system (config_key, config_value, config_type, category, description, is_readonly) VALUES
('system.name', 'CodeAS Backend', 'STRING', 'SYSTEM', '系统名称', false),
('system.version', '0.0.1', 'STRING', 'SYSTEM', '系统版本', true),
('system.copyright', '© 2024 akko. All rights reserved.', 'STRING', 'SYSTEM', '版权信息', false),
('security.password.min_length', '6', 'NUMBER', 'SECURITY', '密码最小长度', false),
('security.password.max_length', '20', 'NUMBER', 'SECURITY', '密码最大长度', false),
('security.password.require_special_char', 'false', 'BOOLEAN', 'SECURITY', '密码是否需要特殊字符', false),
('security.login.max_attempts', '5', 'NUMBER', 'SECURITY', '最大登录尝试次数', false),
('security.login.lock_duration', '900', 'NUMBER', 'SECURITY', '账户锁定时长(秒)', false),
('cache.default_ttl', '1800', 'NUMBER', 'CACHE', '默认缓存过期时间(秒)', false),
('file.upload.max_size', '10485760', 'NUMBER', 'FILE', '文件上传最大大小(字节)', false),
('file.upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'STRING', 'FILE', '允许上传的文件类型', false);

-- 插入功能开关
INSERT INTO foundation_schema.config_feature_toggle (feature_key, feature_name, is_enabled, description, environment) VALUES
('user.registration', '用户注册', true, '是否允许用户自主注册', 'ALL'),
('user.email_verification', '邮箱验证', false, '是否启用邮箱验证', 'ALL'),
('user.phone_verification', '手机验证', false, '是否启用手机验证', 'ALL'),
('audit.async_logging', '异步日志', true, '是否启用异步操作日志记录', 'ALL'),
('cache.multi_level', '多级缓存', true, '是否启用多级缓存', 'ALL'),
('security.captcha', '验证码', false, '是否启用登录验证码', 'ALL');

-- 插入数据字典分类
INSERT INTO platform_schema.dictionary_category (category_code, category_name, description, sort_order) VALUES
('GENDER', '性别', '用户性别分类', 1),
('USER_STATUS', '用户状态', '用户账户状态', 2),
('OPERATION_TYPE', '操作类型', '系统操作类型分类', 3),
('FILE_TYPE', '文件类型', '文件类型分类', 4),
('CONFIG_TYPE', '配置类型', '系统配置类型', 5);

-- 插入数据字典项
INSERT INTO platform_schema.dictionary_item (category_id, item_code, item_name, item_value, sort_order, description) VALUES
-- 性别
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'GENDER'), 'MALE', '男', 'MALE', 1, '男性'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'GENDER'), 'FEMALE', '女', 'FEMALE', 2, '女性'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'GENDER'), 'OTHER', '其他', 'OTHER', 3, '其他性别'),

-- 用户状态
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'USER_STATUS'), 'ACTIVE', '正常', 'ACTIVE', 1, '账户正常状态'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'USER_STATUS'), 'INACTIVE', '禁用', 'INACTIVE', 2, '账户禁用状态'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'USER_STATUS'), 'LOCKED', '锁定', 'LOCKED', 3, '账户锁定状态'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'USER_STATUS'), 'EXPIRED', '过期', 'EXPIRED', 4, '账户过期状态'),

-- 操作类型
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'LOGIN', '登录', 'LOGIN', 1, '用户登录操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'LOGOUT', '登出', 'LOGOUT', 2, '用户登出操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'CREATE', '创建', 'CREATE', 3, '创建操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'UPDATE', '更新', 'UPDATE', 4, '更新操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'DELETE', '删除', 'DELETE', 5, '删除操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'EXPORT', '导出', 'EXPORT', 6, '数据导出操作'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'OPERATION_TYPE'), 'IMPORT', '导入', 'IMPORT', 7, '数据导入操作'),

-- 文件类型
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'FILE_TYPE'), 'IMAGE', '图片', 'IMAGE', 1, '图片文件'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'FILE_TYPE'), 'DOCUMENT', '文档', 'DOCUMENT', 2, '文档文件'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'FILE_TYPE'), 'EXCEL', '表格', 'EXCEL', 3, 'Excel表格文件'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'FILE_TYPE'), 'PDF', 'PDF', 'PDF', 4, 'PDF文件'),

-- 配置类型
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'CONFIG_TYPE'), 'STRING', '字符串', 'STRING', 1, '字符串类型配置'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'CONFIG_TYPE'), 'NUMBER', '数字', 'NUMBER', 2, '数字类型配置'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'CONFIG_TYPE'), 'BOOLEAN', '布尔', 'BOOLEAN', 3, '布尔类型配置'),
((SELECT id FROM platform_schema.dictionary_category WHERE category_code = 'CONFIG_TYPE'), 'JSON', 'JSON', 'JSON', 4, 'JSON类型配置');
