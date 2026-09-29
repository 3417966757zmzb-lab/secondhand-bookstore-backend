# 二手书店毕设学习笔记

本文件由 Codex 在每个项目学习日持续维护，记录通用知识点、常用 API、
易错点和当天复盘。

## 每日任务规则

见 [STUDY_RULES.md](STUDY_RULES.md)。规则有新增或更改时更新那个文件。

## P01 工程底座与第一个 Web 接口

### 项目事实

- 项目路径：`D:\Computer\Spring-boot-zzy\secondhandBookstore`
- 构建工具：Maven Wrapper
- Spring Boot：4.1.1
- Java 目标版本：17
- 根包：`org.example.secondhandbookstore`

### Maven 基础

- Maven 通过 `groupId + artifactId + version` 唯一标识依赖。
- `spring-boot-starter-parent` 管理常用依赖版本，子依赖通常不需要重复写版本。
- Starter 是一组相互兼容的依赖集合，不是一个单独的 Jar。
- `<scope>test</scope>` 表示依赖只在测试编译和测试运行时可用。
- 修改 `pom.xml` 后需要重新导入 Maven，必要时执行 Maven Wrapper 命令。

### 当前依赖

- `spring-boot-starter-webmvc`
- `spring-boot-starter-validation`
- `lombok`
- `mysql-connector-j`
- `spring-boot-starter-flyway`
- `flyway-mysql`
- `mybatis-plus-spring-boot4-starter`
- `spring-boot-starter-security` 暂时注释，P05 权限模块再恢复
- Redis 和 JWT 按 P04、P05、P06 进度接入
- 测试依赖当前不处理

### 练习 1：添加 WebMVC 依赖

标准答案：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

检查结论：

- 依赖位于 `<dependencies>` 内。
- XML 标签正确闭合。
- 没有手写版本号。
- `pom.xml` 配置正确。

### Java 版本踩坑

- 初始 `pom.xml` 使用 Java 21。
- 本机 `JAVA_HOME` 指向 `D:\计算机类\java`，实际版本为 Java 17。
- Maven 编译曾报错“不支持发行版本 21”。
- 已将 `<java.version>` 改为 `17`，终端确认 Java 17 后 Maven 编译成功。
- IntelliJ 的 `.idea/misc.xml` 仍记录 `JDK_21`；如 IDE 报错，需要将项目 SDK 和
  Maven Runner JRE 改为 Java 17。

### 本日待学注解

- `@RestController`：标识 REST 控制器，方法返回值写入响应体。
- `@RequestMapping`：设置 Controller 或方法的基础请求路径。
- `@GetMapping`：将方法映射到 GET 请求。

### 练习 2 检查结果

- 文件：`org.example.secondhandbookstore.health.controller.HealthController`
- 请求路径：`GET /api/health`
- 当前返回值：字符串 `OK`
- Maven 编译：通过
- 代码格式暂不作为失败条件；接口路径属于后续前后端契约，必须保持一致。

### P01 复盘已完成

P01 的复盘问题已全部回答并纠错。

### 练习 3：Service、依赖注入和配置读取

- `@Service` 将 Service 注册为 Spring Bean。
- 构造器注入可以保证依赖不为空，并让依赖声明为 `final`。
- `@Value("${app.name}")` 从配置文件读取值。
- `record` 适合不可变 DTO，自动生成构造器、访问方法、`equals`、`hashCode`
  和 `toString`。
- 代码文件按功能移动到 `health/controller`、`health/service`、`health/dto`。

### 练习 4：DTO、参数校验和 POST 接口

- `@PostMapping` 处理 POST 请求。
- `@RequestBody` 将 JSON 请求体转换为 Java 对象。
- `@Valid` 触发 DTO 上的 Bean Validation 校验。
- `@NotBlank` 同时拒绝 `null`、空字符串和纯空格。
- `@NotNull` 只拒绝 `null`。
- `@Pattern` 使用正则表达式校验字符串。
- `@Size` 限制字符串长度。
- `@DecimalMin` 限制最小数值。
- `@Digits` 限制整数位和小数位。
- 空值校验必须显式使用 `@NotNull` 或 `@NotBlank`，其他约束对 `null` 通常放行。

### 练习 5：统一响应和全局异常处理

- `Result<T>` 是统一响应类型，使用 `record` 实现不可变数据对象。
- `Result.success(data)` 返回成功响应。
- `Result.error(code, message)` 返回无数据错误。
- `Result.error(code, message, data)` 返回带错误详情的错误。
- `@RestControllerAdvice` 统一处理 Controller 异常。
- `@ExceptionHandler` 指定处理的异常类型。
- `ResponseEntity` 可以同时控制 HTTP 状态码和响应体。
- 参数校验采用方案二：返回“字段名 -> 错误消息”的 `Map<String, String>`。
- `getFieldErrors()` 可以获取全部字段错误。
- `LinkedHashMap` 保持错误字段的插入顺序。
- `putIfAbsent` 避免同一字段产生重复错误。
- 500 异常使用 `log.error("说明", exception)` 保留完整堆栈。

### P01 复盘结论

1. Controller 管 HTTP，Service 管业务，Mapper 管持久化，DTO 管接口数据。
2. `@Valid` 负责触发 DTO 校验；没有触发注解时，校验规则不会被自动执行。
3. JSON 类型转换与 Bean Validation 是两个阶段；类型转换失败通常抛
   `HttpMessageNotReadableException`，校验失败抛 `MethodArgumentNotValidException`。
4. HTTP 状态码表示错误类别：400 表示请求错误，500 表示服务器内部错误。
5. 业务状态冲突应使用更精确的状态码。例如商品已经被其他买家购买，应使用
   HTTP 409，而不是 404；404 表示资源不存在。
6. 业务异常由自定义异常类表示，使用 `@ExceptionHandler` 做精确映射，返回
   `ResponseEntity<Result<Void>>`。预期业务异常通常记录警告，不需要当成系统故障。

## P02 MySQL、Flyway、MyBatis-Plus 基础

### 依赖与配置

- MySQL 负责持久化数据。
- Flyway 负责数据库迁移，迁移文件位于 `src/main/resources/db/migration`。
- MyBatis-Plus 负责 Mapper CRUD 和查询条件构造。
- `application.yml` 配置了 MySQL 数据源和 MyBatis-Plus 自增主键策略。

### Flyway

- 迁移文件命名：`V1__create_category_table.sql`。
- Flyway 执行迁移后，会把版本和 checksum 写入 `flyway_schema_history`。
- 已经执行过的迁移文件不能随意修改，否则 checksum 不匹配，应用启动可能失败。
- 数据库结构变化应新增 `V2`、`V3`，不要回头改 `V1`。

### MyBatis-Plus

- `@TableName("bs_category")` 将实体映射到数据库表。
- `@TableId(type = IdType.AUTO)` 表示主键由数据库自增生成。
- `BookCategoryMapper extends BaseMapper<BookCategory>` 后，可以直接使用
  `insert`、`selectById`、`selectList`、`selectCount`、`updateById`、
  `deleteById` 等通用方法。
- Mapper 接口没有实现类，MyBatis-Plus 在运行时生成代理对象并执行 SQL。
- 不继承 `IService` 也能调用 Mapper CRUD。`IService` 是 MyBatis-Plus 对
  Service 层的额外封装，已在 P02-10 中将分类 Service 改为
  `IService + ServiceImpl` 风格。

### 查询条件

- `LambdaQueryWrapper` 使用方法引用指定字段，例如
  `BookCategory::getName`。
- 方法引用比字符串字段名更安全，字段写错时编译期直接失败。

### DTO 与转换

- `CategoryCreateRequest` 使用 `@Data` 和 `@NoArgsConstructor`，承载请求字段。
- `CategoryResponse` 使用 `@Data` 和 `@NoArgsConstructor`。
- Service 使用 `BeanUtils.copyProperties` 将 `BookCategory` 转成
  `CategoryResponse`。
- 不使用响应全参构造器时，不保留未使用的 `@AllArgsConstructor`。

### 分类模块

- `POST /api/categories` 新增分类。
- `GET /api/categories` 查询分类列表。
- 新增前先按 `name` 查询是否重复。
- 分类名重复时抛 `BusinessException(409, "分类名称已存在")`。
- `GlobalExceptionHandler` 使用 `ResponseEntity.status(e.getCode())` 返回真实
  HTTP 状态码。
- 参数校验失败返回 HTTP 400 和字段错误 Map。

### P02 复盘结论

1. Flyway 已执行迁移的 checksum 不能变化，否则启动时会校验失败。
2. `IdType.AUTO` 表示数据库自增生成主键，MyBatis-Plus 会把生成的 id 回填到实体。
3. `BaseMapper` 提供通用 CRUD 方法声明，MyBatis-Plus 运行时生成实现。
4. `LambdaQueryWrapper` 的字段引用能在编译期发现字段写错。
5. 重复分类名应抛 `BusinessException`，由全局异常处理器返回真实 HTTP 409。
6. Service 不返回 `Result`，避免把 HTTP 响应逻辑混进业务层。

### P02 验证证据

- 合法新增分类返回 HTTP 200。
- 查询分类列表能返回刚新增的数据。
- 重复新增分类返回 HTTP 409。
- 非法参数返回 HTTP 400，`data` 包含字段级错误。
- Maven 编译通过，应用启动通过，Flyway 校验 1 个迁移成功。

### P02 收尾结论

- P02-10 已完成，分类 Service 使用 `IService + ServiceImpl` 风格。
- P02 全部基础任务已完成，下一个编号进入 P03：用户表、注册和 BCrypt。

## 登录退避与 Redis 计数

### 登录退避标准

前三次失败不等待，之后按以下时间递增：

| 连续失败次数 | 下次尝试前等待 |
| --- | --- |
| 1 至 3 次 | 0 秒 |
| 第 4 次 | 10 秒 |
| 第 5 次 | 30 秒 |
| 第 6 次 | 1 分钟 |
| 第 7 次 | 2 分钟 |
| 第 8 次 | 4 分钟 |
| 第 9 次 | 8 分钟 |
| 第 10 次及以后 | 15 分钟 |

### Redis 实现要点

- 使用 Redis 原子计数保存失败次数，计数键设置合理 TTL。
- 按“账号 + IP”组合记录，避免只按账号计数导致恶意锁定其他用户。
- 退避期间使用独立键保存截止时间，剩余时间返回给前端。
- 检查退避、增加计数和设置等待时间应尽量使用 Redis 原子命令或 Lua 完成。
- 登录成功后清除失败计数和退避记录。
- 凭据错误返回 HTTP 401；仍在退避期返回 HTTP 429 和 `Retry-After`。
- 不使用异常消息判断错误类型，应定义明确的凭据异常。
- 只有应用位于可信反向代理之后，才允许读取 `X-Forwarded-For` 获取客户端 IP。
