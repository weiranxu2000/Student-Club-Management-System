# 学生社团管理系统

一个基于现代企业级架构的学生社团综合管理平台，支持俱乐部常规管理、成员数据库管理、资金申请流程，为学生参与俱乐部活动和学术交流提供全方位技术支持。

## 业务功能特性

### 社团管理

- 社团信息维护与展示
- 成员管理与权限控制
- 活动组织与参与管理

### 资金管理

- 资金申请流程自动化
- 多级审批工作流
- 申请状态实时追踪

### 活动管理

- 活动创建与发布
- RSVP预约与票务管理
- 活动取消与修改支持

## 在线演示

**生产环境**: [https://swen90007-student-club-ui.onrender.com/](https://op-u60g.onrender.com/)

## 技术架构亮点

### 四层企业级架构设计
- **接口层 (Interface Layer)**: 基于Jakarta Servlet的RESTful API设计
- **服务层 (Service Layer)**: 业务逻辑封装与事务协调
- **领域层 (Domain Layer)**: 核心业务对象与规则实现
- **数据访问层 (Data Access Layer)**: Repository模式的数据抽象

### 高级设计模式实现
- **Unit of Work模式**: 事务管理与数据一致性保障
- **Repository模式**: 数据访问层抽象与解耦
- **ThreadLocal模式**: 线程安全的工作单元管理
- **Dependency Injection**: 依赖注入实现松耦合

## 核心技术栈

### 后端技术
```xml
• Java 11 - 现代化JVM语言特性
• Jakarta Servlet API 6.0.0 - 企业级Web服务
• Spring Security 6.3.0 - 安全认证框架
• PostgreSQL 42.5.3 - 企业级关系数据库
• Jackson 2.14.2 - 高性能JSON序列化
• Maven 3.9 - 项目构建与依赖管理
```

### 前端技术
```json
• React 18.3.1 - 现代化组件框架
• React Router DOM 6.26.2 - 客户端路由管理
• ESLint - 代码质量保障
• Modern ES6+ - 最新JavaScript特性
```

### 基础设施
```dockerfile
• Docker多阶段构建 - 容器化部署
• Tomcat 10.1.28 - Java Web容器
• Render云平台 - 现代化部署方案
```

## 高并发控制机制

### 悲观离线锁 (Pessimistic Offline Lock)
```java
// 自定义锁管理器实现
public class LockManagerWait {
    private ConcurrentMap<String, String> lockMap;
    
    public synchronized void acquireLock(String lockable, String owner) {
        while(lockMap.containsKey(lockable)) {
            wait(); // 线程等待机制
        }
        lockMap.put(lockable, owner);
    }
}
```

### 工作单元事务管理
```java
// Unit of Work模式保障数据一致性
public class UnitOfWork {
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();
    
    public boolean commit() {
        LockManagerWait.getInstance().acquireLock("uow", 
            Thread.currentThread().getName());
        // 原子性事务处理
    }
}
```

### 并发场景覆盖
- **RSVP创建并发**: 防止活动超额预定
- **资金申请并发**: 避免重复申请提交
- **活动修改并发**: 保障数据变更一致性
- **审批流程并发**: 防止重复审批决策

## 企业级安全机制

### Spring Security集成
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .cors(Customizer.withDefaults())
            .csrf().disable()
            .build();
    }
}
```

### 认证与授权
- **基于角色的访问控制 (RBAC)**: 学生、管理员、院系管理员
- **自定义UserDetailsService**: 灵活的用户认证机制
- **CORS配置**: 跨域资源共享支持
- **Session管理**: 会话状态维护

## 全面性能测试

### JMeter压力测试覆盖
```bash
测试场景包括:
├── Unit of Work性能测试 (1000并发线程)
├── 并发RSVP创建测试 (4个不同场景)
├── 并发资金申请测试 (提交/修改/审批)
├── 悲观锁性能测试
├── 懒加载性能测试 (500并发)
└── 事件管理并发测试
```

### 并发测试验证
- **死锁预防**: 有序锁获取策略
- **性能基准**: 高并发场景下响应时间
- **数据一致性**: 并发操作下的数据完整性
- **系统稳定性**: 长时间高负载运行测试

## 现代化部署

### Docker多阶段构建
```dockerfile
# 构建阶段
FROM maven:3.9-amazoncorretto-17 AS build
RUN mvn clean package -DwarName=api

# 运行阶段  
FROM tomcat:10.1.28-jre17
COPY --from=build /app/target/api-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/
```

### 云原生部署
- **Render平台部署**: 自动CI/CD流水线
- **环境隔离**: 开发/测试/生产环境分离
- **监控告警**: 实时性能监控
- **弹性扩缩容**: 根据负载自动调整

## 开发环境搭建

### 后端启动
```bash
cd src/api
mvn clean install
docker build -t student-club-api .
docker run -p 8080:8080 student-club-api
```

### 前端启动
```bash
cd src/ui
npm install
npm start
```

### 数据库初始化
```bash
psql -h localhost -U postgres -d student_club -f src/api/db/init.sql
```

