package com.demo.risk.service;

import com.demo.risk.dao.AccountStatusDao;
import com.demo.risk.model.RiskResult;
import java.sql.SQLException;
import java.util.List;

public class DisposeService {

    private final AccountStatusDao accountStatusDao;

    public DisposeService(AccountStatusDao accountStatusDao) {
        this.accountStatusDao = accountStatusDao;
    }

    public void dispose(List<RiskResult> results) throws SQLException {
        for (RiskResult r : results) {
            if ("ban".equals(r.getLevel())) {
                String reason = "score=" + r.getScore() + " hits=" + r.getHits();
                accountStatusDao.updateStatus(r.getUserId(), "banned", reason);
            } else if ("restrict".equals(r.getLevel())) {
                accountStatusDao.updateStatus(r.getUserId(), "restricted", "bonus restricted");
            }
        }
    }
}
