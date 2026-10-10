"""恶意充值检测引擎"""
from collections import defaultdict
import mock_mcp_server as mcp

def detect():
    recharges = mcp.query_recharges()
    bonus_map = {b["user_id"]: b for b in mcp.query_bonus()}
    # 按设备聚合
    device_users = defaultdict(set)
    for r in recharges:
        device_users[r["device_id"]].add(r["user_id"])
    results = []
    for user in bonus_map:
        score = 0
        hits = []
        b = bonus_map[user]
        user_orders = [r for r in recharges if r["user_id"] == user]
        # 规则1: 退款后秒消耗赠送
        if any(r["refunded"] for r in user_orders) and b["consumed_tokens"] >= b["bonus_tokens"] * 0.9:
            score += 60
            hits.append("refund_abuse")
        # 规则2: 小额高频刷赠送
        if len(user_orders) >= 3 and all(r["amount"] <= 1 for r in user_orders):
            score += 50
            hits.append("small_amount_brushing")
        # 规则3: 设备聚集
        for dev, users in device_users.items():
            if user in users and len(users) >= 2:
                score += 30
                hits.append(f"device_sharing:{dev}")
                break
        # 规则4: 赠送消耗率过高
        rate = b["consumed_tokens"] / max(1, b["bonus_tokens"])
        if rate >= 0.9 and len(user_orders) <= 2:
            score += 20
            hits.append("fast_consume")
        level = "ban" if score >= 85 else ("restrict" if score >= 50 else ("watch" if score >= 30 else "pass"))
        results.append({"user_id": user, "score": score, "hits": hits, "level": level})
    return results

def dispose(results):
    for r in results:
        if r["level"] == "ban":
            mcp.ban_user(r["user_id"], f"score={r['score']} hits={r['hits']}")
        elif r["level"] == "restrict":
            mcp.restrict_bonus(r["user_id"])
    return results
