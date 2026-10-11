package com.demo.risk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountStatusDao {

    private static final String SELECT_SQL = "SELECT status FROM account_status WHERE user_id = ?";
    private static final String UPSERT_SQL = "INSERT INTO account_status(user_id, status, ban_reason) VALUES(?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE status = VALUES(status), ban_reason = VALUES(ban_reason)";

    private final String url;
    private final String username;
    private final String password;

    public AccountStatusDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getStatus(String userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(SELECT_SQL)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : "normal";
            }
        }
    }

    public void updateStatus(String userId, String status, String reason) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(UPSERT_SQL)) {
            ps.setString(1, userId);
            ps.setString(2, status);
            ps.setString(3, reason);
            ps.executeUpdate();
        }
    }
}
