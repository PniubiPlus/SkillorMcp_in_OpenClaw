package com.skillor.mcp.model;

/** 赠送 Token 明细，对应 python/mock_mcp_server.py 中的 Bonus */
public class Bonus {
    public String userId;
    public int bonusTokens;
    public int consumedTokens;

    public Bonus(String userId, int bonusTokens, int consumedTokens) {
        this.userId = userId;
        this.bonusTokens = bonusTokens;
        this.consumedTokens = consumedTokens;
    }
}
