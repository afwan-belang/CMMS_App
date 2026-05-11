package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String plainPassword = payload.get("password");
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && passwordEncoder.matches(plainPassword, rs.getString("password"))) {
                String token = jwtUtil.generateToken(rs.getInt("user_id"), username, rs.getString("role"), rs.getString("full_name"));
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", rs.getInt("user_id"));
                userData.put("fullName", rs.getString("full_name"));
                userData.put("role", rs.getString("role"));
                Map<String, Object> res = new HashMap<>();
                res.put("token", token);
                res.put("user", userData);
                return res;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @GetMapping("/fix")
    public String fixDatabase() {
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE users MODIFY password VARCHAR(255)");
            stmt.executeUpdate("UPDATE users SET password = '" + passwordEncoder.encode("admin123") + "' WHERE username = 'admin'");
            stmt.executeUpdate("UPDATE users SET password = '" + passwordEncoder.encode("budi123") + "' WHERE username = 'budi'");
            stmt.executeUpdate("UPDATE users SET password = '" + passwordEncoder.encode("agus123") + "' WHERE username = 'agus'");
            return "SUCCESS: Database Fixed! Silakan coba login kembali.";
        } catch (Exception e) { return "ERROR: " + e.getMessage(); }
    }
}