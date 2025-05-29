-- 创建Schema
CREATE SCHEMA IF NOT EXISTS foundation_schema;
CREATE SCHEMA IF NOT EXISTS platform_schema;
CREATE SCHEMA IF NOT EXISTS business_schema;
CREATE SCHEMA IF NOT EXISTS integration_schema;

-- 设置搜索路径
ALTER DATABASE codeas SET search_path TO platform_schema, foundation_schema, public;

-- 创建注释
COMMENT ON SCHEMA foundation_schema IS '基础层Schema - 包含认证、缓存、配置等基础功能';
COMMENT ON SCHEMA platform_schema IS '平台层Schema - 包含用户、角色、权限等平台功能';
COMMENT ON SCHEMA business_schema IS '业务层Schema - 包含具体业务模块';
COMMENT ON SCHEMA integration_schema IS '集成层Schema - 包含外部系统集成功能';
