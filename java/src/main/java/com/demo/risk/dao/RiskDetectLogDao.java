package com.demo.risk.dao;

import com.demo.risk.model.RiskResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RiskDetectLogDao {

    private static final String SQL = "INSERT INTO risk_detect_log(user_id, score, hits, level, action) VALUES(?, ?, ?, ?, ?)";

    private final String url;
    private final String username;
    private final String password;

    public RiskDetectLogDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void insert(RiskResult result, String action) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, result.getUserId());
            ps.setInt(2, result.getScore());
            ps.setString(3, String.join(",", result.getHits()));
            ps.setString(4, result.getLevel());
            ps.setString(5, action);
            ps.executeUpdate();
        }
    }
}
