package com.demo.risk.mcp;

import com.demo.risk.dao.AccountStatusDao;
import com.demo.risk.dao.AiChatLogDao;
import com.demo.risk.dao.BonusDao;
import com.demo.risk.dao.RechargeDao;
import com.demo.risk.dao.RiskDetectLogDao;
import com.demo.risk.model.Bonus;
import com.demo.risk.model.Recharge;
import com.demo.risk.model.RiskResult;
import com.demo.risk.service.DisposeService;
import com.demo.risk.service.RiskDetectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RechargeMcpServer {

    private static final String URL = "jdbc:mysql://localhost:3306/openclaw_Detect_Anomalies?useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private final ObjectMapper mapper = new ObjectMapper();
    private final RiskDetectService detector;
    private final DisposeService disposer;
    private final RechargeDao rechargeDao;
    private final BonusDao bonusDao;
    private final AccountStatusDao statusDao;

    public RechargeMcpServer() {
        RechargeDao rechargeDao = new RechargeDao(URL, USER, PASSWORD);
        BonusDao bonusDao = new BonusDao(URL, USER, PASSWORD);
        RiskDetectLogDao logDao = new RiskDetectLogDao(URL, USER, PASSWORD);
        AccountStatusDao statusDao = new AccountStatusDao(URL, USER, PASSWORD);
        AiChatLogDao chatDao = new AiChatLogDao(URL, USER, PASSWORD);
        this.rechargeDao = rechargeDao;
        this.bonusDao = bonusDao;
        this.statusDao = statusDao;
        this.detector = new RiskDetectService(rechargeDao, bonusDao, chatDao, logDao);
        this.disposer = new DisposeService(statusDao);
    }

    public static void main(String[] args) throws Exception {
        new RechargeMcpServer().serve();
    }

    private void serve() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            ObjectNode req = (ObjectNode) mapper.readTree(line);
            ObjectNode resp = handle(req);
            out.println(mapper.writeValueAsString(resp));
        }
    }

    private ObjectNode handle(ObjectNode req) {
        ObjectNode resp = mapper.createObjectNode();
        resp.put("jsonrpc", "2.0");
        resp.set("id", req.get("id"));
        try {
            String method = req.path("method").asText();
            ObjectNode params = req.has("params") ? (ObjectNode) req.get("params") : mapper.createObjectNode();
            Object result;
            switch (method) {
                case "initialize":
                    result = toolResult("recharge-risk-mcp/1.0.0");
                    break;
                case "tools/list":
                    result = toolsList();
                    break;
                case "tools/call":
                    result = toolsCall(params);
                    break;
                default:
                    return error(resp, -32601, "unknown method: " + method);
            }
            resp.set("result", mapper.valueToTree(result));
        } catch (Exception e) {
            return error(resp, -32603, e.getMessage());
        }
        return resp;
    }

    private ObjectNode error(ObjectNode resp, int code, String msg) {
        ObjectNode err = mapper.createObjectNode();
        err.put("code", code);
        err.put("message", msg == null ? "error" : msg);
        resp.set("error", err);
        return resp;
    }

    private Object toolsList() {
        ArrayNode tools = mapper.createArrayNode();
        tools.add(tool("detectRisk", "检测恶意充值并打分", "object"));
        tools.add(tool("queryRecharges", "查询充值流水", "object"));
        tools.add(tool("queryBonus", "查询赠送Token", "object"));
        tools.add(tool("banUser", "封禁用户7天", "object"));
        tools.add(tool("restrictBonus", "禁止领取赠送", "object"));
        tools.add(tool("getAccountStatus", "查询账号状态", "object"));
        ObjectNode root = mapper.createObjectNode();
        root.set("tools", tools);
        return root;
    }

    private ObjectNode tool(String name, String desc, String type) {
        ObjectNode t = mapper.createObjectNode();
        t.put("name", name);
        t.put("description", desc);
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", type);
        t.set("inputSchema", schema);
        return t;
    }

    private Object toolsCall(ObjectNode params) throws Exception {
        String name = params.path("name").asText();
        ObjectNode args = params.has("arguments") ? (ObjectNode) params.get("arguments") : mapper.createObjectNode();
        switch (name) {
            case "detectRisk": {
                List<RiskResult> results = detector.detect();
                detector.saveLogs(results, "mcp");
                return toolResult(mapper.writeValueAsString(results));
            }
            case "queryRecharges": {
                List<Recharge> list = rechargeDao.findAll();
                return toolResult(mapper.writeValueAsString(list));
            }
            case "queryBonus": {
                List<Bonus> list = bonusDao.findAll();
                return toolResult(mapper.writeValueAsString(list));
            }
            case "banUser": {
                String uid = args.path("user_id").asText();
                statusDao.updateStatus(uid, "banned", args.path("reason").asText("mcp ban"));
                return toolResult("banned:" + uid);
            }
            case "restrictBonus": {
                String uid = args.path("user_id").asText();
                statusDao.updateStatus(uid, "restricted", "bonus restricted");
                return toolResult("restricted:" + uid);
            }
            case "getAccountStatus": {
                String uid = args.path("user_id").asText();
                return toolResult(statusDao.getStatus(uid));
            }
            default:
                throw new IllegalArgumentException("unknown tool: " + name);
        }
    }

    private ObjectNode toolResult(String text) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode content = mapper.createArrayNode();
        ObjectNode item = mapper.createObjectNode();
        item.put("type", "text");
        item.put("text", text);
        content.add(item);
        root.set("content", content);
        return root;
    }
}
