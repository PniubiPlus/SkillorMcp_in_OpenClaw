package com.demo.risk.model;

public class Bonus {

    private String userId;
    private int bonusTokens;
    private int consumedTokens;

    public Bonus() {
    }

    public Bonus(String userId, int bonusTokens, int consumedTokens) {
        this.userId = userId;
        this.bonusTokens = bonusTokens;
        this.consumedTokens = consumedTokens;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBonusTokens() {
        return bonusTokens;
    }

    public void setBonusTokens(int bonusTokens) {
        this.bonusTokens = bonusTokens;
    }

    public int getConsumedTokens() {
        return consumedTokens;
    }

    public void setConsumedTokens(int consumedTokens) {
        this.consumedTokens = consumedTokens;
    }

    public double consumeRate() {
        if (bonusTokens <= 0) {
            return 0;
        }
        return (double) consumedTokens / bonusTokens;
    }
}
