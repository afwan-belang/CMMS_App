package com.sekolah.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // 1. URL berisi Host, Port, dan Nama Database
    // Ganti "test" dengan nama database yang benar jika Anda menggunakan nama lain
    private static final String URL = "jdbc:mysql://gateway01.ap-southeast-1.prod.alicloud.tidbcloud.com:4000/db_cmms_sekolah?useSSL=true&requireSSL=true";

    // 2. Masukkan Username TiDB Anda di sini
    private static final String USER = "w8LjCAThAQZRFRn.root";

    // 3. Masukkan Password TiDB Anda di sini
    private static final String PASSWORD = "o7NH2c5foxyLutrT";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}