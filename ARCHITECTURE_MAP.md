# 二手书店系统架构地图

本文件用于新对话快速恢复“代码在哪里、如何分层、做完了什么”。
它不是源码替代品，而是压缩后的项目结构快照。

## 一、更新时间

- 最近更新：2026-09-27
- 当前阶段：P02 已完成，下一步 P03
- 当前后端目录：`D:\Computer\Spring-boot-zzy\secondhandBookstore`
- 当前根包：`org.example.secondhandbookstore`

## 二、当前包结构

```text
org.example.secondhandbookstore
|-- SecondhandBookstoreApplication
|-- common
|   |-- exception
|   |   |-- BusinessException
|   |   `-- GlobalExceptionHandler
|   `-- result
|       `-- Result
|-- catalog
|   |-- controller
|   |   |-- BookCategoryController
|   |   `-- BookDraftController
|   |-- dto
|   |   |-- BookDraftRequest
|   |   |-- CategoryCreateRequest
|   |   `-- CategoryResponse
|   |-- entity
|   |   `-- BookCategory
|   |-- mapper
|   |   `-- BookCategoryMapper
|   `-- service
|       |-- BookCategoryService
|       `-- impl
|           `-- BookCategoryServiceImpl
`-- health
    |-- controller
    |   `-- HealthController
    |-- dto
    |   `-- HealthResponse
    `-- service
        `-- HealthService
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
| POST | `/api/categories` | 新增图书分类 |
| GET | `/api/categories` | 查询图书分类列表 |

## 五、当前响应和异常约定

- 统一响应类型：`Result<T>`，字段为 `code`、`message`、`data`。
- 成功响应使用 `Result.success(data)`。
- 参数校验失败使用 HTTP 400，`data` 为字段名到错误消息的 Map。
- 未处理异常使用 HTTP 500，`data` 为 `null`。
- 业务异常使用 `BusinessException`，由 `GlobalExceptionHandler` 映射为精确
  HTTP 状态码，例如分类名重复使用 HTTP 409。

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
| Redis 缓存和 JWT 黑名单 | 计划 P05、P06 接入 |
| 用户、登录、JWT | 未开始 |
| 商品、审核、搜索 | 未开始 |
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

