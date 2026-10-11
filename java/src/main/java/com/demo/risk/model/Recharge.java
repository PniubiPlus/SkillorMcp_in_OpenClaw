package com.demo.risk.model;

public class Recharge {

    private String userId;
    private String orderId;
    private double amount;
    private boolean refunded;
    private String deviceId;
    private String ip;

    public Recharge() {
    }

    public Recharge(String userId, String orderId, double amount, boolean refunded, String deviceId, String ip) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.refunded = refunded;
        this.deviceId = deviceId;
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Recharge{userId=" + userId + ", orderId=" + orderId + ", amount=" + amount + ", refunded=" + refunded + "}";
    }
}
