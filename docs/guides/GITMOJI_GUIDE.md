# 🎨 Gitmoji 提交规范指南

本项目采用 Gitmoji 规范来标准化 Git 提交信息，结合 GitHub 支持的 emoji 来提高提交信息的可读性和表达力。

## 📋 基本格式

```
<gitmoji> <type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### 示例
```
:sparkles: feat(auth): 添加JWT认证功能

实现用户登录、注册和token刷新功能
- 添加JWT工具类
- 实现用户认证服务
- 添加安全配置

Closes #123
```

## 🎯 核心 Gitmoji 规范

### 🚀 功能开发
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| ✨ | `:sparkles:` | feat | 引入新功能 |
| 🎉 | `:tada:` | feat | 初始化项目 |
| 🚀 | `:rocket:` | feat | 部署功能 |
| 💫 | `:dizzy:` | feat | 添加动画效果 |
| 🌟 | `:star2:` | feat | 添加重要新功能 |

### 🐛 问题修复
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 🐛 | `:bug:` | fix | 修复bug |
| 🚑 | `:ambulance:` | fix | 紧急修复 |
| 🩹 | `:adhesive_bandage:` | fix | 简单修复 |
| 🔥 | `:fire:` | fix | 移除代码或文件 |
| 💥 | `:boom:` | fix | 引入破坏性变更 |

### ⚡ 性能优化
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| ⚡ | `:zap:` | perf | 提升性能 |
| 🐎 | `:racehorse:` | perf | 提升性能 |
| 📈 | `:chart_with_upwards_trend:` | perf | 添加分析或跟踪代码 |
| ⏱️ | `:timer_clock:` | perf | 性能相关改进 |

### 🎨 代码质量
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 🎨 | `:art:` | style | 改进代码结构/格式 |
| ♻️ | `:recycle:` | refactor | 重构代码 |
| 🧹 | `:broom:` | refactor | 清理代码 |
| 💄 | `:lipstick:` | style | 更新UI和样式文件 |
| 🏗️ | `:building_construction:` | refactor | 架构变更 |

### 📝 文档相关
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 📝 | `:memo:` | docs | 添加或更新文档 |
| 📚 | `:books:` | docs | 添加或更新文档 |
| 💡 | `:bulb:` | docs | 添加或更新源码中的注释 |
| 📖 | `:book:` | docs | 添加或更新README |
| 📄 | `:page_facing_up:` | docs | 添加或更新许可证 |

### 🧪 测试相关
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| ✅ | `:white_check_mark:` | test | 添加、更新或通过测试 |
| 🧪 | `:test_tube:` | test | 添加失败的测试 |
| 🤡 | `:clown_face:` | test | 模拟事物 |
| 📸 | `:camera_flash:` | test | 添加或更新快照 |
| 🔍 | `:mag:` | test | 改进SEO |

### ⚙️ 配置和工具
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| ⚙️ | `:gear:` | chore | 添加或更新配置文件 |
| 🔧 | `:wrench:` | chore | 添加或更新配置文件 |
| 🔨 | `:hammer:` | chore | 添加或更新开发脚本 |
| 👷 | `:construction_worker:` | ci | 添加或更新CI构建系统 |
| 💚 | `:green_heart:` | ci | 修复CI构建问题 |
| 📦 | `:package:` | build | 添加或更新编译文件或包 |

### 🔒 安全相关
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 🔒 | `:lock:` | security | 修复安全问题 |
| 🛡️ | `:shield:` | security | 添加安全功能 |
| 🔐 | `:closed_lock_with_key:` | security | 添加密钥、密码等 |
| 🚨 | `:rotating_light:` | security | 修复编译器/linter警告 |

### 📱 平台相关
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 📱 | `:iphone:` | feat | 响应式设计工作 |
| 🖥️ | `:desktop_computer:` | feat | 桌面端相关 |
| 🌐 | `:globe_with_meridians:` | feat | 国际化和本地化 |
| ♿ | `:wheelchair:` | feat | 改进可访问性 |
| 🔊 | `:loud_sound:` | feat | 添加或更新日志 |
| 🔇 | `:mute:` | feat | 移除日志 |

## 🎯 扩展 Gitmoji（GitHub 特色）

### 🤖 自动化和机器人
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 🤖 | `:robot:` | ci | 自动化相关 |
| 🔄 | `:arrows_counterclockwise:` | ci | 更新依赖 |
| ⬆️ | `:arrow_up:` | chore | 升级依赖 |
| ⬇️ | `:arrow_down:` | chore | 降级依赖 |
| 📌 | `:pushpin:` | chore | 固定依赖到特定版本 |

### 🎯 项目管理
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 🏷️ | `:label:` | chore | 添加或更新类型 |
| 🗃️ | `:card_file_box:` | feat | 执行数据库相关更改 |
| 🔀 | `:twisted_rightwards_arrows:` | merge | 合并分支 |
| ⏪ | `:rewind:` | revert | 回滚更改 |
| 🔖 | `:bookmark:` | release | 发布/版本标签 |

### 🌟 特殊标记
| Gitmoji | Code | 类型 | 描述 |
|---------|------|------|------|
| 💩 | `:poop:` | fix | 编写需要改进的糟糕代码 |
| 🚧 | `:construction:` | wip | 进行中的工作 |
| ⚗️ | `:alembic:` | feat | 进行实验 |
| 🥅 | `:goal_net:` | fix | 捕获错误 |
| 💸 | `:money_with_wings:` | feat | 添加赞助或金钱相关基础设施 |

## 📋 使用规范

### 1. 提交类型 (type)
- `feat`: 新功能
- `fix`: 修复
- `docs`: 文档
- `style`: 格式
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试
- `chore`: 构建过程或辅助工具的变动
- `ci`: CI/CD相关
- `build`: 构建系统
- `revert`: 回滚
- `wip`: 进行中
- `merge`: 合并
- `release`: 发布

### 2. 作用域 (scope)
- `auth`: 认证相关
- `user`: 用户相关
- `api`: API相关
- `ui`: 界面相关
- `db`: 数据库相关
- `config`: 配置相关
- `test`: 测试相关
- `ci`: CI/CD相关
- `docs`: 文档相关
- `deps`: 依赖相关

### 3. 描述 (description)
- 使用中文描述
- 动词开头，简洁明了
- 不超过50个字符
- 不以句号结尾

## 🛠️ 工具推荐

### VS Code 插件
- **Gitmoji**: 提供gitmoji选择器
- **Conventional Commits**: 规范化提交信息
- **GitLens**: 增强Git功能

### 命令行工具
```bash
# 安装gitmoji-cli
npm i -g gitmoji-cli

# 使用gitmoji提交
gitmoji -c
```

## 📚 参考资源

- [Gitmoji 官网](https://gitmoji.dev/)
- [GitHub Emoji API](https://api.github.com/emojis)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Angular Commit Guidelines](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)
