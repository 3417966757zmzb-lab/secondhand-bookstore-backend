# 二手书店系统架构地图

本文件用于新对话快速恢复“代码在哪里、如何分层、做完了什么”。
它不是源码替代品，而是压缩后的项目结构快照。

## 一、更新时间

- 最近更新：2026-11-04
- 当前阶段：P07 进行中（P07-05 已完成，下一步 P07-06 编辑草稿）
- 当前后端目录：`D:\Computer\Spring-boot-zzy\secondhandBookstore`
- 当前根包：`org.example.secondhandbookstore`

## 二、当前包结构

```text
org.example.secondhandbookstore
|-- SecondhandBookstoreApplication
|-- common
|   |-- config
|   |   |-- CacheConfig
|   |   |-- MybatisPlusConfig
|   |   |-- PasswordEncoderConfig
|   |   `-- SecurityConfig
|   |-- context
|   |   `-- RequestIdContext
|   |-- exception
|   |   |-- BusinessException
|   |   |-- ErrorCode
|   |   |-- GlobalExceptionHandler
|   |   `-- TooManyRequestsException
|   |-- filter
|   |   `-- RequestIdFilter
|   |-- result
|   |   |-- PageResponse
|   |   `-- Result
|   `-- security
|       |-- ClientIpResolver
|       |-- RestAccessDeniedHandler
|       `-- RestAuthenticationEntryPoint
|-- auth
|   |-- config
|   |   `-- JwtProperties
|   |-- controller
|   |   `-- AuthController
|   |-- dto
|   |   |-- LoginRequest
|   |   |-- LoginResponse
|   |-- filter
|   |   `-- JwtAuthenticationFilter
|   `-- service
|       |-- AuthService
|       |-- JwtBlacklistService
|       |-- JwtService
|       `-- LoginAttemptService
|-- catalog
|   |-- controller
|   |   |-- BookCategoryController
|   |   |-- BookDraftController
|   |   `-- BookEditionController
|   |-- dto
|   |   |-- BookDraftRequest
|   |   |-- BookEditionRequest
|   |   |-- BookEditionResponse
|   |   |-- CategoryRequest
|   |   `-- CategoryResponse
|   |-- entity
|   |   |-- BookCategory
|   |   `-- BookEdition
|   |-- mapper
|   |   |-- BookCategoryMapper
|   |   `-- BookEditionMapper
|   `-- service
|       |-- BookCategoryService
|       |-- BookEditionService
|       `-- impl
|           |-- BookCategoryServiceImpl
|           `-- BookEditionServiceImpl
|-- health
|   |-- controller
|   |   `-- HealthController
|   |-- dto
|   |   `-- HealthResponse
|   `-- service
|       `-- HealthService
|-- listing
|   |-- controller
|   |   `-- BookListingController
|   |-- dto
|   |   |-- BookListingRequest
|   |   `-- BookListingResponse
|   |-- entity
|   |   `-- BookListing
|   |-- enums
|   |   |-- ListingCondition
|   |   `-- ListingStatus
|   |-- mapper
|   |   `-- BookListingMapper
|   `-- service
|       |-- BookListingService
|       `-- impl
|           `-- BookListingServiceImpl
`-- user
    |-- controller
    |   `-- UserController
    |-- dto
    |   |-- UserRegisterRequest
    |   `-- UserResponse
    |-- entity
    |   `-- User
    |-- enums
    |   |-- UserRole
    |   `-- UserStatus
    |-- mapper
    |   `-- UserMapper
    `-- service
        |-- UserService
        `-- impl
            `-- UserServiceImpl
```

## 三、分层职责

```text
Controller
  -> 接收 HTTP 请求、读取 DTO、触发 @Valid、调用 Service、返回 Result

Service
  -> 业务规则、流程编排、权限判断、事务边界

Mapper
  -> 数据库访问，只负责持久化，不写业务规则

Entity
  -> 数据库表映射，不直接暴露给前端

DTO
  -> 接口输入输出结构，承载字段校验和响应数据
```

依赖方向：

```text
Controller -> Service -> Mapper -> Database
     |            |
     `-> DTO      `-> Entity
```

## 四、当前接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 返回应用名、版本和运行状态 |
| POST | `/api/book-drafts/preview` | 校验并预览书籍草稿请求 |
| POST | `/api/categories` | 新增图书分类（仅 ADMIN） |
| GET | `/api/categories` | 查询图书分类列表（公开，走 Redis 缓存） |
| PUT | `/api/categories/{id}` | 修改图书分类（仅 ADMIN） |
| DELETE | `/api/categories/{id}` | 删除图书分类（仅 ADMIN） |
| POST | `/api/book-editions` | 新增书籍版本（仅 ADMIN） |
| PUT | `/api/book-editions/{id}` | 修改书籍版本（仅 ADMIN） |
| GET | `/api/book-editions/{id}` | 查询书籍版本详情（公开） |
| GET | `/api/book-editions?page&size` | 分页查询书籍版本（公开） |
| POST | `/api/listings` | 创建商品草稿（需登录，卖家取自令牌） |
| POST | /api/users/register | 注册普通用户 |
| POST | /api/auth/login | 用户名或邮箱登录并返回 JWT |
| POST | `/api/auth/logout` | 退出登录并把当前令牌的 jti 写入黑名单 |

补充：`GET /api/categories`、`GET /api/book-editions/**` 匿名可访问；分类
`POST`、`PUT`、`DELETE` 与书籍版本 `POST`、`PUT` 仅 `ADMIN`；
其余 `/api/**` 需要携带 `Authorization: Bearer <token>`。

## 五、当前响应和异常约定

- 统一响应类型：`Result<T>`，字段为 `code`、`message`、`data`、`requestId`。
- 成功响应使用 `Result.success(data)`；请求 ID 由 `RequestIdFilter` 生成并写入响应头。
- 参数校验失败使用 HTTP 400，`data` 为字段名到错误消息的 Map。
- 业务异常使用 `ErrorCode` 区分业务码和 HTTP 状态，由 `GlobalExceptionHandler` 统一映射。
- 未处理异常使用 HTTP 500，`data` 为 `null`。
- 认证失败（未认证或令牌无效）返回 HTTP 401 和业务码 40002，由
  `RestAuthenticationEntryPoint` 在过滤器层直接写 JSON。
- 权限不足（已认证但角色不够）返回 HTTP 403 和业务码 40003，由
  `RestAccessDeniedHandler` 处理。
- 路径不存在返回 HTTP 404 和业务码 10006；HTTP 方法不支持返回 405 和业务码 10007。
- 登录退避期间返回 HTTP 429 和业务码 40004，并带 `Retry-After` 响应头。

## 六、功能状态

| 模块 | 状态 |
| --- | --- |
| 工程底座 | 已完成 |
| 健康检查 | 已完成 |
| DTO 参数校验 | 已完成 |
| 统一响应 | 已完成 |
| 全局异常 | 已完成 |
| MySQL、Flyway、MyBatis-Plus | 已完成基础验证 |
| 图书分类新增和查询 | 已完成 |
| Redis 接入与登录退避 | 已完成 |
| JWT 黑名单与退出登录 | 已完成 |
| 可信代理客户端 IP 处理 | 已完成（默认不信任 XFF） |
| Redis 分类缓存 | 已完成（命中、写后失效、重启恢复已验证） |
| 用户注册 | 已完成 |
| 登录、JWT | 已完成 |
| 认证过滤器、401 与 403 统一响应 | 已完成 |
| 普通用户与管理员授权 | 已完成（`POST /api/categories` 仅 ADMIN） |
| 商品、审核、搜索 | 进行中（创建草稿已完成，下一步编辑草稿） |
| 购物车、订单、支付 | 未开始 |
| 后台管理 | 未开始 |

## 七、每日结束时需要补充

每天学习结束后，统一更新以下内容：

```text
1. 完成的功能
2. 新增或移动的包和类
3. 新增接口
4. 新增依赖或配置
5. 下一个未完成任务
6. 当日踩坑和标准答案
```

不要只粘贴完整源码树。树只说明位置，必须同时写明职责、依赖方向和当前状态。

