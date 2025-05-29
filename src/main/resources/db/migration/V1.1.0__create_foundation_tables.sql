-- Foundation Schema Tables

-- JWT令牌管理表
CREATE TABLE foundation_schema.auth_token (
    id BIGSERIAL PRIMARY KEY,
    token_value VARCHAR(500) NOT NULL,
    user_id BIGINT NOT NULL,
    token_type VARCHAR(20) NOT NULL DEFAULT 'ACCESS', -- ACCESS, REFRESH
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    is_revoked BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE foundation_schema.auth_token IS 'JWT令牌管理表';
COMMENT ON COLUMN foundation_schema.auth_token.token_value IS '令牌值';
COMMENT ON COLUMN foundation_schema.auth_token.user_id IS '用户ID';
COMMENT ON COLUMN foundation_schema.auth_token.token_type IS '令牌类型：ACCESS-访问令牌，REFRESH-刷新令牌';
COMMENT ON COLUMN foundation_schema.auth_token.expires_at IS '过期时间';
COMMENT ON COLUMN foundation_schema.auth_token.is_revoked IS '是否已撤销';

-- 用户会话表
CREATE TABLE foundation_schema.auth_session (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(64) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    ip_address INET,
    user_agent TEXT,
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_access_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
);

COMMENT ON TABLE foundation_schema.auth_session IS '用户会话表';
COMMENT ON COLUMN foundation_schema.auth_session.session_id IS '会话ID';
COMMENT ON COLUMN foundation_schema.auth_session.user_id IS '用户ID';
COMMENT ON COLUMN foundation_schema.auth_session.ip_address IS 'IP地址';
COMMENT ON COLUMN foundation_schema.auth_session.user_agent IS '用户代理';
COMMENT ON COLUMN foundation_schema.auth_session.login_at IS '登录时间';
COMMENT ON COLUMN foundation_schema.auth_session.last_access_at IS '最后访问时间';
COMMENT ON COLUMN foundation_schema.auth_session.expires_at IS '过期时间';
COMMENT ON COLUMN foundation_schema.auth_session.is_active IS '是否活跃';

-- 登录尝试记录表
CREATE TABLE foundation_schema.auth_login_attempt (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100),
    ip_address INET NOT NULL,
    user_agent TEXT,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_success BOOLEAN NOT NULL,
    failure_reason VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE foundation_schema.auth_login_attempt IS '登录尝试记录表';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.username IS '用户名';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.ip_address IS 'IP地址';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.user_agent IS '用户代理';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.attempt_time IS '尝试时间';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.is_success IS '是否成功';
COMMENT ON COLUMN foundation_schema.auth_login_attempt.failure_reason IS '失败原因';

-- 系统配置表
CREATE TABLE foundation_schema.config_system (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    config_type VARCHAR(20) NOT NULL DEFAULT 'STRING', -- STRING, NUMBER, BOOLEAN, JSON
    category VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    is_readonly BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE foundation_schema.config_system IS '系统配置表';
COMMENT ON COLUMN foundation_schema.config_system.config_key IS '配置键';
COMMENT ON COLUMN foundation_schema.config_system.config_value IS '配置值';
COMMENT ON COLUMN foundation_schema.config_system.config_type IS '配置类型：STRING, NUMBER, BOOLEAN, JSON';
COMMENT ON COLUMN foundation_schema.config_system.category IS '配置分类';
COMMENT ON COLUMN foundation_schema.config_system.description IS '配置描述';
COMMENT ON COLUMN foundation_schema.config_system.is_encrypted IS '是否加密';
COMMENT ON COLUMN foundation_schema.config_system.is_readonly IS '是否只读';

-- 功能开关表
CREATE TABLE foundation_schema.config_feature_toggle (
    id BIGSERIAL PRIMARY KEY,
    feature_key VARCHAR(100) UNIQUE NOT NULL,
    feature_name VARCHAR(200) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    description TEXT,
    environment VARCHAR(20) DEFAULT 'ALL', -- ALL, DEV, TEST, PROD
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE foundation_schema.config_feature_toggle IS '功能开关表';
COMMENT ON COLUMN foundation_schema.config_feature_toggle.feature_key IS '功能键';
COMMENT ON COLUMN foundation_schema.config_feature_toggle.feature_name IS '功能名称';
COMMENT ON COLUMN foundation_schema.config_feature_toggle.is_enabled IS '是否启用';
COMMENT ON COLUMN foundation_schema.config_feature_toggle.description IS '功能描述';
COMMENT ON COLUMN foundation_schema.config_feature_toggle.environment IS '适用环境';

-- 序列号管理表
CREATE TABLE foundation_schema.common_sequence (
    id BIGSERIAL PRIMARY KEY,
    sequence_name VARCHAR(50) UNIQUE NOT NULL,
    current_value BIGINT NOT NULL DEFAULT 0,
    increment_step INTEGER NOT NULL DEFAULT 1,
    prefix VARCHAR(20),
    suffix VARCHAR(20),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE foundation_schema.common_sequence IS '序列号管理表';
COMMENT ON COLUMN foundation_schema.common_sequence.sequence_name IS '序列名称';
COMMENT ON COLUMN foundation_schema.common_sequence.current_value IS '当前值';
COMMENT ON COLUMN foundation_schema.common_sequence.increment_step IS '递增步长';
COMMENT ON COLUMN foundation_schema.common_sequence.prefix IS '前缀';
COMMENT ON COLUMN foundation_schema.common_sequence.suffix IS '后缀';

-- 文件管理表
CREATE TABLE foundation_schema.common_file (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    mime_type VARCHAR(100),
    md5_hash VARCHAR(32),
    upload_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE foundation_schema.common_file IS '文件管理表';
COMMENT ON COLUMN foundation_schema.common_file.file_name IS '文件名';
COMMENT ON COLUMN foundation_schema.common_file.original_name IS '原始文件名';
COMMENT ON COLUMN foundation_schema.common_file.file_path IS '文件路径';
COMMENT ON COLUMN foundation_schema.common_file.file_size IS '文件大小';
COMMENT ON COLUMN foundation_schema.common_file.file_type IS '文件类型';
COMMENT ON COLUMN foundation_schema.common_file.mime_type IS 'MIME类型';
COMMENT ON COLUMN foundation_schema.common_file.md5_hash IS 'MD5哈希值';
COMMENT ON COLUMN foundation_schema.common_file.upload_by IS '上传者';

-- 创建索引
CREATE INDEX idx_auth_token_user_id ON foundation_schema.auth_token(user_id);
CREATE INDEX idx_auth_token_expires_at ON foundation_schema.auth_token(expires_at);
CREATE INDEX idx_auth_session_user_id ON foundation_schema.auth_session(user_id);
CREATE INDEX idx_auth_session_session_id ON foundation_schema.auth_session(session_id);
CREATE INDEX idx_auth_login_attempt_ip ON foundation_schema.auth_login_attempt(ip_address);
CREATE INDEX idx_auth_login_attempt_time ON foundation_schema.auth_login_attempt(attempt_time);
CREATE INDEX idx_config_system_category ON foundation_schema.config_system(category);
CREATE INDEX idx_config_feature_environment ON foundation_schema.config_feature_toggle(environment);
CREATE INDEX idx_common_file_upload_by ON foundation_schema.common_file(upload_by);
CREATE INDEX idx_common_file_created_at ON foundation_schema.common_file(created_at);
