package com.skillor.mcp;

import com.skillor.mcp.model.Bonus;
import com.skillor.mcp.model.Recharge;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Mock 内部数据 MCP
 * 对标 python/mock_mcp_server.py：提供查询 + 封号写入
 */
public class MockMcpServer {

    private static final List<Recharge> RECHARGES = Arrays.asList(
            new Recharge("user_001", "ord_001", 6.0, false, "dev_A", "1.1.1.1"),
            new Recharge("user_002", "ord_002", 6.0, true, "dev_B", "2.2.2.2"),
            new Recharge("user_003", "ord_003", 1.0, false, "dev_B", "2.2.2.2"),
            new Recharge("user_003", "ord_004", 1.0, false, "dev_B", "2.2.2.2"),
            new Recharge("user_003", "ord_005", 1.0, false, "dev_B", "2.2.2.2"),
            new Recharge("user_004", "ord_006", 100.0, false, "dev_C", "3.3.3.3")
    );

    private static final List<Bonus> BONUS = Arrays.asList(
            new Bonus("user_001", 1000, 100),
            new Bonus("user_002", 1000, 1000),
            new Bonus("user_003", 3000, 2900),
            new Bonus("user_004", 5000, 500)
    );

    /** userId -> 封禁信息 */
    public static final Map<String, String> BANNED = new LinkedHashMap<>();

    public static List<Recharge> queryRecharges() {
        return new ArrayList<>(RECHARGES);
    }

    public static List<Bonus> queryBonus() {
        return new ArrayList<>(BONUS);
    }

    public static boolean banUser(String userId, String reason) {
        BANNED.put(userId, reason + " | " + LocalDateTime.now() + " | 7d");
        System.out.println("[MCP] banUser " + userId + " reason=" + reason);
        return true;
    }

    public static boolean restrictBonus(String userId) {
        System.out.println("[MCP] restrictBonus " + userId);
        return true;
    }

    public static String getAccountStatus(String userId) {
        return BANNED.containsKey(userId) ? "banned" : "normal";
    }
}
