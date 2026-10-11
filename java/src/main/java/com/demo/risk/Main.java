package com.demo.risk;

import com.demo.risk.dao.AccountStatusDao;
import com.demo.risk.dao.AiChatLogDao;
import com.demo.risk.dao.BonusDao;
import com.demo.risk.dao.RechargeDao;
import com.demo.risk.dao.RiskDetectLogDao;
import com.demo.risk.model.RiskResult;
import com.demo.risk.service.DisposeService;
import com.demo.risk.service.RiskDetectService;
import java.util.List;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/openclaw_Detect_Anomalies?useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) throws Exception {
        RechargeDao rechargeDao = new RechargeDao(URL, USER, PASSWORD);
        BonusDao bonusDao = new BonusDao(URL, USER, PASSWORD);
        RiskDetectLogDao logDao = new RiskDetectLogDao(URL, USER, PASSWORD);
        AccountStatusDao statusDao = new AccountStatusDao(URL, USER, PASSWORD);
        AiChatLogDao chatDao = new AiChatLogDao(URL, USER, PASSWORD);

        RiskDetectService detector = new RiskDetectService(rechargeDao, bonusDao, chatDao, logDao);
        DisposeService disposer = new DisposeService(statusDao);

        System.out.println("=== 恶意充值检测 Demo (Java) ===");
        List<RiskResult> results = detector.detect();
        for (RiskResult r : results) {
            System.out.println(r);
        }
        disposer.dispose(results);
        detector.saveLogs(results, "auto");
        System.out.println("done.");
    }
}
