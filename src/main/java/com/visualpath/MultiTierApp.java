package com.visualpath;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

public class MultiTierApp {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/accounts";
        String user = "dbuser";
        String password = "dbpassword";

        System.out.println("Connecting to MySQL Database...");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connection established successfully!");
            
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String response = "<h1>MULTI-TIER WEB APP: ONLINE</h1><p>Connected cleanly to MySQL Data Tier!</p>";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });
            
            System.out.println("Web tier Pull request testing & testing listening on http://localhost:8081");
            server.setExecutor(null);
            server.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Database tier offline: " + e.getMessage());
            System.exit(1);
        }
    }
}
