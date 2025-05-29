-- Platform Schema Tables

-- 用户基本信息表
CREATE TABLE platform_schema.user_profile (
    id BIGSERIAL PRIMARY KEY,
    asid VARCHAR(32) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20),
    real_name VARCHAR(100),
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    gender VARCHAR(10), -- MALE, FEMALE, OTHER
    birth_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_phone_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    last_login_ip INET,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.user_profile IS '用户基本信息表';
COMMENT ON COLUMN platform_schema.user_profile.asid IS '应用唯一标识';
COMMENT ON COLUMN platform_schema.user_profile.username IS '用户名';
COMMENT ON COLUMN platform_schema.user_profile.email IS '邮箱';
COMMENT ON COLUMN platform_schema.user_profile.phone_number IS '手机号';
COMMENT ON COLUMN platform_schema.user_profile.real_name IS '真实姓名';
COMMENT ON COLUMN platform_schema.user_profile.nickname IS '昵称';
COMMENT ON COLUMN platform_schema.user_profile.avatar_url IS '头像URL';
COMMENT ON COLUMN platform_schema.user_profile.gender IS '性别';
COMMENT ON COLUMN platform_schema.user_profile.birth_date IS '出生日期';
COMMENT ON COLUMN platform_schema.user_profile.is_active IS '是否激活';
COMMENT ON COLUMN platform_schema.user_profile.is_email_verified IS '邮箱是否验证';
COMMENT ON COLUMN platform_schema.user_profile.is_phone_verified IS '手机是否验证';
COMMENT ON COLUMN platform_schema.user_profile.last_login_at IS '最后登录时间';
COMMENT ON COLUMN platform_schema.user_profile.last_login_ip IS '最后登录IP';

-- 用户凭证表
CREATE TABLE platform_schema.user_credential (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    credential_type VARCHAR(20) NOT NULL DEFAULT 'PASSWORD', -- PASSWORD, OAUTH, API_KEY
    credential_value VARCHAR(255) NOT NULL,
    salt VARCHAR(64),
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.user_credential IS '用户凭证表';
COMMENT ON COLUMN platform_schema.user_credential.user_id IS '用户ID';
COMMENT ON COLUMN platform_schema.user_credential.credential_type IS '凭证类型';
COMMENT ON COLUMN platform_schema.user_credential.credential_value IS '凭证值';
COMMENT ON COLUMN platform_schema.user_credential.salt IS '盐值';
COMMENT ON COLUMN platform_schema.user_credential.is_active IS '是否激活';
COMMENT ON COLUMN platform_schema.user_credential.expires_at IS '过期时间';

-- 角色定义表
CREATE TABLE platform_schema.role_definition (
    id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    description TEXT,
    role_level INTEGER DEFAULT 0,
    parent_id BIGINT,
    is_system BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.role_definition IS '角色定义表';
COMMENT ON COLUMN platform_schema.role_definition.role_code IS '角色编码';
COMMENT ON COLUMN platform_schema.role_definition.role_name IS '角色名称';
COMMENT ON COLUMN platform_schema.role_definition.description IS '角色描述';
COMMENT ON COLUMN platform_schema.role_definition.role_level IS '角色级别';
COMMENT ON COLUMN platform_schema.role_definition.parent_id IS '父角色ID';
COMMENT ON COLUMN platform_schema.role_definition.is_system IS '是否系统角色';
COMMENT ON COLUMN platform_schema.role_definition.is_active IS '是否激活';

-- 角色层级关系表
CREATE TABLE platform_schema.role_hierarchy (
    id BIGSERIAL PRIMARY KEY,
    parent_role_id BIGINT NOT NULL,
    child_role_id BIGINT NOT NULL,
    hierarchy_level INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    UNIQUE(parent_role_id, child_role_id)
);

COMMENT ON TABLE platform_schema.role_hierarchy IS '角色层级关系表';
COMMENT ON COLUMN platform_schema.role_hierarchy.parent_role_id IS '父角色ID';
COMMENT ON COLUMN platform_schema.role_hierarchy.child_role_id IS '子角色ID';
COMMENT ON COLUMN platform_schema.role_hierarchy.hierarchy_level IS '层级级别';

-- 权限资源表
CREATE TABLE platform_schema.permission_resource (
    id BIGSERIAL PRIMARY KEY,
    resource_code VARCHAR(100) UNIQUE NOT NULL,
    resource_name VARCHAR(200) NOT NULL,
    resource_type VARCHAR(20) NOT NULL DEFAULT 'API', -- API, MENU, BUTTON, DATA
    resource_url VARCHAR(500),
    http_method VARCHAR(10),
    parent_id BIGINT,
    sort_order INTEGER DEFAULT 0,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.permission_resource IS '权限资源表';
COMMENT ON COLUMN platform_schema.permission_resource.resource_code IS '资源编码';
COMMENT ON COLUMN platform_schema.permission_resource.resource_name IS '资源名称';
COMMENT ON COLUMN platform_schema.permission_resource.resource_type IS '资源类型';
COMMENT ON COLUMN platform_schema.permission_resource.resource_url IS '资源URL';
COMMENT ON COLUMN platform_schema.permission_resource.http_method IS 'HTTP方法';
COMMENT ON COLUMN platform_schema.permission_resource.parent_id IS '父资源ID';
COMMENT ON COLUMN platform_schema.permission_resource.sort_order IS '排序';

-- 权限动作表
CREATE TABLE platform_schema.permission_action (
    id BIGSERIAL PRIMARY KEY,
    action_code VARCHAR(50) UNIQUE NOT NULL,
    action_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.permission_action IS '权限动作表';
COMMENT ON COLUMN platform_schema.permission_action.action_code IS '动作编码';
COMMENT ON COLUMN platform_schema.permission_action.action_name IS '动作名称';

-- 菜单项表
CREATE TABLE platform_schema.menu_item (
    id BIGSERIAL PRIMARY KEY,
    menu_code VARCHAR(100) UNIQUE NOT NULL,
    menu_name VARCHAR(200) NOT NULL,
    menu_type VARCHAR(20) NOT NULL DEFAULT 'MENU', -- DIRECTORY, MENU, BUTTON
    parent_id BIGINT,
    menu_path VARCHAR(500),
    component_path VARCHAR(500),
    icon VARCHAR(100),
    sort_order INTEGER DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    is_cache BOOLEAN DEFAULT FALSE,
    is_external BOOLEAN DEFAULT FALSE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.menu_item IS '菜单项表';
COMMENT ON COLUMN platform_schema.menu_item.menu_code IS '菜单编码';
COMMENT ON COLUMN platform_schema.menu_item.menu_name IS '菜单名称';
COMMENT ON COLUMN platform_schema.menu_item.menu_type IS '菜单类型';
COMMENT ON COLUMN platform_schema.menu_item.parent_id IS '父菜单ID';
COMMENT ON COLUMN platform_schema.menu_item.menu_path IS '菜单路径';
COMMENT ON COLUMN platform_schema.menu_item.component_path IS '组件路径';
COMMENT ON COLUMN platform_schema.menu_item.icon IS '图标';
COMMENT ON COLUMN platform_schema.menu_item.sort_order IS '排序';
COMMENT ON COLUMN platform_schema.menu_item.is_visible IS '是否可见';
COMMENT ON COLUMN platform_schema.menu_item.is_cache IS '是否缓存';
COMMENT ON COLUMN platform_schema.menu_item.is_external IS '是否外链';

-- 菜单权限关联表
CREATE TABLE platform_schema.menu_permission (
    id BIGSERIAL PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    UNIQUE(menu_id, resource_id, action_id)
);

COMMENT ON TABLE platform_schema.menu_permission IS '菜单权限关联表';
COMMENT ON COLUMN platform_schema.menu_permission.menu_id IS '菜单ID';
COMMENT ON COLUMN platform_schema.menu_permission.resource_id IS '资源ID';
COMMENT ON COLUMN platform_schema.menu_permission.action_id IS '动作ID';

-- 用户角色关联表
CREATE TABLE platform_schema.user_role_mapping (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    UNIQUE(user_id, role_id)
);

COMMENT ON TABLE platform_schema.user_role_mapping IS '用户角色关联表';
COMMENT ON COLUMN platform_schema.user_role_mapping.user_id IS '用户ID';
COMMENT ON COLUMN platform_schema.user_role_mapping.role_id IS '角色ID';
COMMENT ON COLUMN platform_schema.user_role_mapping.effective_from IS '生效开始时间';
COMMENT ON COLUMN platform_schema.user_role_mapping.effective_to IS '生效结束时间';
COMMENT ON COLUMN platform_schema.user_role_mapping.is_active IS '是否激活';

-- 角色权限关联表
CREATE TABLE platform_schema.role_permission_mapping (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    is_granted BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    UNIQUE(role_id, resource_id, action_id)
);

COMMENT ON TABLE platform_schema.role_permission_mapping IS '角色权限关联表';
COMMENT ON COLUMN platform_schema.role_permission_mapping.role_id IS '角色ID';
COMMENT ON COLUMN platform_schema.role_permission_mapping.resource_id IS '资源ID';
COMMENT ON COLUMN platform_schema.role_permission_mapping.action_id IS '动作ID';
COMMENT ON COLUMN platform_schema.role_permission_mapping.is_granted IS '是否授权';

-- 操作日志表
CREATE TABLE platform_schema.audit_operation_log (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(64),
    user_id BIGINT,
    username VARCHAR(100),
    operation_type VARCHAR(50) NOT NULL,
    operation_name VARCHAR(200),
    resource_type VARCHAR(50),
    resource_id VARCHAR(100),
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    request_params TEXT,
    request_body TEXT,
    response_body TEXT,
    ip_address INET,
    user_agent TEXT,
    execution_time BIGINT,
    is_success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.audit_operation_log IS '操作日志表';
COMMENT ON COLUMN platform_schema.audit_operation_log.trace_id IS '追踪ID';
COMMENT ON COLUMN platform_schema.audit_operation_log.user_id IS '用户ID';
COMMENT ON COLUMN platform_schema.audit_operation_log.username IS '用户名';
COMMENT ON COLUMN platform_schema.audit_operation_log.operation_type IS '操作类型';
COMMENT ON COLUMN platform_schema.audit_operation_log.operation_name IS '操作名称';
COMMENT ON COLUMN platform_schema.audit_operation_log.resource_type IS '资源类型';
COMMENT ON COLUMN platform_schema.audit_operation_log.resource_id IS '资源ID';
COMMENT ON COLUMN platform_schema.audit_operation_log.request_method IS '请求方法';
COMMENT ON COLUMN platform_schema.audit_operation_log.request_url IS '请求URL';
COMMENT ON COLUMN platform_schema.audit_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN platform_schema.audit_operation_log.request_body IS '请求体';
COMMENT ON COLUMN platform_schema.audit_operation_log.response_body IS '响应体';
COMMENT ON COLUMN platform_schema.audit_operation_log.ip_address IS 'IP地址';
COMMENT ON COLUMN platform_schema.audit_operation_log.user_agent IS '用户代理';
COMMENT ON COLUMN platform_schema.audit_operation_log.execution_time IS '执行时间(毫秒)';
COMMENT ON COLUMN platform_schema.audit_operation_log.is_success IS '是否成功';
COMMENT ON COLUMN platform_schema.audit_operation_log.error_message IS '错误信息';
COMMENT ON COLUMN platform_schema.audit_operation_log.operation_time IS '操作时间';

-- 字典分类表
CREATE TABLE platform_schema.dictionary_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(50) UNIQUE NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    sort_order INTEGER DEFAULT 0,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE platform_schema.dictionary_category IS '字典分类表';
COMMENT ON COLUMN platform_schema.dictionary_category.category_code IS '分类编码';
COMMENT ON COLUMN platform_schema.dictionary_category.category_name IS '分类名称';
COMMENT ON COLUMN platform_schema.dictionary_category.parent_id IS '父分类ID';
COMMENT ON COLUMN platform_schema.dictionary_category.sort_order IS '排序';

-- 字典项表
CREATE TABLE platform_schema.dictionary_item (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    item_value VARCHAR(500),
    parent_id BIGINT,
    sort_order INTEGER DEFAULT 0,
    description TEXT,
    extra_data JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    UNIQUE(category_id, item_code)
);

COMMENT ON TABLE platform_schema.dictionary_item IS '字典项表';
COMMENT ON COLUMN platform_schema.dictionary_item.category_id IS '分类ID';
COMMENT ON COLUMN platform_schema.dictionary_item.item_code IS '项编码';
COMMENT ON COLUMN platform_schema.dictionary_item.item_name IS '项名称';
COMMENT ON COLUMN platform_schema.dictionary_item.item_value IS '项值';
COMMENT ON COLUMN platform_schema.dictionary_item.parent_id IS '父项ID';
COMMENT ON COLUMN platform_schema.dictionary_item.sort_order IS '排序';
COMMENT ON COLUMN platform_schema.dictionary_item.extra_data IS '扩展数据';

-- 创建索引
CREATE INDEX idx_user_profile_asid ON platform_schema.user_profile(asid);
CREATE INDEX idx_user_profile_username ON platform_schema.user_profile(username);
CREATE INDEX idx_user_profile_email ON platform_schema.user_profile(email);
CREATE INDEX idx_user_credential_user_id ON platform_schema.user_credential(user_id);
CREATE INDEX idx_role_definition_code ON platform_schema.role_definition(role_code);
CREATE INDEX idx_role_hierarchy_parent ON platform_schema.role_hierarchy(parent_role_id);
CREATE INDEX idx_role_hierarchy_child ON platform_schema.role_hierarchy(child_role_id);
CREATE INDEX idx_permission_resource_code ON platform_schema.permission_resource(resource_code);
CREATE INDEX idx_permission_resource_type ON platform_schema.permission_resource(resource_type);
CREATE INDEX idx_permission_action_code ON platform_schema.permission_action(action_code);
CREATE INDEX idx_menu_item_code ON platform_schema.menu_item(menu_code);
CREATE INDEX idx_menu_item_parent ON platform_schema.menu_item(parent_id);
CREATE INDEX idx_menu_permission_menu ON platform_schema.menu_permission(menu_id);
CREATE INDEX idx_user_role_mapping_user ON platform_schema.user_role_mapping(user_id);
CREATE INDEX idx_user_role_mapping_role ON platform_schema.user_role_mapping(role_id);
CREATE INDEX idx_role_permission_mapping_role ON platform_schema.role_permission_mapping(role_id);
CREATE INDEX idx_role_permission_mapping_resource ON platform_schema.role_permission_mapping(resource_id);
CREATE INDEX idx_audit_log_user_id ON platform_schema.audit_operation_log(user_id);
CREATE INDEX idx_audit_log_operation_time ON platform_schema.audit_operation_log(operation_time);
CREATE INDEX idx_audit_log_trace_id ON platform_schema.audit_operation_log(trace_id);
CREATE INDEX idx_dictionary_category_code ON platform_schema.dictionary_category(category_code);
CREATE INDEX idx_dictionary_item_category ON platform_schema.dictionary_item(category_id);
CREATE INDEX idx_dictionary_item_code ON platform_schema.dictionary_item(item_code);

-- 创建外键约束
ALTER TABLE platform_schema.user_credential ADD CONSTRAINT fk_user_credential_user_id
    FOREIGN KEY (user_id) REFERENCES platform_schema.user_profile(id);

ALTER TABLE platform_schema.role_hierarchy ADD CONSTRAINT fk_role_hierarchy_parent
    FOREIGN KEY (parent_role_id) REFERENCES platform_schema.role_definition(id);

ALTER TABLE platform_schema.role_hierarchy ADD CONSTRAINT fk_role_hierarchy_child
    FOREIGN KEY (child_role_id) REFERENCES platform_schema.role_definition(id);

ALTER TABLE platform_schema.menu_permission ADD CONSTRAINT fk_menu_permission_menu
    FOREIGN KEY (menu_id) REFERENCES platform_schema.menu_item(id);

ALTER TABLE platform_schema.menu_permission ADD CONSTRAINT fk_menu_permission_resource
    FOREIGN KEY (resource_id) REFERENCES platform_schema.permission_resource(id);

ALTER TABLE platform_schema.menu_permission ADD CONSTRAINT fk_menu_permission_action
    FOREIGN KEY (action_id) REFERENCES platform_schema.permission_action(id);

ALTER TABLE platform_schema.user_role_mapping ADD CONSTRAINT fk_user_role_mapping_user
    FOREIGN KEY (user_id) REFERENCES platform_schema.user_profile(id);

ALTER TABLE platform_schema.user_role_mapping ADD CONSTRAINT fk_user_role_mapping_role
    FOREIGN KEY (role_id) REFERENCES platform_schema.role_definition(id);

ALTER TABLE platform_schema.role_permission_mapping ADD CONSTRAINT fk_role_permission_mapping_role
    FOREIGN KEY (role_id) REFERENCES platform_schema.role_definition(id);

ALTER TABLE platform_schema.role_permission_mapping ADD CONSTRAINT fk_role_permission_mapping_resource
    FOREIGN KEY (resource_id) REFERENCES platform_schema.permission_resource(id);

ALTER TABLE platform_schema.role_permission_mapping ADD CONSTRAINT fk_role_permission_mapping_action
    FOREIGN KEY (action_id) REFERENCES platform_schema.permission_action(id);

ALTER TABLE platform_schema.dictionary_item ADD CONSTRAINT fk_dictionary_item_category
    FOREIGN KEY (category_id) REFERENCES platform_schema.dictionary_category(id);
