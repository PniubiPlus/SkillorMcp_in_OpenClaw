package com.skillor.mcp.model;

/** 充值流水，对应 python/mock_mcp_server.py 中的 Recharge */
public class Recharge {
    public String userId;
    public String orderId;
    public double amount;
    public boolean refunded;
    public String deviceId;
    public String ip;

    public Recharge(String userId, String orderId, double amount, boolean refunded, String deviceId, String ip) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.refunded = refunded;
        this.deviceId = deviceId;
        this.ip = ip;
    }
}
