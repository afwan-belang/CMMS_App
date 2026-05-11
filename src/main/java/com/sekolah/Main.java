package com.sekolah;

import com.sekolah.config.DatabaseConnection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Bean ini akan otomatis dieksekusi oleh Spring Boot saat aplikasi baru menyala
    @Bean
    public CommandLineRunner testDatabaseConnection() {
        return args -> {
            System.out.println("\n=============================================");
            System.out.println("⏳ Sedang mencoba terhubung ke TiDB Cloud...");

            try (Connection conn = DatabaseConnection.connect()) {
                System.out.println("✅ STATUS: BERHASIL TERHUBUNG!");
                System.out.println("Database siap digunakan.");
                System.out.println("=============================================\n");
            } catch (Exception e) {
                System.out.println("❌ STATUS: GAGAL TERHUBUNG!");
                System.out.println("Penyebab Error: " + e.getMessage());
                System.out.println("=============================================\n");
            }
        };
    }
}