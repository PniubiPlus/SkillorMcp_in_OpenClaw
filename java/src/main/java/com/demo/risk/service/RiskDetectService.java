package com.demo.risk.service;

import com.demo.risk.dao.AccountStatusDao;
import com.demo.risk.dao.AiChatLogDao;
import com.demo.risk.dao.BonusDao;
import com.demo.risk.dao.RechargeDao;
import com.demo.risk.dao.RiskDetectLogDao;
import com.demo.risk.model.Bonus;
import com.demo.risk.model.Recharge;
import com.demo.risk.model.RiskResult;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RiskDetectService {

    private final RechargeDao rechargeDao;
    private final BonusDao bonusDao;
    private final AiChatLogDao chatDao;
    private final RiskDetectLogDao logDao;

    public RiskDetectService(RechargeDao rechargeDao, BonusDao bonusDao, AiChatLogDao chatDao, RiskDetectLogDao logDao) {
        this.rechargeDao = rechargeDao;
        this.bonusDao = bonusDao;
        this.chatDao = chatDao;
        this.logDao = logDao;
    }

    public List<RiskResult> detect() throws SQLException {
        List<Recharge> recharges = rechargeDao.findAll();
        List<Bonus> bonuses = bonusDao.findAll();

        Map<String, List<Recharge>> ordersByUser = new HashMap<>();
        for (Recharge r : recharges) {
            ordersByUser.computeIfAbsent(r.getUserId(), k -> new ArrayList<>()).add(r);
        }

        Map<String, Set<String>> usersByDevice = new HashMap<>();
        for (Recharge r : recharges) {
            usersByDevice.computeIfAbsent(r.getDeviceId(), k -> new HashSet<>()).add(r.getUserId());
        }

        List<RiskResult> results = new ArrayList<>();
        for (Bonus b : bonuses) {
            RiskResult result = new RiskResult();
            result.setUserId(b.getUserId());
            List<Recharge> orders = ordersByUser.getOrDefault(b.getUserId(), List.of());

            boolean hasRefund = orders.stream().anyMatch(Recharge::isRefunded);
            if (hasRefund && b.consumeRate() >= 0.8) {
                result.addHit("refund_abuse", 70);
            }

            long smallCount = orders.stream().filter(o -> o.getAmount() < 6).count();
            if (smallCount >= 5) {
                result.addHit("small_amount_brushing", 30);
            }

            for (Map.Entry<String, Set<String>> e : usersByDevice.entrySet()) {
                if (e.getValue().contains(b.getUserId()) && e.getValue().size() >= 3) {
                    result.addHit("device_sharing:" + e.getKey(), 30);
                    break;
                }
            }

            if (b.consumeRate() >= 0.9) {
                result.addHit("fast_consume", 20);
            }

            if (chatDao.countByUser(b.getUserId()) == 0) {
                result.addHit("no_valid_chat", 70);
            }

            int score = result.getScore();
            if (score >= 90) {
                result.setLevel("ban");
            } else if (score >= 60) {
                result.setLevel("restrict");
            } else if (score >= 30) {
                result.setLevel("watch");
            } else {
                result.setLevel("pass");
            }
            results.add(result);
        }
        return results;
    }

    public void saveLogs(List<RiskResult> results, String action) throws SQLException {
        for (RiskResult r : results) {
            logDao.insert(r, action);
        }
    }
}
