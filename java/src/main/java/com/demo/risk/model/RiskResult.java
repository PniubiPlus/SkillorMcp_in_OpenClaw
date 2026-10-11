package com.demo.risk.model;

import java.util.ArrayList;
import java.util.List;

public class RiskResult {

    private String userId;
    private int score;
    private List<String> hits = new ArrayList<>();
    private String level;

    public RiskResult() {
    }

    public RiskResult(String userId, int score, List<String> hits, String level) {
        this.userId = userId;
        this.score = score;
        this.hits = hits;
        this.level = level;
    }

    public void addHit(String rule, int points) {
        hits.add(rule);
        score += points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getHits() {
        return hits;
    }

    public void setHits(List<String> hits) {
        this.hits = hits;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return userId + " score=" + score + " " + hits + " -> " + level;
    }
}
