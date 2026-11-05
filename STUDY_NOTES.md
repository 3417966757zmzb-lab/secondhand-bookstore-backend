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

## P03 工程基线、用户模型与 BCrypt 注册

### 错误码与响应链路

- `ErrorCode` 统一定义业务码、默认消息和 HTTP 状态，业务码与 HTTP 状态不混用。
- `BusinessException` 保存 `ErrorCode`，由全局异常处理器返回真实 HTTP 状态。
- `requestId` 在过滤器中生成，写入 MDC 和响应头 `X-Request-Id`，再写入 `Result`。
- 请求结束必须在 `finally` 中清理 MDC，避免 Tomcat 线程复用时污染后续请求日志。

### 用户模型与注册规则

- `USER`、`ADMIN` 是权限角色，买书和卖书是业务行为，不区分互斥的买家、卖家角色。
- `username`、`email` 统一去除首尾空格并转为小写；昵称只去首尾空格。
- 当前注册规则为用户名 4 至 20 位、密码 8 至 30 位、昵称最多 20 位、邮箱最多 100 位。
- 数据库使用唯一约束保证用户名和邮箱唯一，提前查询只用于友好提示，不能代替唯一索引。

### BCrypt 与并发

- BCrypt 每次加密使用随机盐，盐和算法参数包含在编码后的哈希字符串中。
- 登录验证使用 `PasswordEncoder.matches`，不需要也不能还原原始密码。
- 并发注册时两个请求可能都通过 `exists` 检查，最终由数据库唯一索引拒绝后插入的一条。
- MySQL 重复键异常由 MyBatis 转换为 `DuplicateKeyException`，全局处理器返回 HTTP 409。

### P03 验证证据

- 合法注册返回 HTTP 200，响应不包含密码字段。
- 参数校验失败返回 HTTP 400。
- 重复用户名返回 HTTP 409 和业务码 `30001`。
- 重复邮箱返回 HTTP 409 和业务码 `30002`。
- 数据库只保存 BCrypt 哈希，长度为 60，`status=ENABLED`，`role=USER`。

### P03 收尾结论

- P03 已完成，下一个阶段为 P04 登录和 JWT。

## P04 登录和 JWT

### JWT 基础

- JWT 结构为 `Header.Payload.Signature`，前两段只是 Base64URL 编码，不是加密。
- 签名用于验证 Header 和 Payload 未被篡改；HS256 使用 HMAC-SHA256 和密钥。
- 从签名不能反推出密钥或 Payload，但 Payload 任何持有者都能直接解码。
- JWT 无状态表示服务端不保存 Session，但服务端仍保存签名密钥，并可通过 Redis 黑名单实现主动失效。
- `jti` 是单个令牌的唯一标识，用于按令牌退出、强制失效、审计和重放排查，不用于证明令牌属于哪个用户。
- 黑名单 key 可使用 `jwt:blacklist:<jti>`，TTL 与该令牌剩余有效期一致；过期令牌不需要写黑名单。

### 令牌声明与配置

- 当前令牌声明：`sub`（用户名）、`userId`、`role`、`jti`（UUID）、`iss`、`iat`、`exp`。
- `JwtProperties` 使用 `@ConfigurationProperties(prefix = "app.jwt")` 绑定 `secret`、`issuer`、`expiration`。
- `secret` 通过环境变量 `JWT_SECRET` 注入；`expiration` 使用 `Duration`，配置值 `2h`。
- JJWT 0.11.5 依赖：`jjwt-api` 编译接口，`jjwt-impl` 和 `jjwt-jackson` 运行时实现。
- `Keys.hmacShaKeyFor` 要求 HMAC 密钥至少 256 位，也就是至少 32 字节。

### 登录流程

- `AuthService` 是流程编排服务，没有单一 Entity 和 Mapper，使用普通 `@Service`。
- 账号先 `trim()` 并转小写，再按用户名或邮箱查询。
- 用户不存在、账号被禁用、`passwordEncoder.matches` 返回 false 时，统一抛 `INVALID_CREDENTIALS`。
- 登录失败统一返回 HTTP 401、业务码 `40001` 和“账号或密码错误”，不泄露账号是否存在。
- 登录成功返回 `accessToken`、`tokenType=Bearer`、`expiresIn`（秒）和必要的非敏感用户信息。
- `AuthController` 只负责 HTTP 层，提供 `POST /api/auth/login`。

### 常见易错点

- 密码校验必须使用 `!passwordEncoder.matches(...)`；漏掉 `!` 会把正确密码拒绝、错误密码放行。
- `tokenType` 使用标准写法 `Bearer`。
- JWT 的签名不等于加密；签名不能隐藏 Payload 内容。
- 认证过滤器必须解析并校验令牌后再检查 `jti` 黑名单。

### P04 验证证据

- 正确账号密码返回 HTTP 200，响应包含 JWT、`Bearer`、`expiresIn=7200` 和必要用户信息。
- 登录响应包含 `requestId`，不包含 `passwordHash`、`phone`、`email`。
- JWT Payload 已解码核对：`sub`、`userId`、`role`、`jti`、`iss`、`iat`、`exp` 正确。
- 错误密码返回 HTTP 401、业务码 `40001`、消息“账号或密码错误”。
- 禁用账号返回同一 HTTP 401 和业务码，测试后已恢复 `ENABLED`。
- 篡改和过期令牌的端到端 HTTP 验证调整到 P05 认证过滤器完成后执行。

### P04 收尾结论

- P04 已完成，下一个阶段为 P05 认证授权、登录退避与 JWT 黑名单。
## P05 认证授权、登录退避与 JWT 黑名单（进行中）

### Spring Security 自动配置的实际情况（Boot 4.1.1 + Security 7.1.1）

- Spring Boot 4.1.1 管理的 Spring Security 版本是 `7.1.1`。
- `HttpSecurityConfiguration.httpSecurity()` 默认注册的配置器包括 csrf、exceptionHandling、
  headers、sessionManagement、securityContext、requestCache、anonymous、servletApi、logout；
  另有 `applyDefaultConfigurers`，它通过 `SpringFactoriesLoader` 加载 `AbstractHttpConfigurer` 扩展。
- `formLogin` 和 `httpBasic` 默认不启用：即使不写 `disable()`，也不会出现登录页和 Basic 弹窗。
- 未配置 entry point 时，未认证访问受保护接口的默认行为是 `Http403ForbiddenEntryPoint`，
  即一个没有响应体的 403。

### 认证过滤器的边界

- 自定义过滤器要插在 `UsernamePasswordAuthenticationFilter` 之前；`ExceptionTranslationFilter`
  位于它后面，因此过滤器自身抛出的异常不会被翻译成 401/403，而是直接变成 500。
- `@RestControllerAdvice` 只处理 DispatcherServlet 内部的异常，接不到过滤器层的异常。
- `JwtAuthenticationFilter` 捕获 `JwtException`（`ExpiredJwtException`、
  `io.jsonwebtoken.security.SignatureException`、`MalformedJwtException`、
  `IncorrectClaimException` 的公共父类）后不设置认证并放行，把"未认证"交给授权规则拒绝。
- 空字符串令牌会抛 `IllegalArgumentException`（`Assert.hasText`），它不是 `JwtException`，
  所以 `Authorization: Bearer ` 这种请求必须在解析前用 `isBlank()` 挡掉，否则会变成 500。
- principal 放 `userId`，权限放 `ROLE_` 加角色名；`hasRole("ADMIN")` 实际检查 `ROLE_ADMIN`。
- `@Component` 的 Filter 会被 Boot 额外注册成 Servlet 过滤器，但 `OncePerRequestFilter`
  用请求属性防止重复执行，功能上不受影响。

### 认证与授权失败的分流

- 分流规则不是"有没有带令牌"，而是当前身份是否为匿名：`AuthenticationException` 走 401；
  `AccessDeniedException` 且当前匿名也走 401；已认证但权限不足才走 403。
- `AnonymousAuthenticationFilter` 在上下文为空时放入 `AnonymousAuthenticationToken`
  （principal 为 `anonymousUser`，权限为 `ROLE_ANONYMOUS`），所以上下文永远不会是 null。
- `AnonymousAuthenticationToken` 的构造器调用了 `setAuthenticated(true)`，因此匿名身份的
  `isAuthenticated()` 返回 **true**；判断"是否真的登录"必须用
  `AuthenticationTrustResolver.isAnonymous(...)`。
- `permitAll()` 允许匿名访问；`authenticated()` 要求非匿名。
- 认证失败入口和权限不足处理器运行在过滤器层，不能用 `ResponseEntity`，必须自己设置状态码与
  `Content-Type`，再用 `ObjectMapper` 写 JSON；写响应失败时让 `IOException` 向上抛，
  不要包装成业务异常（那时已经没有人能渲染它）。
- Jackson 3（`tools.jackson`）的异常是非受检的 `JacksonException`，不是 `IOException`。
  Boot 4 自动配置提供的是 `tools.jackson.databind.json.JsonMapper`，它继承
  `tools.jackson.databind.ObjectMapper`，按父类型注入即可。
- 这两个处理器只依赖 `Result`，不依赖 auth 业务类，因此放在 `common/security`；
  而 `JwtAuthenticationFilter` 依赖 `JwtService`，属于 auth 功能，放在 `auth/filter`。

### 统一异常处理的补漏

- 访问不存在的路径时，Spring MVC 由 `ResourceHttpRequestHandler` 抛
  `NoResourceFoundException`（implements `ErrorResponse`，语义是 404）。如果 advice 只有
  `@ExceptionHandler(Exception.class)` 兜底，它会被转成 500，必须在 advice 中显式处理。
- HTTP 方法与接口不匹配时抛 `HttpRequestMethodNotSupportedException`，应映射为 405。
- 通用错误码：10006 = 资源不存在（404），10007 = 方法不支持（405），
  40002 = 未认证（401），40003 = 权限不足（403）。

### 调试接口的通用易错点

- **自动跟随重定向会把一次请求变成两次**：`POST /logout` 返回 302 跳 `/login?logout`，
  客户端跟随后的第二次请求才决定你看到的状态码，403、404、500 都可能是它造成的。
  调试时用 `curl.exe -i`（不跟随重定向），或查看请求历史里的第一条。
- Spring Security 自带的 `/logout` 只清 Session、清 Cookie 并重定向 `/login?logout`，
  对无状态 JWT 没有任何登出效果；真正的登出必须把 `jti` 写入黑名单。
- 打开 `logging.level.org.springframework.security=DEBUG` 后，启动日志中的
  `Will secure any request with [...]` 就是真实的过滤器链，是排查授权问题最直接的手段。
- 角色改动后必须重新登录才生效，因为角色写在令牌声明里（信任令牌声明方案的已知代价）。

### P05 验证证据（截至 2026-10-12）

- 不带令牌访问受保护接口：HTTP 401 加业务码 40002，响应体含 `requestId`。
- 篡改令牌：HTTP 401 加 40002（不是 500，也不是 403）。
- 带合法令牌访问 `GET /api/categories`：HTTP 200。
- 普通用户 `POST /api/categories`：HTTP 403 加业务码 40003。
- 管理员 `POST /api/categories`：HTTP 200（重复名称 409）。
- 未知路径 HTTP 404 加 10006；`POST /api/health` HTTP 405 加 10007。
### P05-06 Redis 接入与登录退避设计（2026-10-12）

- Spring Boot 4 的 Redis 属性前缀是 `spring.data.redis`（`DataRedisProperties`），
  不是旧版的 `spring.redis`；字段包含 host、port、database、username、password。
- `spring-boot-starter-data-redis` 默认客户端是 Lettuce，Boot 会自动配置
  `RedisConnectionFactory`、`RedisTemplate<Object,Object>` 和 `StringRedisTemplate`。
- `RedisTemplate` 默认使用 JDK 序列化，key 在 redis-cli 里会显示成 `\xac\xed...` 乱码；
  `StringRedisTemplate` 的构造器把 key、value、hashKey、hashValue 四个序列化器都设为
  `RedisSerializer.string()`，所以业务读写统一用它，值使用字符串或 JSON。
- `ttl` 返回值：正数为剩余秒数；-1 为键存在但没有过期时间（防泄漏时要排查的信号）；
  -2 为键不存在。
- YAML 里重复的顶层键会让后面的整块覆盖前面的，加配置时要并入已有的 `spring:` 块。

### 登录退避的设计要点

- 服务端退避**不能在请求线程里 sleep**，只返回 429 和 `Retry-After`，等待由客户端完成。
- 记录维度用「账号 + IP」而不是只按账号：只按账号会让攻击者用错误密码把别人锁死，
  等于把防护机制变成攻击工具。
- 计数窗口的 TTL **必须严格大于最大退避时长**，否则攻击者每熬过一次退避就能把计数清零。
  实测推算：15 分钟窗口下，第 10 次失败最早发生在第 1 次失败后 940 秒，超出窗口，
  最后一档永远无法达到。取最大退避的两倍（30 分钟）即可。
- 滑动窗口（每次失败刷新 TTL）比固定窗口更贴合退避语义：只要还在失败就持续累加；
  静默超过窗口才清零。
- 抖动摊平的是"大量账号同时解封后的重试波峰"，**不阻止同一秒的并发请求**；
  抖动只在写退避截止时间时执行一次，`Retry-After` 由该键剩余秒数计算，避免两者不一致。
- 退避档位的需求值不是 2 的幂，位运算或 `Math.pow` 公式无法精确表达，
  用 `static final int[]` 查表最贴切（O(1)、零分配、改档位只改数据）。
- Lua 脚本要点：KEYS 与 ARGV 必须用参数传递，不能把值拼进脚本文本；
  本版本 `DefaultRedisScript` 只有构造器（没有静态 `of`）；
  `return count` 对应 Java 侧 `Long.class`，写成 `tostring` 会抛类型转换异常；
  脚本对象放 `static final` 字段复用，才能走 `EVALSHA`。
- Redis 往返预算：检查退避 1 次 `GET`，记录失败 2 次（`EVALSHA` 加 `SET`），
  成功清理 1 次 `DEL`。接口耗时取决于 Redis 往返次数，不是几次 if 比较。
- 已知局限：换 IP 可绕过「账号 + IP」维度；分布式爆破需要账号维度的限速与验证码或告警，
  但不能直接锁定账号，否则又会变成锁死真实用户。
### P05-07 登录退避的排查与踩坑（2026-10-17）

- **触发退避的那次失败仍然返回 401**，429 由"被退避挡住的请求"返回。所以第 4 次失败之后，
  第 5 次请求才会拿到 429；想让第 4 次就返回 429，等于要求系统在失败发生前预知它，逻辑上不可能。
- 档位数组的**下标就是失败次数**，"多少次以内不等待"由独立常量 `NO_DELAY_MAX_FAILURES` 控制。
  两个常量必须一起看：只改数组或只改门槛，会让某些档位变成永远读不到的死数据，
  或者让整条档位曲线整体前移一格。
- Lua 脚本字符串拼接时如果漏掉空格，语句会粘成一行（`...KEYS[1])redis.call(...)`）。
  Lua 不强制语句分隔符，所以**能跑**，但可读性和可维护性都很差，拼接时必须显式留空格。
- `DefaultRedisScript` 的结果类型写 `Long.class`；写 `long.class` 也能工作，因为 `long.class`
  的类型就是 `Class<Long>`，且 Spring Data Redis 的 `ReturnType` 判断包含原始类型。
- 向上取整要写 `(millis + 999) / 1000`；写成 `millis / 1000 + 1` 在整除时会多报 1 秒。
- 档位表属于需求的一部分：改动它必须同时同步流程文档、进度文档和论文里的档位表，否则
  自测步骤会和代码行为对不上（本次就出现过"文档写第 4 次是 401、代码实际返回 429"）。
### P05-10 与 P05-11 的排查结论（2026-10-21）

- **黑名单判断条件写反属于安全反转**：写成"在名单里才设置认证"，会导致正常令牌全部 401，
  而已登出的令牌反而能通过认证。正确条件是 `jti == null || !isBlacklisted(jti)` 才设置认证。
  这类条件取反的 bug，一条端到端请求就能暴露；写条件时要用一句话复述它的含义。
- **验证登出效果不能用公开接口**：`GET /api/categories` 是 `permitAll`，登出后依然返回 200。
  必须用受保护接口（例如 `POST /api/book-drafts/preview`）才能证明令牌真的失效。
- 黑名单键的 TTL 应等于令牌剩余有效期：实测登录后立即登出，TTL 为 7199（上限 7200），
  而不是固定值；已经过期的令牌不需要写入黑名单。
- 本机开发时 `getRemoteAddr()` 返回 IPv6 回环 `0:0:0:0:0:0:0:1`，退避键里会带一串冒号；
  按 IP 做统计或白名单时要注意 IPv4 与 IPv6 是两种写法。
- 关闭 Spring Security 自带的 `/logout` 后，无令牌访问 `/logout` 返回 401（不再是 302 跳
  `/login?logout`），说明请求回到了授权规则，而不是被 `LogoutFilter` 处理。
### P05 复盘结论（2026-10-22）

- 令牌可用性 = `exp` 未过期 且 不在黑名单；签名只保证"真实性"，不保证"还能用"。
  黑名单按 `jti` 提供"提前作废"能力，粒度是单个令牌；全端下线要靠 `userId + iat` 或令牌版本号。
- 没有签名校验时，攻击者可以自造令牌并伪造 `role=ADMIN`，黑名单拦不住（换一个 `jti` 即可）。
- Lua 的价值是消除中间状态：`INCR` 与 `EXPIRE` 分开执行时，若 `EXPIRE` 失败会留下无 TTL 的键
  （`ttl` = -1），窗口语义失效，很久以前的失败会被当成连续失败；并发下也可能漏设 TTL。
- 退避按「账号 + IP」而不是只按账号：只按账号会让攻击者用错误密码把真实用户锁死（账号锁定型
  DoS）。代价是换 IP 可绕过，分布式爆破需要账号维度的限速、验证码或告警，而不是直接锁定账号。
- 黑名单 TTL 若写死成 24 小时：安全性不会提升（令牌 `exp` 到了本来就无效），只是把无效数据在
  Redis 里多留约 12 倍时间，内存被无意义占用；共享 Redis 上还可能触发淘汰策略影响别的键。
- **过期令牌不是"验签失败"**：签名可能完全有效，是 `exp` 声明校验把它拒掉；两者异常类型不同
  （`ExpiredJwtException` 与签名异常）。已经过期的令牌不需要写入黑名单。
- 客户端 IP：默认不读 `X-Forwarded-For`；只有部署在可信反向代理后才打开开关。取第一个值的前提是
  代理**覆盖**该头（`$remote_addr`），若用追加模式（`$proxy_add_x_forwarded_for`），最左值可被伪造。
### P05-13 端到端验证（2026-10-22）

- 验证过期路径时，可以临时把令牌有效期设为 5 秒：环境变量 `APP_JWT_EXPIRATION=5s` 会覆盖
  `application.yml` 里的 2 小时；验证完成后必须移除该变量并恢复正常配置。
- 验证结果：无令牌 401/40002；篡改令牌 401/40002；普通 USER 访问 `POST /api/categories`
  为 403/40003；4 次错误密码 401、第 5 次 429 且 `Retry-After: 9`；登出 200、随后同一令牌
  401（黑名单键 value=1、ttl=4）；过期令牌访问 401，过期令牌登出也 401，且
  `jwt:blacklist:<jti>` 不存在。
- 三层机制协同正常：`exp` 处理过期、黑名单处理提前作废、签名处理篡改；过期令牌在过滤器阶段
  就被拒，不会进入 Controller，因此不会产生黑名单垃圾键。
- 清理测试数据时只删除自己创建的键，不要用 `keys` 的结果批量删除；共享 Redis 上可能误删
  别人的退避计数或业务键。

## P06 分类缓存接入与验证（2026-11-02）

### redis-cli 的输出格式会骗人

- **非交互输出会自动转义**：标准输出不是 tty（IDE 终端捕获、管道、重定向）时，redis-cli
  对字符串回复用双引号包裹、把 `"` 转成 `\"`、把非 ASCII 字节按 `\xNN` 逐字节转义。
  看到 `"[{\"id\":1,\"name\":\"\xe8\xae\xa1\xe7\xae\x97\xe6\x9c\xba\"}]"` 时很容易误判成
  "双层 JSON 编码"或"序列化失败"，实际存储的是 `[{"id":1,"name":"计算机"}]`。
- `\xe8\xae\xa1\xe7\xae\x97\xe6\x9c\xba` 是 `计算机` 的 UTF-8 字节（每字 3 字节）。
- 判断真实内容用 `redis-cli --raw get <key>`；`--no-raw` 是强制转义格式，不要用它排查。
- 更硬的证据是把 `--raw` 的结果直接交给 JSON 解析器，确认类型、条数和字段，而不是用眼睛看。

### Spring Cache 注解语义

- `@Cacheable` 命中时不执行方法体，SQL 不会发出；未命中才执行并写回，默认会缓存返回值。
- `@CacheEvict` 默认 `beforeInvocation = false`，即**方法正常返回后**才清除；方法抛异常时
  缓存保持原样。这正好符合"写失败不该删缓存"的要求，但必须知道它依赖默认值。
- `allEntries = true` 清除缓存名下的所有键，适合"一个缓存名对应多种键"的结构；代价是
  `RedisCache.clear()` 走默认 `BatchStrategies.keys()`，实际发出 `KEYS 前缀*`，Redis 单线程
  下会阻塞。键集合固定时，用 `key = "'all'"` 做精确 `DEL` 是 O(1) 的更优选择。
- `@CacheEvict` 与数据库写入不在同一事务时，先提交后清除的顺序是相对的；一旦方法加上
  `@Transactional`，清除会发生在提交之前，并发读可能把旧值写回缓存。正确做法是
  `@TransactionalEventListener(AFTER_COMMIT)` 或在 `afterCommit` 回调里删除。
- 同类内部 `this.xxx()` 调用不经过代理，`@Cacheable` / `@CacheEvict` 不生效；跨 Bean 调用才走代理。

### 缓存序列化

- `RedisCacheConfiguration` 默认前缀是 `缓存名 + "::"`，所以 `prefixCacheNameWith("cache:")`
  之后的真实键是 `cache:catalog:categories::all`，不是 `cache:catalog:categories:all`。
- `GenericJacksonJsonRedisSerializer` 会写入 `@class` 类型信息，值可以反序列化成原类型；
  `JacksonJsonRedisSerializer(JavaType)` 不写类型信息，体积更小，但**读取端的类型必须与写入端
  完全一致**，改 DTO 结构后旧缓存可能反序列化失败。
- `List<CategoryResponse>` 要用 `TypeFactory.constructCollectionType` 构造 `JavaType`，
  否则读回来是 `List<LinkedHashMap>`，强转时抛 `ClassCastException`。
- Jackson 3（`tools.jackson`）已内置 `java.time` 支持，`LocalDateTime` 默认写成 ISO-8601 字符串，
  不需要单独注册 `JavaTimeModule`。
- Java 8 时间类型的字符串形式跨版本可读；写成时间戳数组时，重启或换 Jackson 版本后容易读不回来。

### 证明"命中缓存"的正确方法

- 连续两次调用接口、返回值相同**不能**证明命中，数据库没变时本来也一样。
- 有效证据是绕过应用直接改库：`UPDATE bs_category SET name='X' WHERE id=?`，再调接口，
  若仍返回旧名字，说明这次没有查库；验证后必须把数据改回去。
- 写后失效的证据是：写接口返回 200 后 `EXISTS key` 为 `0`，下一次查接口键重新出现且值为新值。
- 重启恢复的证据是：重启应用后直接查接口返回正确内容，这同时证明反序列化配置可用——
  序列化问题只在第一次读缓存时暴露，启动健康检查证明不了。

### 已知限制（写进论文）

- 分类列表缓存是单键整表，数据量小；缓存穿透（查不存在的数据）与缓存击穿（热键过期瞬间）
  在这张表上不构成主要问题，不额外做空值缓存或互斥重建。
- 直接改库、多实例部署、Redis 写入失败都会让缓存与数据库短暂不一致，最终由 30 分钟 TTL 兜底。
- `allEntries = true` 的 `KEYS` 扫描在共享 Redis 上可能影响其他业务，属于首版接受的代价。

### P06 复盘结论（2026-11-02）

**缓存与事务的时序**

- `@CacheEvict` 默认在方法**正常返回后**执行，但它不等于"事务提交后"。方法返回时事务还没提交，
  清除已经发生。
- 危险时序：事务内更新 → 清缓存 → 并发读未命中 → 读到未提交前的旧值 → 把旧值写回缓存
  → 事务提交。结果是数据库新值、缓存旧值，且没有后续写操作去纠正，脏到 TTL 过期。
- `beforeInvocation = false` 只保证"方法抛异常时不清缓存"，不解决"提交失败"的问题。
- `@EnableTransactionManagement` 与 `@EnableCaching` 的 advisor order 默认都是
  `LOWEST_PRECEDENCE`，谁在外层没有保证，不能靠"碰巧"。
- 标准做法是 `TransactionAwareCacheManagerProxy` 包装 CacheManager，把 `put`、`evict`、`clear`
  推迟到 `afterCommit`；或者 `@TransactionalEventListener(phase = AFTER_COMMIT)`。
  `afterCommit` 里 Redis 失败不能回滚数据库，只能记录告警。

**穿透、击穿、雪崩**

- 穿透：key 在缓存和数据库都不存在，每次穿过缓存打库；前提是 key 来自外部输入。
- 击穿：**单个热点 key** 过期的瞬间，大量并发同时未命中回源。
- 雪崩：**大量 key 同时过期**或缓存整体不可用。
- 分类列表缓存 key 是常量 `'all'`，外部无法构造不存在的 key，所以**穿透不成立**，
  空值缓存和布隆过滤器都用不上；只有以后加 `getCategory(id)` 这类按 ID 查询的缓存才需要讨论。
- 分类列表是单键热点，**击穿成立**：过期瞬间并发全部回源。数据量小、查询毫秒级，
  影响是连接池里多一批重复查询，不构成引入分布式锁的理由。
- **雪崩不成立**：缓存名下只有一个键。
- 更深一层的现实风险：`RedisCache` 在 Redis 不可用时默认抛异常，请求变 500；
  当前没有 `CacheErrorHandler`，属于"Redis 挂了接口就挂"，需要写进论文已知限制。

**深分页**

- MyBatis-Plus 的 `Page(page, size)` 换算为 `LIMIT (page-1)*size, size`；`LIMIT a, b`
  的 a 是**行数**不是页码，`page=9999&size=10` 的偏移是 99980。
- `offset` 是计数值，B+ 树无法按位置定位，InnoDB 必须从头按索引顺序走到第 offset+1 行，
  成本与 offset 成正比；丢掉的行走不掉，必须被读过。
- 分页还会额外发一次 `SELECT COUNT(*)`，深页时同样要付。
- 缓解手段：限制最大页码（后台列表最适合）、延迟关联子查询（保留跳页，减少宽列读取）、
  游标分页 `WHERE id > :lastId`（O(size)，但不能跳页，适合"加载更多"）。

**索引与模糊搜索**

- `LIKE '%kw%'` 的前导 `%` 让 B+ 树无法定位起点，索引失效，走全表扫描；
  `LIKE 'kw%'` 是范围查询，能走索引；`LIKE '%kw'` 也不行。
- 前后模糊的办法：MySQL 8 的 `FULLTEXT ... WITH PARSER ngram` 建全文索引配合
  `MATCH ... AGAINST`；或引入 Elasticsearch（首版明确不做）；或接受全表扫描并用
  分类、价格等条件先缩小范围。
- 反向列/函数索引只能解决后缀匹配，解决不了任意位置匹配。
- 当前书籍版本表数据量小，首版 `LIKE '%kw%'` 接受全表扫描，限制页大小即可，
  优化方向写进论文。

### P06 收尾结论

- P06 全部完成：书籍版本增改查、分页、分类层级与引用保护、Redis 分类缓存。
- 下一步 P07：二手商品发布与图片存储。

### P07-01 复习补充（2026-11-04）

- 分页的“越界”和“非法”是两回事：`page` 大于总页数是合法请求，返回 200、空 `records`，
  `total` 仍为全表总数；只有 `page < 1` 或 `size` 超出范围才是 400。
  `PaginationInnerInterceptor.setMaxLimit` 对超限的 `size` 是**静默截断**而不是报错，
  所以 Service 仍需显式校验，避免调用方把截断后的结果当成真实分页。
- `@CacheEvict` 有两个独立维度：`beforeInvocation` 决定清除时机（默认 false，方法正常返回后），
  `allEntries` 决定清除范围（整个缓存名还是单个键）。方法抛异常时默认不清缓存，
  与 `allEntries` 的取值无关；`allEntries = true` 时不能同时指定 `key`。
- 分类删除的 409/20007 有两条路径：Service 查到子分类后主动拒绝（快速失败），
  数据库外键 `ON DELETE RESTRICT` 拒绝删除（最终保护）；分类不存在是另一条 404/20005。
- “先查后删”不是原子操作：并发新增**书籍版本**引用时，由 `bs_book_edition` 的外键兜底；
  但 `bs_category.parent_id` 没有自引用外键（根分类用 0，也无法直接引用不存在的行），
  并发新增**子分类**仍可能在检查与删除之间插入，留下孤儿分类。这是当前已知缺口：
  首版接受并写入论文限制，或在后续迁移中改为可空父级加自引用外键。
- 分类被引用时不能级联删除（越过业务意图、不可恢复），也不能把 `category_id` 置空
  （该列 `NOT NULL`，且会产生无法按分类检索的孤儿数据），因此返回 409 让调用方先处理引用。

### P07-02 方案选定（2026-11-04）

- 采用平衡版：品相 `condition_rank TINYINT`（1 最差至 5 近乎全新，`CHECK BETWEEN 1 AND 5`），
  状态 `VARCHAR(20)` + `CHECK IN (...)`，Java 枚举按 `name()` 映射；审核字段 `reviewer_id`、
  `reviewed_at`、`reject_reason` 在 P07 一次建好，P08 不再改表。
- 品相用数字而非字符串：P09 的“九成新以上”可以直接写 `condition_rank >= 4`，是范围查询，
  索引可用且可排序；字符串只能枚举 `IN`，范围语义要回到 Java 翻译。数字不直观的问题由
  列注释、Java 枚举标签和前端展示补偿。
- 状态用 `VARCHAR + CHECK` 而非 MySQL `ENUM`：状态名可读、日志和论文直接可用；新增状态
  只需改 `CHECK`，没有 `ENUM` 的序号和空字符串问题。代价是与 `bs_user` 的 `ENUM` 风格不统一，
  这一点写入论文说明。
- 图片表 `bs_listing_image` 独立存放，`listing_id` 外键 `ON DELETE CASCADE`，索引
  `(listing_id, sort_order)`；不建 `(listing_id, sort_order)` 唯一键，避免拖拽换序时撞键。
- 索引按查询建：卖家列表 `(seller_id, status, id)`、审核/公开列表 `(status, id)`、
  按版本查在售 `(book_edition_id, status)`。
- 品相和售价在创建草稿时就要求填写（`NOT NULL`），首版不支持半成品草稿；若以后要支持，
  需改为可空，并在提交审核时统一校验。
- 未选简单版：P08 要再改表、P09 要补索引；未选严谨版：状态日志、字典表和乐观锁更适合
  放到 P11 讲并发时再加，P07 先控制复杂度。

### P07-03 迁移验证（2026-11-04）

- 用户启动应用后确认 V5 执行成功，数据库中已创建 `bs_book_listing` 和 `bs_listing_image` 两张表。
- 表结构包含品相与状态 `CHECK`、卖家/书籍版本/审核人外键、三个查询索引和图片排序索引。
- 接下来进入 P07-04a：枚举、Entity 和 Mapper。

### P07-04a 持久层骨架（2026-11-04）

- `BookListing` 映射 `bs_book_listing`；`status` 用 `ListingStatus` 枚举，MyBatis 按 `name()`
  对应 `VARCHAR`；`condition_rank` 用 `Integer`，不引入额外 TypeHandler，品相标签由
  `ListingCondition.resolveByRank` 提供。
- 金额用 `BigDecimal`，时间用 `LocalDateTime`。
- MyBatis-Plus 默认驼峰转下划线，`bookEditionId` 自动对应 `book_edition_id`，
  不需要逐字段写 `@TableField`。
- Mapper 使用 `@Mapper`，因为启动类没有配置 `@MapperScan`。

### P07-04b 商品 DTO（2026-11-04）

- 请求 DTO `BookListingRequest` 只接收 `bookEditionId`、`conditionRank`、`price`、`description`；
  卖家 id、商品状态、审核人和审核时间都由服务端决定，不放进请求体，避免越权。
- `conditionRank` 用 `@NotNull` + `@Min(1)` + `@Max(5)`；`price` 用 `@NotNull`、
  `@DecimalMin("0.01")`、`@Digits(integer = 8, fraction = 2)`，与 `DECIMAL(10,2)` 对应；
  `description` 可空，最多 2000 字符。
- 响应 DTO `BookListingResponse` 包含书籍标题、作者、封面，卖家列表不必再单独查书籍；
  `conditionLabel` 由 `ListingCondition` 转换；`status` 直接用枚举，JSON 输出为名称；
  不返回 `reviewerId`，避免暴露后台人员身份。

### P07-05 首轮检查（2026-11-04）

- 可选字段只加 `@Size` 时仍然允许 `null`；Service 里直接调用 `getDescription().strip()`
  会抛 `NullPointerException`。可空字段必须先判空，再决定是否 `strip()`。
- 引用类错误码要与业务语义一致：分类引用用 400/20002，书籍版本引用也应使用
  `BOOK_EDITION_REFERENCE_NOT_FOUND`（20008/400），而不是复用详情接口的
  `BOOK_EDITION_NOT_FOUND`（404/20004）。
- 同一请求内重复查询同一书籍版本会多一次 SQL，两次查询之间还可能发生状态变化；
  复用第一次查询的结果。
- REST 资源路径统一用复数：`/api/listings`，与 `/api/categories`、`/api/book-editions` 一致。
- 以上四点已于 2026-11-04 修正：空描述返回 200 且保存为 `NULL`，非法书籍版本返回 400/20008，
  路径统一为 `/api/listings`，书籍版本只查一次。

### P07-05 收尾（2026-11-04）

- `POST /api/listings` 验证通过：200、`status=DRAFT`、品相标签正确、书籍标题/作者/封面
  和价格来自数据库，响应不含 `sellerId` 与 `reviewerId`。
- 空描述返回 200 且数据库保存 `NULL`；不存在的书籍版本返回 400/20008；数据库核对
  `seller_id` 等于令牌中的 `userId`，`reviewer_id` 为 `NULL`。
- 接口约定：`/api/listings` 需要登录，卖家身份只从 `@AuthenticationPrincipal Long` 获取。