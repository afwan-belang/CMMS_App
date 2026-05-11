package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalAssets", 0);
        stats.put("runningAssets", 0);
        stats.put("openTickets", 0);

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM assets");
            if (rs1.next()) stats.put("totalAssets", rs1.getInt(1));

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM assets WHERE status = 'RUNNING'");
            if (rs2.next()) stats.put("runningAssets", rs2.getInt(1));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM work_orders WHERE status = 'OPEN' OR status = 'IN_PROGRESS'");
            if (rs3.next()) stats.put("openTickets", rs3.getInt(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}