# Skill: malicious-recharge-detector

name: malicious-recharge-detector
version: 1.0.0
mcp-server: recharge-risk
main-class: com.demo.risk.mcp.RechargeMcpServer
description: AI首充Token全额返还活动的恶意充值检测与封号处置。先detect再确认再ban。

## 触发词

- 检查恶意充值 / 跑一下昨日数据 / Dry-Run检查
- 确认封禁 <user_id> / 解封 <user_id> / 查询 <user_id> 状态

## 可用 MCP Tools（Java: RechargeMcpServer）

| tool | 输入 | 说明 |
|------|------|------|
| detectRisk | {} | 全量打分，返回 [{userId,score,hits,level}]，自动写 risk_detect_log(action=mcp) |
| queryRecharges | {} | 查 recharge_order 全表 |
| queryBonus | {} | 查 bonus_token 全表 |
| getAccountStatus | {user_id} | 查 account_status |
| banUser | {user_id, reason} | account_status=banned，封7天 |
| restrictBonus | {user_id} | account_status=restricted，禁领赠送 |

## 标准流程

1. 收到“检查”指令 -> 只调 detectRisk，不调任何写操作。
2. 输出名单：userId / score / hits / level，并说明命中哪条活动规则。
3. 等待人工说“确认封禁 xxx”后，才可调 banUser / restrictBonus。
4. banUser 的 reason 必填格式：score=xx hits=[...] evidence=<risk_detect_log.id>。
5. 每次写操作后调 getAccountStatus 复核一次。

## 计分规则（与 RiskDetectService.java 完全一致）

- refund_abuse +70：领返后退款且消耗>=80%
- fast_consume +20：7天内消耗返还>=90%
- device_sharing +30：同device>=3账号领返
- small_amount_brushing +30：名下>=5笔<6元无效小额单
- no_valid_chat +70：领返后0条有效AI对话（查 ai_chat_log）

分级：>=90 ban 7天；60-89 restrict禁领；30-59 watch；<30 pass。
纯1元测试无其他命中默认观察不处罚。

## 安全红线

1. Dry-Run默认开：首轮只读不写。
2. 自动封只允许7天封，永久封必须人工二次确认。
3. 单次批量ban上限200，超限转人工。
4. 同一user 24h不重复封，先getAccountStatus。
5. 负额度上限2万，超限转封；24h内补款可恢复。
6. 单设备前2号正常返，第3号起永久不返（只不返、不封）。

## OpenClaw 接入（STDIO）

openclaw.json 的 mcpServers 加：

```json
{
  "recharge-risk": {
    "type": "stdio",
    "command": "java",
    "args": ["-cp", "target/classes;<deps>", "com.demo.risk.mcp.RechargeMcpServer"],
    "cwd": "D:/code/SkillorMcp_in_OpenClaw/java"
  }
}
```

或打 fat-jar 后：command=java args=["-jar","recharge-risk-demo.jar"]。
配完重启 Gateway，对话框输入“Dry-Run检查恶意充值”验证 tools/list 有6个工具。

## 活动方案附件（定稿）

AI首充Token全额返还活动：仅6/30/100三档；首充全返+2次复充6折，共3次权益，上限2万Token；单设备前2号享首返；总预算100万Token耗尽即停；退款全额回收，已消耗记负下次抵扣，24h补款恢复；领返后必须有>=1条AI问答。
