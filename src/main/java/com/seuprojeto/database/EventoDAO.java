package com.seuprojeto.database;

import com.seuprojeto.Dados.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    private Connection connection;

    public EventoDAO() {
        // Utilizando a classe DatabaseConnection para obter a conexão
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Evento> listarEventos() {
        List<Evento> eventos = new ArrayList<>();
        try {
            String sql = "SELECT * FROM evento";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Usar o construtor com ID
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getTimestamp("data_inicio").toLocalDateTime(),
                        rs.getTimestamp("data_fim").toLocalDateTime(),
                        rs.getString("localizacao")
                );
                eventos.add(evento);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    public Evento getEventoById(int id) {
        Evento evento = null;
        try {
            String sql = "SELECT * FROM evento WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Usar o construtor com ID
                evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getTimestamp("data_inicio").toLocalDateTime(),
                        rs.getTimestamp("data_fim").toLocalDateTime(),
                        rs.getString("localizacao")
                );
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evento;
    }

    public void removerEvento(int id) {
        try {
            String sql = "DELETE FROM evento WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    // Outros métodos de CRUD podem ser adicionados aqui


}
