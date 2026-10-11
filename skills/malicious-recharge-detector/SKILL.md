# Skill: malicious-recharge-detector

## 触发词
检查恶意充值 / 昨日风控 / Dry-Run 检查 / 确认封禁 user_xxx / 解封 user_xxx

## 前置
MCP Server `recharge-risk` 必须已连接。tools: detectRisk, queryRecharges, queryBonus, banUser, restrictBonus, getAccountStatus。

## 标准流程
1. 先调 `detectRisk` 拿 score/hits/level 全量名单。
2. 默认 Dry-Run：只展示名单，不调 banUser/restrictBonus。
3. 用户说"确认封禁"后，才对 level=ban 的用户逐个调 `banUser(user_id, reason=score+hits)`；level=restrict 调 `restrictBonus`。
4. 批量自动封上限 200，超限转人工工单。
5. 永久封禁必须人工二次确认，本 Skill 只执行 7 天封禁。
6. 同一 user 24h 内不重复封，先调 `getAccountStatus` 检查。
7. 解封指令：调 `getAccountStatus` 确认 banned 后，更新为 normal（走 banUser 的反向操作，由运营执行）。

## 计分规则（唯一口径）
- refund_abuse +70：领返后退款且消耗>=80%
- fast_consume +20：7天内消耗返还>=90%
- device_sharing +30：同设备>=3账号领返
- small_amount_brushing +30：名下>=5笔<6元无效小额单
- no_valid_chat +70：领返后0条有效AI对话
- >=90 封7天；60-89 禁领赠送；30-59 观察；<30 放行
- 纯1元测试无其他命中默认观察不处罚

## 活动方案（AI首充全额返）
- 仅6/30/100元三档参与，首充全额返Token，复充6折，无返
- 每人共3次权益（1首充+2复充），累计返还上限2万Token
- 单设备仅前2账号享首充返，第3号起永久不返，无白名单
- 总预算100万Token，耗尽即停
- 退款全额回收，已消耗记负，下次抵扣，负上限2万，超限转封；24h内补款恢复
- 领返后必须有>=1条AI问答，否则计no_valid_chat

## 安全红线
- 无 evidence（score+hits）不执行封号
- 白名单：内部测试号、渠道号永不自动封，只告警
- 所有动作写 risk_detect_log，保留180天

## OpenClaw 接入配置
openclaw.json -> mcpServers:
{"recharge-risk": {"type": "stdio", "command": "java -cp target/classes;<deps> com.demo.risk.mcp.RechargeMcpServer"}}
建议打 fat-jar 后简化为 java -jar recharge-risk-demo.jar。配完重启 Gateway。
