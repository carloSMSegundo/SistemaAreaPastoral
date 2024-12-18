package com.seuprojeto.Dados;

import com.seuprojeto.database.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Grupo {

    private static int ultimoId = 0;  // Variável estática para gerar o próximo ID
    private int id;  // Identificador único do grupo
    private String nome;
    private String responsavel;
    private List<Membro> membros;

    // Construtor atualizado para gerar o id automaticamente
    public Grupo(String nome, String responsavel) {
        this.id = ++ultimoId;  // Incrementa o ID a cada vez que um novo grupo é criado
        this.nome = nome;
        this.responsavel = responsavel;
        this.membros = new ArrayList<>();
    }

    // Método para adicionar membro ao grupo e ao banco de dados
    public void adicionarMembro(Membro membro) {
        if (!membros.contains(membro)) {  // Verifica se o membro já está no grupo
            membros.add(membro);
            if (!adicionarMembroAoBanco(this.id, membro.getCpf())) {
                JOptionPane.showMessageDialog(null, "Erro ao adicionar membro ao banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Membro já está no grupo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para adicionar o membro ao banco de dados
    private boolean adicionarMembroAoBanco(int grupoId, String cpf) {
        String sql = "INSERT INTO grupo_membros (grupo_id, membro_cpf) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grupoId);
            pstmt.setString(2, cpf);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao adicionar membro ao banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Método para carregar membros do banco de dados
    public void carregarMembros(Connection connection) throws SQLException {
        // Limpa a lista de membros antes de carregar os novos do banco de dados
        this.membros.clear(); // Considere se isso é necessário

        String sql = "SELECT membro_cpf FROM grupo_membros WHERE grupo_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String cpf = rs.getString("membro_cpf");
                String nome = buscarMembroPorCpf(cpf, connection); // Busca apenas o nome do membro
                if (nome != null) {
                    // Se o membro não estiver na lista, crie um novo objeto Membro com os dados disponíveis
                    Membro membro = new Membro(cpf, nome, null, null, null, null); // Ajuste os parâmetros conforme necessário
                    if (!this.membros.contains(membro)) {
                        this.membros.add(membro);
                    }
                }
            }
        }
    }

    // Método auxiliar para buscar membro pelo CPF no banco de dados
    private String buscarMembroPorCpf(String cpf, Connection connection) throws SQLException {
        String sql = "SELECT nome FROM membros WHERE cpf = ?"; // Apenas seleciona o nome
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome"); // Retorna apenas o nome
            }
        }
        return null; // Retorna null se não encontrar o membro
    }

    // Método para remover membro do grupo e do banco de dados
    public void removerMembro(Membro membro) {
        if (membros.remove(membro)) {  // Se o membro foi removido da lista
            if (!removerMembroDoBanco(this.id, membro.getCpf())) {
                JOptionPane.showMessageDialog(null, "Erro ao remover membro do banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Membro não encontrado no grupo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para remover o membro do banco de dados
    private boolean removerMembroDoBanco(int grupoId, String cpf) {
        String sql = "DELETE FROM grupo_membros WHERE grupo_id = ? AND membro_cpf = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grupoId);
            pstmt.setString(2, cpf);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao remover membro do banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Getter para retornar o id do grupo
    public int getId() {
        return id;
    }

    // Outros getters
    public String getNome() {
        return nome;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public List<Membro> getMembros() {
        return membros; // Retorna a lista de membros
    }

    // Setters para permitir a atualização do nome e do responsável
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    // Método toString para exibir informações do grupo
    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nome + ", Responsável: " + responsavel;
    }
}
