package com.demo.risk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AiChatLogDao {

    private final String url;
    private final String username;
    private final String password;

    public AiChatLogDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public int countByUser(String userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ai_chat_log WHERE user_id=?")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
