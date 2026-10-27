> **LiuPets 中文版说明**
>
> 本项目在原版 MCPets 基础上完成中文本地化、更名与 Liu 插件生态联动，插件名、命令、配置目录为 `LiuPets` / `liupets`，同时保留 `mcpets` 命令别名与旧数据兼容。
>
> - 主命令：`/liupets`（别名 `pets`、`mcpets`、`lpets`），无参数直接打开中文「宠物中心」界面。
> - 坐骑命令：`/mcmounts`（别名 `mounts`）。
> - 永久解锁（供商城/战令奖励调用）：`/liupets unlock <在线玩家或UUID> <宠物ID>`，基于 LuckPerms 写入权限节点。
> - 联动：`Liupass`（喂食/驯服/升级进度）、`mcSkills`（目标过滤与宠物伤害保护）；宠物中心只保留宠物功能，不再内置商城/战令等快捷入口。
> - 联动配置：`plugins/LiuPets/integrations.yml`。
> - 旧数据迁移：首次启动会自动把 `plugins/MCPets/` 复制到 `plugins/LiuPets/`，原目录保留作为备份。
> - 旧版英文 `language.yml` 会在重载时自动回退为中文默认文本，自定义中文文本会保留；如需完全重置可删除该文件后重载。
> - `/liupets editor` 配置编辑器的菜单标题、物品名称、说明与输入提示均已中文化；新增语言键会自动补齐，不会覆盖已有自定义文本。

<img src="https://i.ibb.co/7RYYYZb/spigot-mcpets-banner.png" alt="MC Pets logo">

# LiuPets

LiuPets 是 MCPets 的中文分支，面向中文服务器提供完整的宠物、坐骑与养成体验。插件依赖 MythicMobs 提供生物与技能，并使用 ModelEngine 或 BetterModel 渲染 3D 模型。

需要了解完整配置与玩法时，可查阅 [MCPets Wiki](https://mcpets.gitbook.io/mcpets/)；遇到问题可加入 [Discord](https://discord.com/invite/p7QTm2gUyf)。

<img src="https://i.imgur.com/saPEOAJ.png" alt="Requirements">

✨ 运行环境与依赖请查看 [Wiki 要求页面](https://mcpets.gitbook.io/mcpets/common-issues/common-issues/requirements)。

<img src="https://i.imgur.com/KadwjCO.png" alt="Features">

⭕ 基于 MythicMobs 与 ModelEngine 的 **3D 宠物**，支持任意行为、技能与特效

⭕ 使用 ModelEngine 创建 **坐骑**，并通过 MCPets 快速配置与召唤

⭕ 每只宠物拥有独立的 **背包**

⭕ **宠物养成属性**：经验、生命、技能伤害倍率等

⭕ **驯服**、**进化** 与 **宠物食物** 系统

⭕ 可自定义的 **游戏内 GUI**：召唤宠物、查看属性、改名、骑乘、外观、背包，并可按分类整理

⭕ 使用 **指挥棒** 向宠物下达指令

⭕ 基于权限的解锁系统，可为每只宠物配置 **独立权限**，按玩家权限决定是否在 GUI 中显示

⭕ **WorldGuard 旗标**，控制宠物与区域之间的交互

⭕ 支持 MySQL 存储

⭕ **Velocity 跨服宠物同步**：玩家在 Velocity 网络内切换服务器时，已召唤的宠物会无缝跟随（详见 [mcpets-velocity/README.md](mcpets-velocity/README.md)）

✨ 想先体验示例宠物？可以看看 [Sleepy the Otter](https://mcmodels.net/products/11051/sleepy-the-otter)！

---

## Velocity 跨服同步

本分支附带一个独立的 Velocity 代理端插件（`mcpets-velocity/`），用于让玩家的已召唤宠物在所有已同步的服务器之间保持一致。

**工作原理：**
- 玩家切换到已同步的服务器时，当前宠物会保存到共享 MySQL 数据库，并在目标服务器自动召唤
- 在任意服务器收回宠物都会清理全网记录，避免出现“幽灵宠物”
- 只有 Velocity 配置中 `synced-servers` 列出的服务器参与同步，其他服务器不受影响

**快速配置：**
1. 在 MCPets 中启用 MySQL，并让所有需要同步的服务器连接同一个数据库
2. 在每台已同步服务器的 `config.yml` 中设置 `Velocity.Enabled: true`
3. 把 `MCPets-Velocity-<version>.jar` 放入 Velocity 代理端的 `plugins/` 目录
4. 在代理端 `plugins/mcpets-velocity/config.yml` 中配置需要同步的服务器

完整安装步骤与配置项见 [mcpets-velocity/README.md](mcpets-velocity/README.md)。

---

<img src="https://i.ibb.co/Sn460M4/patreon-advantages.png" alt="Patreon advantages">

⭕ 在 [Patreon](https://www.patreon.com/tofnocsy_workshop) 获取独家内容与每月更新

⭕ 参与每月模型投票，选择你喜欢的模型加入制作计划

⭕ 加入 [Discord 工坊](https://discord.gg/p7QTm2gUyf) 的赞助者专属频道

# ✨ 合作伙伴 ✨

在 [MCModels.net](https://mcmodels.net/) 下载更多宠物模型

![image](https://cdn.discordapp.com/attachments/884364895108366336/909534639650136064/partnered.png)
