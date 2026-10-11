package com.demo.risk.dao;

import com.demo.risk.model.Recharge;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RechargeDao {

    private static final String SQL = "SELECT user_id, order_id, amount, refunded, device_id, ip FROM recharge_order";

    private final String url;
    private final String username;
    private final String password;

    public RechargeDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<Recharge> findAll() throws SQLException {
        List<Recharge> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Recharge recharge = new Recharge();
                recharge.setUserId(rs.getString("user_id"));
                recharge.setOrderId(rs.getString("order_id"));
                recharge.setAmount(rs.getDouble("amount"));
                recharge.setRefunded(rs.getInt("refunded") == 1);
                recharge.setDeviceId(rs.getString("device_id"));
                recharge.setIp(rs.getString("ip"));
                list.add(recharge);
            }
        }
        return list;
    }
}
