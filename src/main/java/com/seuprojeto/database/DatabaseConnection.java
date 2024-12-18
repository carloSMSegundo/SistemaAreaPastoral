package com.seuprojeto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Ajuste o URL se necessário
    private static final String USER = "postgres"; // Substitua pelo seu usuário do PostgreSQL
    private static final String PASSWORD = "alunolcc"; // Substitua pela sua senha do PostgreSQL

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return connection;
    }
}
