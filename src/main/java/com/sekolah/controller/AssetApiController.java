package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.model.Asset;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetApiController {

    @GetMapping
    public List<Asset> getAll() {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM assets ORDER BY asset_id DESC";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("asset_id"), rs.getString("asset_name"),
                        rs.getString("serial_number"), rs.getString("location"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return assets;
    }

    @PostMapping
    public ResponseEntity<?> addAsset(@RequestBody Map<String, String> payload) {
        String sql = "INSERT INTO assets (asset_name, serial_number, location, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payload.get("name"));
            ps.setString(2, payload.get("serialNumber"));
            ps.setString(3, payload.get("location"));
            ps.setString(4, payload.get("status"));
            ps.executeUpdate();
            return ResponseEntity.ok("Asset berhasil ditambahkan");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menambah aset");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable int id) {
        // Cek apakah aset sedang dipakai di work_orders
        String checkSql = "SELECT COUNT(*) FROM work_orders WHERE asset_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Tidak bisa menghapus mesin karena masih memiliki riwayat perbaikan (Work Order).");
            }

            // Jika aman, hapus
            String deleteSql = "DELETE FROM assets WHERE asset_id = ?";
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, id);
                deletePs.executeUpdate();
                return ResponseEntity.ok("Asset berhasil dihapus");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan server");
        }
    }
}