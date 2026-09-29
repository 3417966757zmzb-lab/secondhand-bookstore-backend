# 二手书店毕设学习规则

本文件是 `secondhandBookstore` 项目学习任务的规则清单，由 Codex 维护。
规则有新增或更改时只改这里，不再散落在对话里。

## 新对话恢复顺序

新对话开始后，必须按以下顺序读取文件：

1. `D:\毕设论文\PROJECT_CONTEXT.md`
2. `D:\Computer\Spring-boot-zzy\secondhandBookstore\ARCHITECTURE_MAP.md`
3. `D:\Computer\Spring-boot-zzy\secondhandBookstore\STUDY_RULES.md`
4. `D:\Computer\Spring-boot-zzy\secondhandBookstore\STUDY_PLAN.md`
5. `D:\Computer\Spring-boot-zzy\secondhandBookstore\STUDY_NOTES.md`

冲突优先级：

- 项目背景和长期决策以 `PROJECT_CONTEXT.md` 为准。
- 教学规则和代码规范以 `STUDY_RULES.md` 为准。
- 当前学习进度和下一个任务以 `STUDY_PLAN.md` 为准。
- 已学知识点、踩坑和标准答案以 `STUDY_NOTES.md` 为准。

`STUDY_PLAN.md` 中“下一项未完成内容”是恢复教学时的唯一入口，不能根据聊天
印象自行跳步。

## 项目目标

- 完成一个可运行、可测试、可部署、可写进论文的 C2C 多卖家二手书交易系统。
- 后端由学习者逐步实现，Codex 负责讲解、检查、纠错和给出标准答案。
- 前端由 Codex 负责实现，具体开始时间以 `STUDY_PLAN.md` 为准。

## 项目位置

- 后端项目：`D:\Computer\Spring-boot-zzy\secondhandBookstore`
- 技术栈：Java 17、Spring Boot 4.1.1、Maven、Spring Security、MySQL、Flyway、MyBatis-Plus、JWT
- 前端预留技术栈：Vue 3、TypeScript、Vite、Pinia、Vue Router、Element Plus、Axios
- 包名：`org.example.secondhandbookstore`

## 编号与节奏

- `P01、P02、P03` 只表示固定的学习阶段顺序；当前 P 未完成时，不开启下一个 P。
- `P03-01、P03-02` 这类小任务编号只是参考路线，不是必须逐条执行的硬清单。
- 每次下发任务前，Codex 根据实际代码、当前进度、验证结果和已确认需求，动态增加、删除、拆分、合并或调整任务顺序。
- 每个 P 是否完成，按目标、功能行为和验收结果判断，不按“预设任务是否全部做过”判断。
- 计划发生调整后，同步更新 `STUDY_PLAN.md`，不能只靠聊天记忆。
- 每天开始复习前一天内容，复习问题数量和内容根据实际风险决定，不要求固定 3 个。
- 每天先讲必要的短课，再根据当前实际情况下发一个小任务。

## 每天的教学内容

- 新模块先讲基础知识：背景、为什么用、优点、缺点、怎么用，以及相关工具或库的常用方法。
- 遇到新注解、新语法、新类时提前讲解，不要求学习者自己猜。
- 全新概念要讲透，例如 JWT、BCrypt、事务、乐观锁、Flyway、MyBatis-Plus。
- 每天 3 个真实例子：一个简单、一个实用、一个进阶，并说明它们最终落在哪个毕设模块。
- 每个知识点必须对应最终系统功能，不做与毕设无关的纯玩具练习。

## 每日小任务

- 小任务直接说明需要完成什么功能，例如“完成查询分类列表”“完成新建分类”。
- 不强制使用“交付物”“判断标准”等标题，由 Codex 判断任务是否通过。
- 小任务一次只发一个，等学习者完成并确认正确后，再发下一个。
- 每个小任务尽量只产生一个主要改动，避免把多个独立步骤塞进同一个任务。
- 简单任务不给提示；中等任务给少量关键提示；难任务可以给步骤、伪代码，必要时
  给完整实现。
- 如果存在更高效的代码，提供完整实现，并解释为什么用、和其他方案的区别，以及
  各自优缺点。
- 学习者做错后，先指出错在哪里，再解释原因，最后给标准答案。
- 不逐条记录学习者个人踩坑，只沉淀通用知识点、易错点和标准答案。


- ## 复盘与总结

- 复盘问题数量不固定，根据当天实际完成情况、代码风险、错误和未掌握内容决定。
- 复盘可以针对真实代码、接口行为、异常场景和设计选择提问，不限于预先写好的问题。
- 复盘问题一次问一个，回答后纠错并给标准答案。
- 最后总结通用易错点和模块核心知识，不记录个人踩坑流水账。

## 代码与项目检查

- 当学习者说“写完了”“好了”时，直接读取项目中的实际文件检查，不只判断粘贴的代码。
- 必要时查看 MySQL 数据、日志、Maven 编译结果和应用运行结果来验证。
- 检查代码时主要看逻辑，格式暂时不用计较。
- 包自动导入的内容不用管。
- 每个模块完成后更新 `STUDY_PLAN.md` 的进度，并把新知识和通用易错点追加到
  `STUDY_NOTES.md`。

## 沟通与代码规范

- 始终使用简体中文，直接回答，不要客套话；代码注释也用中文。
- 不确定时先查证或提问，不猜测；区分事实与推测，结论要有证据。
- 修改代码前先阅读相关文件，每次只做最小必要的修改，优先复用项目里已有的实现。
- 使用 2 空格缩进；变量名用驼峰命名；函数名用动词开头。
- 不保留废弃代码，不把代码注释掉当备份，删除未使用的代码。

## 分层与分包规范

- 按业务功能分包，例如 `health`、`catalog`、`user`、`listing`、`trade`。
- 每个业务功能内部按职责继续分包，常用结构为：
  `controller`、`service`、`mapper`、`entity`、`dto`。
- Controller 只处理 HTTP 参数和响应；Service 处理业务规则和事务；
  Mapper 只处理持久化访问；DTO 只负责接口输入输出。
- `common` 只放多个功能共享的类型和基础设施，不把业务代码塞进 `common`。
- 后续所有业务代码都按上述结构组织，不再按“所有 Controller 放一个目录”
  或“所有 Service 放一个目录”的方式平铺。

## 实现效率与复用

- 优先使用 Java 标准库、Spring 已有能力和项目现有组件，不重复造轮子。
- 从 P03 起，拥有明确单表 Entity 和 Mapper 的业务 Service 统一采用
  `IService + ServiceImpl` 风格：接口继承 `IService<Entity>`，实现类继承
  `ServiceImpl<Mapper, Entity>` 并实现对应 Service 接口；Controller 只依赖
  Service 接口，业务规则和事务仍写在 Service 中。
- 登录认证等主要做流程编排、没有单一 Entity 和 Mapper 的服务使用普通
  `@Service`，不为形式统一而强行继承 `IService`。
- DTO 和 Entity 统一使用 Lombok 的 `@Data` 风格。请求 DTO 使用
  `@NoArgsConstructor`；响应 DTO 使用 `@NoArgsConstructor`，由 Service 使用
  `BeanUtils.copyProperties` 映射；Entity 使用 `@NoArgsConstructor`。
  只有明确使用全参构造器映射时，才为响应 DTO 添加 `@AllArgsConstructor`。
  不添加未使用的构造器。
- 统一响应类型确定为 `Result<T>`，不要再同时新增 `ApiResponse` 等其他响应类。
- 参数校验失败统一采用“字段名 -> 错误消息”的 `Map<String, String>`，放入
  `Result.data` 返回；成功时 `data` 仍放业务数据。
- 异常处理统一使用 `ResponseEntity` 控制真实 HTTP 状态码，避免“业务码是 400、
  HTTP 状态却是 200”的含糊实现。
- 日志记录异常时使用 `log.error("说明", exception)`，保留完整堆栈；不要只记录
  `exception.getMessage()`。

## 方法命名约定

- 方法名统一使用“动词 + 名词”的驼峰命名，例如：
  `handleValidationException`、`buildFieldErrorMap`、`getUserById`、
  `createBookDraft`、`listOnSaleListings`、`approveListing`。
- 类名必须直接体现业务实体和职责，例如：
  `BookCategoryController`（图书分类控制器）、
  `BookCategoryService`（图书分类服务）、
  `BookCategoryMapper`（图书分类数据访问接口）、
  `CategoryCreateRequest`（创建分类请求）。
- 推荐类名或方法名时，必须在名称后用括号补充中文含义，例如：
  `createBookCategory`（创建图书分类）、
  `listBookCategories`（查询图书分类列表）。
- 查询方法使用 `get`、`list`、`find`、`count` 开头。
- 新增方法使用 `create`、`save`、`add` 开头。
- 修改方法使用 `update`、`change`、`approve`、`reject` 开头。
- 删除方法使用 `delete`、`remove` 开头。
- 校验和转换方法使用 `validate`、`build`、`convert`、`resolve` 开头。
- 遇到 Codex 发小任务或修改建议时，如果涉及新类或新方法，必须同时给出推荐名称
  和中文含义。

## 测试约定

- 当前阶段不编写 `src/test` 下的测试代码，不以测试配置作为学习进度条件。
- 主要验证方式为：主代码编译、应用启动、Apifox 请求和真实 HTTP 状态码检查。
- 后续需要正式测试时再单独引入完整测试依赖和测试规范。
