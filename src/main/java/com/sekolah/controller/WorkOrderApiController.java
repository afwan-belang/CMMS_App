package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.model.WorkOrder;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderApiController {

    @GetMapping
    public List<WorkOrder> getAll() {
        List<WorkOrder> list = new ArrayList<>();
        String sql = "SELECT wo.wo_id, a.asset_name, u.full_name, wo.issue_description, wo.priority, wo.status, wo.created_at " +
                "FROM work_orders wo JOIN assets a ON wo.asset_id = a.asset_id LEFT JOIN users u ON wo.technician_id = u.user_id " +
                "ORDER BY wo.created_at DESC";
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                list.add(new WorkOrder(rs.getInt("wo_id"), rs.getString("asset_name"),
                        rs.getString("full_name") == null ? "-" : rs.getString("full_name"),
                        rs.getString("issue_description"), rs.getString("priority"), rs.getString("status"), rs.getTimestamp("created_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @PostMapping
    public boolean create(@RequestBody Map<String, Object> payload) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("INSERT INTO work_orders (asset_id, issue_description, priority, status) VALUES (?, ?, ?, 'OPEN')");
                 PreparedStatement ps2 = conn.prepareStatement("UPDATE assets SET status = 'DOWN' WHERE asset_id = ?")) {
                ps1.setInt(1, Integer.parseInt(payload.get("assetId").toString()));
                ps1.setString(2, payload.get("issue").toString());
                ps1.setString(3, payload.get("priority").toString());
                ps1.executeUpdate();
                ps2.setInt(1, Integer.parseInt(payload.get("assetId").toString()));
                ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (Exception e) { conn.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @PutMapping("/{id}/take")
    public boolean takeTicket(@PathVariable int id, @RequestBody Map<String, Integer> payload) {
        String sql = "UPDATE work_orders SET status = 'IN_PROGRESS', technician_id = ? WHERE wo_id = ? AND status = 'OPEN'";
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payload.get("technicianId"));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @PutMapping("/{id}/complete")
    public boolean complete(@PathVariable int id, @RequestBody Map<String, String> payload) {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement("UPDATE work_orders SET status = 'COMPLETED', action_taken = ? WHERE wo_id = ? AND status = 'IN_PROGRESS'");
                ps1.setString(1, payload.get("action"));
                ps1.setInt(2, id);
                if (ps1.executeUpdate() == 0) { conn.rollback(); return false; }

                int assetId = 0;
                PreparedStatement ps2 = conn.prepareStatement("SELECT asset_id FROM work_orders WHERE wo_id = ?");
                ps2.setInt(1, id);
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) assetId = rs.getInt("asset_id");

                PreparedStatement ps3 = conn.prepareStatement("UPDATE assets SET status = 'RUNNING' WHERE asset_id = ?");
                ps3.setInt(1, assetId);
                ps3.executeUpdate();
                conn.commit();
                return true;
            } catch (Exception e) { conn.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}