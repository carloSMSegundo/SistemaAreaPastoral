package com.seuprojeto.database;

import com.seuprojeto.Dados.Evento;
import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Membro; // Certifique-se de ter essa importação
import com.seuprojeto.main.UserSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IgrejaDAO {

    private Connection connection;

    // Construtor que agora utiliza a conexão recebida como argumento
    public IgrejaDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para adicionar um novo membro
    public void addMember(String cpf, String nome, String endereco, String telefone, String sexo, String cargo) throws SQLException {
        String sql = "INSERT INTO membros (cpf, nome, endereco, telefone, sexo, cargo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, nome);
            stmt.setString(3, endereco);
            stmt.setString(4, telefone);
            stmt.setString(5, sexo);
            stmt.setString(6, cargo); // Adiciona o cargo na inserção
            stmt.executeUpdate();
        }
    }

    // Método para listar todos os membros
    public List<Membro> listMembers() throws SQLException {
        List<Membro> lista = new ArrayList<>();
        String sql = "SELECT * FROM membros"; // Certifique-se de que a tabela 'membros' possui a coluna 'cargo'
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Inclui o campo cargo na criação do objeto Membro
                Membro member = new Membro(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("sexo"),
                        rs.getString("cargo") // Adiciona o cargo
                );
                lista.add(member);
            }
        }
        return lista; // Retorna List<Membro>
    }

// Método para atualizar um membro
    public void updateMember(String cpf, String nome, String endereco, String telefone, String sexo, String cargo) throws SQLException {
        String sql = "UPDATE membros SET nome = ?, endereco = ?, telefone = ?, sexo = ?, cargo = ? WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, endereco);
            stmt.setString(3, telefone);
            stmt.setString(4, sexo);
            stmt.setString(5, cargo); // Atualiza o cargo
            stmt.setString(6, cpf); // Condição da atualização
            stmt.executeUpdate();
        }
    }

    // Método para deletar um membro
    public void deleteMember(String cpf) throws SQLException {
        String sql = "DELETE FROM membros WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            int rowsAffected = stmt.executeUpdate();

            // Verifica se algum membro foi removido
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Membro com o CPF " + cpf + " não encontrado no banco de dados.");
            }
        }
    }

    public boolean isMembroPagandoDizimo(String cpf) throws SQLException {
        // Aqui você deve consultar o banco de dados para verificar se há registros de dízimo
        String query = "SELECT COUNT(*) FROM transacoes_financeiras WHERE cpf_doador = ?"; // Exemplo de consulta
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true se houver registros
            }
        }
        return false; // Retorna false se não houver registros
    }

    // Método para listar grupos no banco de dados
    public List<Grupo> listGrupos() throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        String sql = "SELECT nome, responsavel FROM grupo";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String responsavel = resultSet.getString("responsavel");
                grupos.add(new Grupo(nome, responsavel));
            }
        }
        return grupos;
    }

    // Método para adicionar grupo no banco de dados
    public void addGrupo(String nomeGrupo, String responsavel) throws SQLException {
        String query = "INSERT INTO grupo (nome, responsavel) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nomeGrupo);
            stmt.setString(2, responsavel);
            stmt.executeUpdate();
        }
    }

    // Método para associar membro a um grupo no banco de dados
    public void addMembroToGrupo(String cpfMembro, String nomeGrupo) throws SQLException {
        String query = "INSERT INTO grupo_membros (cpf_membro, nome_grupo) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cpfMembro);
            stmt.setString(2, nomeGrupo);
            stmt.executeUpdate();
        }
    }

    public void deleteGrupo(String nomeGrupo) throws SQLException {
        String sql = "DELETE FROM grupo WHERE nome = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nomeGrupo);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Grupo não encontrado para remoção: " + nomeGrupo);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao remover grupo: " + e.getMessage());
        }
    }

    // Método para adicionar transações financeiras no banco de dados
    public void addTransacaoFinanceira(String tipo, double valor, String cpfDoador, String nomeResponsavel, String observacao) throws SQLException {
        // Insere a transação no banco de dados
        String query = "INSERT INTO transacoes_financeiras (tipo_transacao, valor, cpf_doador, nome_responsavel, data_transacao, observacao) VALUES (?, ?, ?, ?, NOW(), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, tipo);
            stmt.setDouble(2, valor);
            stmt.setString(3, cpfDoador);        // Para dízimos e doações
            stmt.setString(4, nomeResponsavel);  // Para retiradas
            stmt.setString(5, observacao);       // Observação adicional
            stmt.executeUpdate();
        }

        // Atualiza o saldo na tabela financeiro
        atualizarSaldo(tipo, valor);
    }

    // Método para listar eventos
    public List<Evento> listEventos() throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String query = "SELECT * FROM eventos"; // Substitua pelo nome correto da tabela

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getTimestamp("data_inicio").toLocalDateTime(),
                        rs.getTimestamp("data_fim").toLocalDateTime(),
                        rs.getString("local")
                );
                eventos.add(evento);
            }
        }
        return eventos;
    }

    // Método para adicionar um evento
    public void addEvento(Evento evento) throws SQLException {
        String query = "INSERT INTO eventos (nome, data_inicio, data_fim, local) VALUES (?, ?, ?, ?)"; // Substitua pelos nomes corretos dos campos

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, evento.getNome());
            pstmt.setTimestamp(2, Timestamp.valueOf(evento.getDataHoraInicio())); // Usando dataHoraInicio
            pstmt.setTimestamp(3, Timestamp.valueOf(evento.getDataHoraFim())); // Usando dataHoraFim
            pstmt.setString(4, evento.getLocalizacao()); // Usando localizacao
            pstmt.executeUpdate();
        }
    }

    // Método para remover um evento
    public void removeEvento(Evento evento) throws SQLException {
        String query = "DELETE FROM eventos WHERE id = ?"; // Alterado para usar ID

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, evento.getId()); // Usando ID do evento
            pstmt.executeUpdate();
        }
    }

    // Método para fechar a conexão
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

// Método para atualizar saldo
    private void atualizarSaldo(String tipo, double valor) throws SQLException {
        String sql;

        // Define a operação de atualização com base no tipo de transação
        if ("dizimo".equalsIgnoreCase(tipo) || "doacao".equalsIgnoreCase(tipo)) {
            // Para dízimos e doações, adiciona ao saldo
            sql = "UPDATE public.financeiro SET saldo = saldo + ? WHERE id = 1"; // Supondo que você tenha apenas uma linha de saldo
        } else if ("retirada".equalsIgnoreCase(tipo)) {
            // Para retiradas, subtrai do saldo
            sql = "UPDATE public.financeiro SET saldo = saldo - ? WHERE id = 1"; // Supondo que você tenha apenas uma linha de saldo
        } else {
            throw new IllegalArgumentException("Tipo de transação desconhecido: " + tipo);
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, valor);
            pstmt.executeUpdate();
        }
    }
// Método para obter todos os eventos

    public List<Evento> getAllEventos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos"; // Ajuste a consulta conforme necessário

        try (PreparedStatement stmt = this.connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getTimestamp("data_inicio").toLocalDateTime(),
                        rs.getTimestamp("data_fim").toLocalDateTime(),
                        rs.getString("local")
                );
                eventos.add(evento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    public Membro buscarMembroPorCpf(String cpf) throws SQLException {
        Membro membro = null; // Inicializa o membro como nulo
        String sql = "SELECT * FROM membros WHERE cpf = ?"; // A tabela 'membros' deve existir no seu banco de dados

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf); // Define o CPF no PreparedStatement
            ResultSet rs = stmt.executeQuery(); // Executa a consulta

            // Verifica se um membro foi encontrado
            if (rs.next()) {
                // Cria um novo objeto Membro a partir dos dados retornados
                membro = new Membro(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("sexo"),
                        rs.getString("cargo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar membro pelo CPF: " + e.getMessage());
            throw e; // Re-lança a exceção para que possa ser tratada em outro lugar
        }

        return membro; // Retorna o membro encontrado ou nulo se não encontrado
    }

    public double buscarSaldo() throws SQLException {
        double saldo = 0;
        String query = "SELECT saldo FROM financeiro WHERE id = ?"; // Assumindo que você está buscando pelo ID
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, 1); // Substitua 1 pelo ID que você precisa buscar
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
        }
        return saldo;
    }

}
