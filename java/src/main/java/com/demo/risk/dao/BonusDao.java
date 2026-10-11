package com.demo.risk.dao;

import com.demo.risk.model.Bonus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BonusDao {

    private static final String SQL = "SELECT user_id, bonus_tokens, consumed_tokens FROM bonus_token";

    private final String url;
    private final String username;
    private final String password;

    public BonusDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<Bonus> findAll() throws SQLException {
        List<Bonus> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Bonus bonus = new Bonus();
                bonus.setUserId(rs.getString("user_id"));
                bonus.setBonusTokens(rs.getInt("bonus_tokens"));
                bonus.setConsumedTokens(rs.getInt("consumed_tokens"));
                list.add(bonus);
            }
        }
        return list;
    }
}
