# AGENTS.md — LiuPets（MCPets 中文分支）

本文件面向在本仓库工作的开发者与 AI。工作区级约定见 `/home/liu/plugins/AGENTS.md`，两者同时适用；本文件补充本项目特有信息。

## 项目速览

- Bukkit/Paper 1.21 宠物与坐骑插件：MythicMobs 提供生物与技能，ModelEngine 或 BetterModel 渲染 3D 模型；可选 Nexo/ItemsAdder 自定义物品、WorldGuard 区域旗标、LuckPerms 权限、PlaceholderAPI 变量。
- 本仓库是基于上游 MCPets 4.2.x 的中文分叉 **LiuPets**：插件名与产物为 `LiuPets`，数据目录 `plugins/LiuPets`，主命令 `/liupets`（别名 `pets`/`mcpets`/`lpets`），坐骑命令 `/mcmounts`（别名 `mounts`）。权限节点仍为 `mcpets.use`/`mcpets.admin`/`mcpets.color`，`plugin.yml` 里的 `liupets.*` 只是 children 别名。
- 分叉内容：中文默认文本（`Language` 枚举）、宠物中心 `PetHub`、`/liupets unlock`、`LiuIntegrations`（战令/技能联动）、`integrations.yml`、旧数据迁移、菜单分页重构。部分改动尚未提交：动手前先 `git status --short`，不要覆盖用户改动。
- `mcpets-velocity/` 是独立的 Velocity 代理端插件，有自己的 pom，**不在根构建内**，负责跨服宠物同步。
- 上游仓库：`git@github.com:Nocsy-Workshop/mcpets.git`（分支 `main`）。

## 构建与验证

在仓库根目录执行：

```bash
mvn clean package                              # 主插件 → target/LiuPets-<version>.jar
mvn -f mcpets-velocity/pom.xml clean package   # 代理插件 → mcpets-velocity/target/MCPets-Velocity-<version>.jar
```

- 根 `pom.xml` 是单模块，没有 `<modules>`；代理端必须单独构建。
- Java 21 编译：`maven-compiler-plugin` 的 source/target 为 21，Lombok 走 `annotationProcessorPaths`。根 pom `<properties>` 里的 17 **不生效**，不要据此降版本。代理端目标 Java 11。
- 资源过滤开启：`plugin.yml`、`velocity-plugin.json` 中的 `${project.name}` / `${project.version}` 由 Maven 替换，不要在资源里写死插件名或版本；产物名为 `${project.name}-${project.version}`。
- 无单元测试（无 `src/test`）；验证方式为编译、打包，以及在测试服观察日志与 GUI/命令行为。
- 主工程依赖已缓存时可加 `-o` 离线构建；代理端 `velocity-api:3.3.0-SNAPSHOT` 需要联网从 `repo.papermc.io` 拉取。
- `target/` 已被 `.gitignore` 忽略，不要提交构建产物。

## 代码结构

| 路径 | 职责 |
|---|---|
| `src/main/java/fr/nocsy/mcpets/MCPets.java` | 插件入口：依赖探测、配置加载、生命周期、静态实例与全局访问器 |
| `api/` | `MCPetsAPI`（对第三方暴露宠物查询/召唤）、`LiuPetsAPI`（LuckPerms 永久解锁） |
| `commands/` | `CCommand`/`AArgument` 命令框架；`commands/mcpets/Argument*.java` 子命令；`commands/mounts/` 坐骑命令 |
| `data/Pet.java` | 宠物运行时对象与全局池（`objectPets`、`activePets`），召唤/收回/跟随核心逻辑 |
| `data/config/` | 配置模型：`AbstractConfig` 基类、`GlobalConfig`、`LanguageConfig`/`Language`、`PetConfig`、`CategoryConfig`、`PetFoodConfig`、`ItemsListConfig`、`BlacklistConfig` |
| `data/editor/` | 游戏内 YAML 编辑器（`/liupets editor`）：`Editor`、`EditorState`、`EditorItems`、`EditorConversation`、`EditorText`（编辑器图标中文化层） |
| `data/inventories/` | 全部 GUI 与 `PetInventoryHolder`（宠物中心、列表、交互、背包、皮肤、分类、坐骑） |
| `data/livingpets/` | 等级/经验、生命与伤害修正、宠物食物与 Buff |
| `data/sql/` | 持久化：`Databases`（MySQL，可回退 YAML）、`PlayerData`、`MySQLDB`、`PlayerDataNoDatabase` |
| `data/flags/` | WorldGuard 旗标（`FlagsManager` 与各 Flag） |
| `events/` | 对外 Bukkit 事件（`PetSpawnEvent`、`PetLevelUpEvent`、`PetFedByPlayerEvent` 等） |
| `listeners/` | Bukkit 监听器；`listeners/editor/` 为编辑器输入监听 |
| `modeler/` | 模型抽象 `AbstractModeler`、BetterModel/ModelEngine 实现、名牌处理 |
| `mythicmobs/` | 自定义 MythicMobs 组件：mechanics、conditions、placeholders、targeters（按包名整包注册） |
| `utils/` | 分页 `MenuPaginationHelper`、PDC 标签 `PDCTag`、消息/数学/计时、`Debugger` |
| `velocity/` | 后端侧跨服同步（插件消息 `mcpets:sync` + MySQL） |
| `compat/` | PlaceholderAPI 扩展、`LiuIntegrations` 反射适配 |
| `mcpets-velocity/` | Velocity 代理端独立工程（`MCPetsVelocity`、`SyncListener`、`VelocityPluginConfig`） |
| `src/main/resources/` | 只有 `plugin.yml` 与 `integrations.yml`；其余 YAML 全部运行时生成 |

## 生命周期（改初始化顺序前必读）

1. `onLoad()`：设置 `instance`；`AbstractConfig.migrateLegacyData()` 首次把 `plugins/MCPets` 复制到 `plugins/LiuPets`（目标已存在则跳过，旧目录保留）；重置静态探测标志（支持 PlugMan 重载）；检查 MythicMobs 与模型后端，缺失则直接返回；探测 WorldGuard/LuckPerms/PlaceholderAPI/Nexo/ItemsAdder；WorldGuard 可用时 `FlagsManager.init`。
2. `onEnable()`：再次校验依赖，失败则 `disablePlugin`；`CommandHandler.init`、`EventListener.init`、`modeler.registerListeners`；随后**延迟 1 tick** 执行 `loadConfigs()`（让 Nexo 等 hook 先注册物品）；注册 MythicMobs 自定义组件；`FlagsManager.launchFlags`。
3. `loadConfigs()`：`LiuIntegrations.reload()` → `ItemsListConfig`（重建后调用 `Items.refreshAll()`，使 `menuIcons.yml` 修改无需重启）→ `PetFoodConfig` → `GlobalConfig` → `LanguageConfig` → `BlacklistConfig` → `PetConfig.loadPets` → `CategoryConfig.load`；随后**异步**初始化数据库与 `PlayerData.initAll()`，完成后回主线程执行 `scheduleDbDependentTasks()`（保存统计、按需启动 `VelocitySyncManager`）。DB 加载只填充 `PlayerData`/`PetStats`，宠物背包在首次主线程访问时懒反序列化。
4. `onDisable()`：注销 PlaceholderAPI；清空交互菜单等待状态；`EditorConversation.clearAll()`（避免 JAR 卸载后监听器触发 `zip file closed`）；`Editor.clearAll()`；注销模型监听；异步保存全部数据并 `join`（配置从未加载时跳过保存）；按需写 `mcpets_active_pet`；`PetStats.clearAll()`、`PetTimer.clearAll()`；最后 `Pet.clearPets()`（先对活跃宠物 `despawn(RELOAD)`）并关闭连接。

约定：GUI/实体/Bukkit 操作留在主线程；数据库与文件重活走异步。不要在配置加载前访问 `GlobalConfig` 的可变状态（WorldGuard 标志在 `checkWorldGuard` 中先行设置）。

## 配置系统

- 所有文件位于 `plugins/LiuPets/`（`AbstractConfig.getPath()`）：`config.yml`、`language.yml`、`menuIcons.yml`、`petfoods.yml`、`blacklist.yml`、`integrations.yml`、`Pets/<id>.yml`、`Categories/<id>.yml`。
- **默认值写在 Java 代码里**：`AbstractConfig.init()` 只负责建目录/空文件，各配置类在自己的 `init()` 中逐个 `if (getConfig().get(...) == null) set(...)`，再 `save()+reload()`。仓库没有默认 YAML 资源，新增/修改默认项必须改对应 Java 类（`GlobalConfig`、`LanguageConfig`、`PetFoodConfig` 等）。因此老服已有文件不会自动获得新默认值，除非代码显式补齐。
- `language.yml` 的键 = `Language` 枚举名小写；`LanguageConfig.init()` 只为缺失键补默认值，**不覆盖已有文本**。玩家可见文本一律走 `Language`；新增消息要同时改枚举（默认值）并考虑老服补键方式（删文件重生成或手补）。
- `Pets/<id>.yml` 主要键：`Id`、`MythicMob`、`Permission`、`Mountable`/`MountType`/`MountPermission`、`Distance`/`SpawnRange`/`ComingBackRange`、`DespawnSkill`、`Taming.*`、`Signals.*`、`Icon`、`Levels.<key>.*`（含 `Evolution`、`Cooldowns`、`InventoryExtension`、`Announcement`）、`Skins.<key>.*`。解析在 `PetConfig.reload()`。
- `integrations.yml` 由 `LiuIntegrations` 读取：`liupass.*`（战令任务 ID，留空关闭）、`mcskills.protect-pets`；宠物中心不提供第三方菜单入口，`menus.*` 已移除。

## 命令与权限

- `/liupets`：无参 → `PetHub` 宠物中心；`list` → `PetMenu`；`menu [petId]` → 交互菜单；`name`；`mounts`；`help`；管理员 `reload`（会先保存并收回全部活跃宠物，再清空 `PetStats` 重载）、`editor`、`spawn`、`open`、`inventory`、`revoke`、`signalstick`、`item`、`clearStats`、`petFood`、`category`、`mount`、`unlock <玩家|UUID> <宠物ID>`。
- `/mcmounts`（别名 `mounts`）：坐骑分类/交互菜单，`menu [mountId]`。
- 权限：`mcpets.use`（默认 true）、`mcpets.admin`、`mcpets.color`（改名时是否保留颜色代码）；`plugin.yml` 的 `liupets.*` 通过 children 指向三者。
- 新增子命令清单：`commands/mcpets/Argument*.java`（继承 `AArgument`，声明 `super(name, argCounts, sender, args)` 与 `additionalConditions`/`commandEffect`）+ 在 `MCPetsCommand` 的 `outcomes` 数组注册 + `MCPetsCommandTabCompleter` 补全 + `Language.USAGE`/帮助文本 + 需要时在 `plugin.yml` 增权限。
- `CommandHandler.init` 使用 `plugin.getCommand(主名)` 注册 executor/tab completer，命令名必须与 `plugin.yml` 一致；`unlock` 在 `MCPetsCommand` 中早期直接处理（不经过 `AArgument`）。

## GUI 约定

- 自定义菜单统一用 `PetInventoryHolder(size, title, Type)` 创建，靠 `Type` 分派点击。新增菜单类型要同时扩展 `Type`、写监听器（参考 `PetMenuListener`、`PetHubListener`、`PetInteractionMenuListener`）并在 `EventListener.init` 注册。
- 分页统一用 `MenuPaginationHelper`：`calculatePagination(page, total)` 读取 `config.yml` 的 `InventorySize`（-1 自适应），每页容量固定为“容量 - 2”（首尾翻页键）；翻页按钮用 `Items.previousPage/nextPage` 与 `PDCTag` 标记，点击交给 `handlePagination(...)`。不要手写页大小/页切换逻辑。
- 点击事件应取消并阻止拖拽；物品身份优先用 PDC 标签而不是显示名；标题优先取自 `Language`（`PetHub` 目前为硬编码标题，新增 GUI 建议走 `Language`）。
- `PetHub` 固定 45 格布局，只保留宠物功能入口（我的宠物 11、我的坐骑 13、正在跟随 15、收回全部 22、关闭 40）；商城/战令/技能等第三方快捷入口已移除（槽位含义见 `PetHub.open` 与 `PetHubListener.click`）。
- “正在跟随”总览为 `ActivePetsMenu` + `ActivePetsMenuListener`（`PetInventoryHolder.Type.ACTIVE_PETS_MENU`），点击图标解析为当前活跃实例后打开交互菜单；无活跃宠物时显示 27 格空状态提示。
- `PetMenu.open()` 在存在分类时会跳转 `CategoriesMenu`，此时构造出的 `inventory` 不直接展示；直接 `openInventory(new PetMenu(...))` 与命令路径行为可能不同。

## 宠物运行时与持久化

- `Pet` 同时存在对象池（`getObjectPets()`，配置宠物）与活跃池（`activePets`，按玩家多宠）。`spawn()` 返回码包括 `BLOCKED`、`DESPAWNED_PREVIOUS`、`OWNER_NULL`、`MYTHIC_MOB_NULL`、`NO_MOB_MATCH`、`NOT_ALLOWED`、`MAX_ACTIVE_PETS_REACHED` 等，调用方按 `Language` 报错。
- 可用性由 `Pet.getAvailablePets(p)` 按 `Permission` 过滤。`Permission` 为空且 `checkPermission=true` 时 `hasPermission(null)` 为 false，宠物对所有人隐藏——配置宠物时务必写 `Permission`，或在编辑器中关闭权限检查。
- 玩家数据由 `PlayerData`（背包、活跃宠物等）与 `PetStats`（生命/经验/复活/召唤计时）管理。MySQL 可用则写库，否则回退 YAML；`GlobalConfig.isDatabaseSupport()` 决定分支，因此 DB 初始化完成前不要触发保存。
- `PlayerData.loaded` 在数据库行真正读出后才为 true；`PlayerData.save()` 会跳过未加载对象，防止启动阶段用空快照覆盖真实数据。异步加载会复用已存在的 `PlayerData` 实例，不替换引用。
- 宠物背包从 DB/YAML 读出后只保留序列化字符串，`PetInventory.get(Pet)` 首次在主线程访问时才反序列化并写入静态缓存；保存时先保留未反序列化的条目，再覆盖活跃条目。
- `PetStats.remove(...)`/`clearAll()` 会停止复活、收回、回血计时器（异步调用时切回主线程取消），避免旧计时器在玩家重进后生成过期宠物。
- `PetSkin.reloadSkins` 依赖 `PetSkin.clearList` 无条件清空旧列表；否则每次 `/liupets reload` 会累积重复皮肤。
- `mcpets_active_pet` 表保存“玩家退出/切服时的活跃宠物与皮肤”，供 Velocity 同步；多条宠物用逗号分隔，皮肤格式 `petId:skinPathId`。

## 模型层（ModelEngine / BetterModel）

- `AbstractModeler` 是唯一抽象；`MCPets.checkModeler()` 先试 BetterModel（`kr.toxicity.model.api.BetterModel`），再回退 ModelEngine（`com.ticxo.modelengine.api.ModelEngineAPI`），两者都缺失则插件自禁用。
- `BetterModelModeler`、`ModelEngineModeler` 与名牌实现（`modeler/bone/*`）必须保持行为对等；改一处要同步另一处。新增后端还需在 `pom.xml` 加 provided 依赖、`plugin.yml` 加 softdepend、`checkModeler` 加探测。
- 宠物实体识别走 `Pet.getFromEntity`（读取 Bukkit metadata 键 `AlmPet`）；`Pet.SIGNAL_STICK_TAG` 是信号棒物品的 PDC 标记，别与实体识别混淆。MythicMobs 负责生物本体，模型只是外观。

## MythicMobs 自定义组件

- `onEnable` 用 `CustomComponentRegistry` 按包名整包注册：`mythicmobs.placeholders`、`mythicmobs.conditions`、`mythicmobs.targeters`、`mythicmobs.mechanics`。
- 新增组件只需把类放进对应包（实现 MythicMobs 5.x 接口），无需改注册代码；注意与 `Mythic-Dist 5.12.1` API 兼容。
- 组件名/占位符是服主配置契约，重命名要同步文档与 wiki 引用。

## 第三方联动

- `PlaceholderAPICompat` 注册 `%mcpets_*%` 变量，前缀保持兼容。
- `LiuIntegrations` 不对相邻插件做编译依赖：用 `Class.forName` + 反射调用 Liupass `PassAPI`、mcSkills `SkillDamageUtils`；插件缺失或接口不匹配时静默降级，反射失败只警告一次。保持 try/catch 与降级路径。
- `LiuPetsAPI.unlock` 用 LuckPerms 给玩家添加宠物权限节点（`Pet.getPermission()`）；无 LuckPerms 返回失败 future。商城/战令奖励走此入口。
- Nexo/ItemsAdder 通过 `Class.forName` 探测；物品解析在 `PetConfig`/`Items`（键 `NexoId`、`ItemsAdder`、`CustomModelData`、`ItemModel`、`TextureBase64` 等），缺失时回退原版物品。
- `PetHubListener` 额外拦截 mcSkills 的技能伤害以保护宠物（可用 `mcskills.protect-pets` 关闭）。

## 跨服同步（Velocity）

- 后端：`config.yml` 的 `Velocity.Enabled`/`Velocity.SwitchWindow` + 共享 MySQL；`VelocitySyncManager` 在 DB 初始化完成后启动，注册 `mcpets:sync` 通道（在代码中注册，**不要**在 `plugin.yml` 加 `channels`）。
- 协议：`PLAYER_SWITCHING` 消息 → 源服保存并收回 → 目标服读 `mcpets_active_pet` 恢复；收回操作要同时清理库记录，避免“幽灵宠物”。
- 代理端：`mcpets-velocity/` 独立构建，`config.yml` 的 `synced-servers` 为空表示全网同步，否则只对列出的后端服生效。
- 改协议或表结构必须两端同步修改，并考虑滚动升级：旧版后端仍可能发送旧消息。

## 线程与安全

- 主线程只做 GUI/实体/Bukkit API；MySQL、文件遍历与保存走异步（参考 `MCPets.loadConfigs`、`onDisable`）。
- 玩家进服时只由 `PetListener.reconnectionPlayer` 异步读库，完成后回主线程恢复活跃宠物；不要在 `PlayerJoinEvent` 同步查询数据库，也不要再额外注册第二个 join 读取（会覆盖刚恢复的 `PetStats`）。
- `PetInventory.open` 在 `openInventory` 后立即写 `MCPets;petInventory` metadata（不再延迟 2 tick）；关闭时若找不到对应缓存也要移除该 metadata，避免旧标记污染后续 GUI 保存。
- `onDisable` 的保存 future 必须 `join` 后再关闭连接；插件卸载后避免再触碰 JAR（编辑器会话、反射加载类），否则 `IllegalStateException: zip file closed`。
- 静态状态（`instance`、`modeler`、探测标志、`Editor` 映射、等待输入列表）在 PlugMan 重载场景要重置；`EventListener.init` 已 `listeners.clear()`。
- 未适配 Folia：统一使用 Bukkit 全局调度器，不要假设区域线程。
- 日志与文档中不要写 MySQL 密码、token 等秘密（工作区规则）。

## 本地化与兼容

- 插件身份统一 `LiuPets`/`liupets`；保留 `mcpets` 命令别名、`mcpets.*` 权限、插件消息 channel 与 `plugins/MCPets` 旧数据迁移，避免破坏已有服务器与商城/战令配置。
- 玩家可见文本在 `Language` 枚举维护中文；`README.md` 与代理端文档均为中文，功能改动时同步更新相应段落。`/liupets editor` 的标题、图标名称/说明与输入提示也已中文化。
- 编辑器（`/liupets editor`）的物品标题/描述不直接散落在 `Language` 中：`EditorItems.getItem()` 在返回前调用 `EditorText.localize(...)`，按原始英文标题映射中文标题、类型提示与当前值；未识别的动态宠物名/等级名保持原样，避免改写玩家配置。新增编辑器图标时必须同时在 `EditorText.NAMES` 补中文，否则会回退英文标题。
- `EditorState` 的 GUI 标题同样是中文；编辑器输入提示、校验、保存/重置/删除反馈使用 `EDITOR_*` / `COMMAND_*` 系列 `Language` 键，老服 `language.yml` 会自动补齐缺失键。
- 命令输出中的旧 `/mcpets` 写法应替换为 `/liupets`（见 `MCPetsCommand` 参数不完整提示）。

## 已知陷阱

- `README.md` 与 `mcpets-velocity/README.md` 已全部中文化；修改安装路径或配置键时同步更新这两个文档，代理端路径应为 `plugins/LiuPets/config.yml`。
- 老服 `language.yml` 的旧英文文本不会覆盖中文默认值：`Language.reload()` 会检测不含汉字的配置值并回退到内置中文；含中文的自定义文本仍保留。若需完全重置可删除文件后重载。
- 根 pom `<properties>` 里 `maven.compiler.source`/`target` 写的 17 不生效（被编译插件的 21 覆盖），不要据此改依赖或语法级别。
- 版本号同时出现在根 `pom.xml` 和 `mcpets-velocity/pom.xml`；`plugin.yml`/`velocity-plugin.json` 由资源过滤生成，无需手改。
- `git status` 中可能已有用户未提交的分叉改动（新增 `PetHub`、`LiuIntegrations`、`LiuPetsAPI`、`integrations.yml` 等）；不要 `git checkout` 或覆盖无关文件。

## 交付

- 未获明确要求时不要递增版本、分发 jar、提交或推送；用户调用 `/mvn`、`/mvnp`、`/gitp` 时，按对应技能与 `/home/liu/plugins/AGENTS.md` 的规则执行。
