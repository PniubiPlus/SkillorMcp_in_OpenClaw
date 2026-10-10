"""Mock 内部数据 MCP: 提供充值/赠送/消费查询 + 封号写入"""
from dataclasses import dataclass, asdict
from datetime import datetime
from typing import List, Dict

@dataclass
class Recharge:
    user_id: str
    order_id: str
    amount: float
    refunded: bool
    device_id: str
    ip: str

@dataclass
class Bonus:
    user_id: str
    bonus_tokens: int
    consumed_tokens: int

MOCK_RECHARGES: List[Recharge] = [
    Recharge("user_001", "ord_001", 6.0, False, "dev_A", "1.1.1.1"),
    Recharge("user_002", "ord_002", 6.0, True, "dev_B", "2.2.2.2"),
    Recharge("user_003", "ord_003", 1.0, False, "dev_B", "2.2.2.2"),
    Recharge("user_003", "ord_004", 1.0, False, "dev_B", "2.2.2.2"),
    Recharge("user_003", "ord_005", 1.0, False, "dev_B", "2.2.2.2"),
    Recharge("user_004", "ord_006", 100.0, False, "dev_C", "3.3.3.3"),
]

MOCK_BONUS: List[Bonus] = [
    Bonus("user_001", 1000, 100),
    Bonus("user_002", 1000, 1000),
    Bonus("user_003", 3000, 2900),
    Bonus("user_004", 5000, 500),
]

BANNED: Dict[str, dict] = {}

def query_recharges():
    return [asdict(r) for r in MOCK_RECHARGES]

def query_bonus():
    return [asdict(b) for b in MOCK_BONUS]

def ban_user(user_id: str, reason: str):
    BANNED[user_id] = {"reason": reason, "time": datetime.now().isoformat(), "duration": "7d"}
    return {"ok": True, "user_id": user_id}

def restrict_bonus(user_id: str):
    return {"ok": True, "user_id": user_id, "action": "restrict_bonus"}

def get_account_status(user_id: str):
    if user_id in BANNED:
        return {"user_id": user_id, "status": "banned"}
    return {"user_id": user_id, "status": "normal"}
