package com.visualpath;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MultiTierApp {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/accounts";
        String user = "dbuser";
        String password = "dbpassword";

        System.out.println("Connecting to MySQL Database...");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connection established successfully!");
            
            // Create table if not exists
            createTable(conn);
            
            // Insert sample data
            insertData(conn, "john_doe", "john@example.com");
            
            HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    try {
                        String response = fetchDataFromDB(conn);
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } catch (Exception e) {
                        String error = "Error: " + e.getMessage();
                        exchange.sendResponseHeaders(500, error.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(error.getBytes());
                        os.close();
                    }
                }
            });
            
            System.out.println("Web tier edwin today 9th June listening on http://localhost:8085");
            server.setExecutor(null);
            server.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Database tier offline: " + e.getMessage());
            System.exit(1);
        }
    }

    // Create database table
    private static void createTable(Connection conn) throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "username VARCHAR(50) NOT NULL UNIQUE," +
                     "email VARCHAR(100) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created or already exists");
        }
    }

    // Insert data using PreparedStatement
    private static void insertData(Connection conn, String username, String email) throws Exception {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("Data inserted successfully");
        }
    }

    // Fetch data from database
    private static String fetchDataFromDB(Connection conn) throws Exception {
        String sql = "SELECT id, username, email FROM users";
        StringBuilder response = new StringBuilder("<h1>MULTI-TIER WEB APP: ONLINE</h1>");
        response.append("<p>Connected to MySQL Data Tier!</p><ul>");
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                response.append("<li>ID: ").append(rs.getInt("id"))
                        .append(" | User: ").append(rs.getString("username"))
                        .append(" | Email: ").append(rs.getString("email")).append("</li>");
            }
        }
        response.append("</ul>");
        return response.toString();
    }
}
