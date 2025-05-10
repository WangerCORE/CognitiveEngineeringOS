# CE-OS (Cognitive Engineering OS)

认知工程操作系统 - 一个用于自动采集、评估、分类和归档信息的智能系统。

## 项目概述

CE-OS 是一个基于微服务架构的认知工程系统，旨在通过AI技术自动化信息处理流程，包括：

- RSS源自动抓取
- AI内容评估
- 智能分类
- Markdown格式化
- 本地归档
- 报告生成

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Cloud
- PostgreSQL
- Redis
- Elasticsearch
- RabbitMQ
- OpenAI API

### 前端
- React 18
- TypeScript
- Ant Design
- Redux Toolkit
- ECharts

### DevOps
- Docker
- Kubernetes
- GitHub Actions
- Prometheus + Grafana

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

### 开发环境搭建

1. 克隆项目
```bash
git clone https://github.com/your-org/ce-os.git
cd ce-os
```

2. 启动后端服务
```bash
cd backend
mvn clean install
docker-compose up -d
```

3. 启动前端开发服务器
```bash
cd frontend
npm install
npm run dev
```

4. 访问系统
- 前端: http://localhost:5173
- API文档: http://localhost:8080/swagger-ui.html

## 项目结构

```
ce-os/
├── backend/                # 后端微服务
│   ├── ce-os-common/      # 公共模块
│   ├── ce-os-gateway/     # API网关
│   ├── ce-os-auth/        # 认证服务
│   ├── ce-os-fetch/       # RSS抓取服务
│   ├── ce-os-evaluator/   # AI评估服务
│   ├── ce-os-classifier/  # AI分类服务
│   ├── ce-os-formatter/   # Markdown格式化
│   ├── ce-os-archivist/   # 归档服务
│   └── ce-os-report/      # 报告生成服务
├── frontend/              # 前端应用
│   ├── src/
│   │   ├── components/   # React组件
│   │   ├── pages/       # 页面
│   │   ├── store/       # Redux状态
│   │   └── services/    # API服务
│   └── public/          # 静态资源
└── docs/                # 项目文档
```

## 开发指南

### 代码规范
- 后端遵循Google Java Style Guide
- 前端使用ESLint + Prettier
- 提交信息遵循Conventional Commits

### 分支管理
- main: 主分支，保持稳定
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复分支

### 测试
- 单元测试: JUnit 5 (后端) / Vitest (前端)
- 集成测试: Spring Test
- E2E测试: Cypress

## 部署

### 开发环境
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### 生产环境
```bash
kubectl apply -f k8s/
```

## 监控

- 应用监控: Prometheus + Grafana
- 日志管理: ELK Stack
- 告警: AlertManager

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交变更
4. 发起Pull Request

## 许可证

MIT License 
