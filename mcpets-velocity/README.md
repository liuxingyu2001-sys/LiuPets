# MCPets-Velocity

Velocity 代理端的配套插件，用于让玩家的已召唤宠物在 Velocity 网络内的所有服务器之间保持同步。玩家切换服务器时，宠物会在源服务器收回，并在目标服务器自动召唤；在任意服务器收回宠物也会同步到全网。

---

## 环境要求

| | 版本 |
|---|---|
| Velocity | 3.x |
| MCPets | 本分支（需安装在每台已同步服务器） |
| MySQL | 所有已同步服务器可访问的同一个数据库 |
| Java | 11 或更高 |

---

## 安装步骤

### 第一步：Velocity 代理端

1. 把 `MCPets-Velocity-<version>.jar` 放入 Velocity 代理端的 `plugins/` 目录。
2. 启动或重启代理端，会生成配置文件 `plugins/mcpets-velocity/config.yml`。
3. 打开配置，填写需要参与宠物同步的后端服务器名称：

```yaml
# 名称必须与 velocity.toml 中 [servers] 下定义的服务器名称完全一致
synced-servers:
  - skyblock-1
  - skyblock-2
  - skyblock-3
```

`synced-servers` 留空时，代理端会对 **所有** 服务器同步宠物。

### 第二步：每台已同步的后端服务器

在每台需要参与宠物同步的服务器上：

1. 确认已安装本分支的 LiuPets。
2. 打开 `plugins/LiuPets/config.yml`，配置 MySQL 连接共享数据库：

```yaml
DisableMySQL: false

MySQL:
  Host: your.db.host
  Port: 3306
  Database: mcpets_db
  User: youruser
  Password: yourpassword
  Prefix: ""
```

3. 在同一个配置文件中启用 Velocity 同步：

```yaml
Velocity:
  Enabled: true
  SwitchWindow: 60
```

4. 重启服务器。

> **所有已同步服务器必须连接同一个 MySQL 数据库**，宠物状态才能在这些服务器之间共享。

---

## 配置说明

### Velocity 代理端（`plugins/mcpets-velocity/config.yml`）

| 配置项 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `synced-servers` | 列表 | `[]` | 需要互相同步宠物的服务器名称；留空表示同步所有服务器。 |

### LiuPets 后端（`plugins/LiuPets/config.yml`）

| 配置项 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `Velocity.Enabled` | 布尔值 | `false` | 在每台已同步服务器上设为 `true`，启用跨服宠物同步。 |
| `Velocity.SwitchWindow` | 整数（秒） | `60` | 玩家退出后，其活跃宠物记录在多长时间内仍可用于恢复。玩家切换服务器经常超时的话可以适当调大。 |

---

## 工作流程

玩家切换到已同步服务器时，源服务器会先把该玩家的活跃宠物写入 MySQL 的共享表 `mcpets_active_pet`，再移交连接。Velocity 代理端会通知目标服务器有携带宠物的玩家即将到来；玩家到达后，目标服务器读取记录并召唤正确的宠物。

如果玩家主动收回宠物，数据库记录会立即清除。目标服务器找不到记录时不会召唤任何宠物。

如果由于任何原因没有收到 Velocity 通知，目标服务器会回退使用 `SwitchWindow` 设置：在该时间窗口内写入的记录仍会被视为一次有效的切换，并恢复宠物。

---

## 常见问题

**切换服务器后宠物没有出现**
- 确认目标服务器的 `config.yml` 中也设置了 `Velocity.Enabled: true`，而不只是源服务器。
- 确认所有已同步服务器连接的是同一个 MySQL 数据库，并且 `DisableMySQL: false`。
- 确认 `synced-servers` 中的服务器名称与 `velocity.toml` 中定义的名称完全一致。
- 查看目标服务器控制台，确认玩家加入时 LiuPets 是否输出了错误。

**宠物收回后仍然出现**
- 确认所有已同步服务器运行的是同一版本的 LiuPets 分支。旧版本在收回宠物时不会清理数据库记录。

**召唤出了错误的宠物**
- 宠物 ID 会从每台服务器自己的宠物配置中解析。请确保所有已同步服务器的宠物配置文件完全一致，这样同一个宠物 ID 才能在每台服务器上解析为同一只宠物。

---

## 从源码构建

在 `mcpets-velocity/` 目录执行：

```bash
mvn clean package
```

构建产物位于 `target/MCPets-Velocity-<version>.jar`。
