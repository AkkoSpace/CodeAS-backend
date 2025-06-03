# 分支保护规则配置指南

## 🛡️ 分支保护的重要性

分支保护规则确保代码质量和安全性，防止未经审查的代码直接合并到主分支。

## 📋 推荐的分支保护配置

### **Main分支保护规则**

访问 `https://github.com/AkkoSpace/CodeAS-backend/settings/branches` 配置以下规则：

#### **1. 基本保护**
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: `1`
  - ✅ Dismiss stale PR approvals when new commits are pushed
  - ✅ Require review from code owners

#### **2. 状态检查要求**
- ✅ **Require status checks to pass before merging**
  - ✅ Require branches to be up to date before merging
  - **必需的状态检查**:
    - `CI Pipeline`
    - `Code Quality Analysis`
    - `Security Scan`
    - `License Check`

#### **3. 其他限制**
- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits**
- ✅ **Require linear history**
- ✅ **Include administrators** (管理员也需要遵守规则)

#### **4. 推送限制**
- ✅ **Restrict pushes that create files**
- ✅ **Block force pushes**
- ✅ **Restrict deletions**

### **Dev分支保护规则**

#### **1. 基本保护**
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: `1` (可以是自己)
  - ❌ Dismiss stale PR approvals (开发分支可以更灵活)

#### **2. 状态检查要求**
- ✅ **Require status checks to pass before merging**
  - **必需的状态检查**:
    - `CI Pipeline`
    - `Code Quality Analysis`

## 🔄 新的工作流程

### **Dependabot更新流程**
```
1. Dependabot创建PR → dev分支
2. 自动运行CI/CD检查
3. 代码审查 (可选，小更新可自动合并)
4. 合并到dev分支
5. 定期从dev创建PR到main分支
6. 经过完整审查后合并到main
```

### **功能开发流程**
```
1. 从dev分支创建feature分支
2. 开发完成后创建PR到dev分支
3. 代码审查和CI检查
4. 合并到dev分支
5. 定期从dev创建release PR到main分支
```

## 🚀 配置步骤

### **1. 立即配置分支保护**
1. 访问仓库设置页面
2. 点击 "Branches" 选项卡
3. 为 `main` 分支添加保护规则
4. 为 `dev` 分支添加保护规则

### **2. 验证配置**
1. 尝试直接推送到main分支 (应该被阻止)
2. 创建测试PR验证状态检查
3. 确认Dependabot PR目标到dev分支

## 📚 参考资料

- [GitHub分支保护文档](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches)
- [Dependabot配置文档](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file)
