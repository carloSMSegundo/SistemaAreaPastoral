package com.seuprojeto.Dados;

import com.seuprojeto.database.DatabaseConnection;
import com.seuprojeto.database.IgrejaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Igreja {

    private Map<String, Membro> membros; // Mapa de membros
    private Map<String, Grupo> grupos; // Mapa para armazenar grupos
    private Connection connection; // Conexão com o banco de dados
    private PainelFinanceiro painelFinanceiro; // Painel financeiro da igreja
    private List<Evento> eventos; // Lista de eventos
    private IgrejaDAO igrejaDAO; // Referência ao DAO

    public Igreja(IgrejaDAO igrejaDAO) throws SQLException {
        this.igrejaDAO = igrejaDAO; // Inicializa o DAO
        this.membros = new HashMap<>();
        this.grupos = new HashMap<>();
        this.painelFinanceiro = new PainelFinanceiro(igrejaDAO); // Ajuste aqui
        this.eventos = new ArrayList<>();
        carregarDados(); // Carrega dados do banco de dados
    }

    private void carregarDados() {
        carregarMembros();
        carregarGrupos();
        carregarEventos();
    }

    public void carregarMembros() {
        try {
            List<Membro> listaMembros = igrejaDAO.listMembers();
            for (Membro membro : listaMembros) {
                String sexo = membro.getSexo() != null ? membro.getSexo() : "não informado";
                String cargo = membro.getCargo() != null ? membro.getCargo() : "não informado"; // Carrega o cargo
                Membro novoMembro = new Membro(membro.getCpf(), membro.getNome(), membro.getEndereco(), membro.getTelefone(), sexo, cargo);
                membros.put(novoMembro.getCpf(), novoMembro);
            }
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao carregar membros: " + e.getMessage());
        }
    }

    private void carregarGrupos() {
        try {
            List<Grupo> listaGrupos = igrejaDAO.listGrupos();
            for (Grupo grupo : listaGrupos) {
                grupos.put(grupo.getNome(), grupo);
            }
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao carregar grupos: " + e.getMessage());
        }
    }

    private void carregarEventos() {
        try {
            List<Evento> listaEventos = igrejaDAO.listEventos();
            eventos.addAll(listaEventos);
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao carregar eventos: " + e.getMessage());
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    public void adicionarEvento(Evento evento) throws SQLException {
        eventos.add(evento);
        igrejaDAO.addEvento(evento); // Persistir evento no banco de dados
    }

    public void removerEvento(Evento evento) throws SQLException {
        if (eventos.remove(evento)) {
            igrejaDAO.removeEvento(evento); // Persistir remoção no banco de dados
        } else {
            mostrarMensagemErro("Evento não encontrado para remoção.");
        }
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public Membro getMembro(String cpf) {
        return membros.get(cpf);
    }

    public void cadastrarMembro(String cpf, String nome, String endereco, String telefone, String sexo, String cargo) {
        try {
            // Passa os dados para o banco de dados, incluindo o cargo
            igrejaDAO.addMember(cpf, nome, endereco, telefone, sexo, cargo);

            // Cria uma instância do novo membro
            Membro novoMembro = new Membro(cpf, nome, endereco, telefone, sexo, cargo);
            membros.put(cpf, novoMembro);

        } catch (SQLException e) {
            throw new IllegalArgumentException("Erro ao cadastrar membro: " + e.getMessage());
        }
    }

    public void editarMembro(String cpf, String nome, String endereco, String telefone, String sexo, String cargo) throws SQLException {
        Membro membro = membros.get(cpf);
        if (membro != null) {
            membro.setNome(nome);
            membro.setEndereco(endereco);
            membro.setTelefone(telefone);
            membro.setSexo(sexo);
            membro.setCargo(cargo); // Atualiza o cargo do membro
            igrejaDAO.updateMember(cpf, nome, endereco, telefone, sexo, cargo); // Atualiza no banco de dados
        } else {
            throw new IllegalArgumentException("Membro com CPF " + cpf + " não encontrado.");
        }
    }

    public void removerMembro(String cpf) throws SQLException {
        Membro membro = membros.get(cpf);

        if (membro == null) {
            mostrarMensagemErro("Membro com CPF " + cpf + " não encontrado.");
            return; // Sai do método se o membro não existir
        }

        // Verifica se o membro está pagando dízimo
        boolean pagandoDizimo = igrejaDAO.isMembroPagandoDizimo(cpf);
        if (pagandoDizimo) {
            JOptionPane.showMessageDialog(null,
                    "O membro está pagando dízimo e não pode ser removido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return; // Cancela a remoção
        }

        // Verifica se o membro está em algum grupo
        boolean emGrupo = false;

        for (Grupo grupo : grupos.values()) {
            if (grupo.getMembros().contains(membro)) {
                emGrupo = true;
                break;
            }
        }

        if (emGrupo) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "O membro está em um grupo. Ao removê-lo, ele será excluído do grupo também. Deseja continuar?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return; // Cancela a remoção
            }
        }

        // Remove do banco de dados
        igrejaDAO.deleteMember(cpf);
        // Remove do mapa
        membros.remove(cpf);
    }

    public Grupo getGrupo(String nomeGrupo) {
        return grupos.get(nomeGrupo);
    }

    public void adicionarMembroAoGrupo(String nomeGrupo, Membro membro) {
        Grupo grupo = grupos.get(nomeGrupo); // Verifica se o grupo já existe
        if (grupo == null) {
            // Se o grupo não existir, cria um novo grupo "Coroinha"
            grupo = new Grupo(nomeGrupo, membro.getNome()); // Define o responsável como o primeiro membro
            grupos.put(nomeGrupo, grupo);
            try {
                igrejaDAO.addGrupo(nomeGrupo, membro.getNome()); // Persiste no banco de dados
            } catch (SQLException e) {
                throw new IllegalArgumentException("Erro ao criar grupo: " + e.getMessage());
            }
        }
        grupo.adicionarMembro(membro); // Adiciona o membro ao grupo
    }

    public void atualizarGrupo(Grupo grupo) throws SQLException {
        // Verifica se o grupo existe pelo nome
        String verificaSql = "SELECT COUNT(*) FROM grupo WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement verificaPstmt = conn.prepareStatement(verificaSql)) {
            verificaPstmt.setString(1, grupo.getNome());
            ResultSet rs = verificaPstmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("Grupo com nome " + grupo.getNome() + " não encontrado.");
            }
        }

        // Se o grupo existir, realiza a atualização
        String sql = "UPDATE grupo SET nome = ?, responsavel = ? WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grupo.getNome());  // Definindo o novo nome
            pstmt.setString(2, grupo.getResponsavel());
            pstmt.setString(3, grupo.getNome());  // Usando o nome atual para identificar qual registro atualizar
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma linha foi atualizada. Verifique se o nome está correto.");
            }
        } catch (SQLException ex) {
            throw new SQLException("Erro ao atualizar grupo: " + ex.getMessage());
        }
    }

    public Map<String, Membro> getMembros() {
        return membros; // Retorna mapa de membros
    }

    public Map<String, Grupo> getGrupos() {
        return grupos; // Retorna mapa de grupos
    }

    public boolean verificarMembroPorCpf(String cpf) {
        return membros.containsKey(cpf); // Retorna se o membro existe
    }

    public void registrarDizimo(double dizimo, String cpfDoador, String nomeDoador, String observacao) throws SQLException {
        if (verificarMembroPorCpf(cpfDoador)) {
            // Registra no painel financeiro, agora incluindo o nome do doador
            painelFinanceiro.registrarDizimo(dizimo, cpfDoador, nomeDoador, observacao); // Passando o nome do doador

            // Adiciona a transação financeira ao banco de dados
            igrejaDAO.addTransacaoFinanceira("dizimo", dizimo, cpfDoador, nomeDoador, observacao); // Passando o nome do doador
        } else {
            // Lógica para cadastrar membro se não existir (opcional)
            mostrarMensagemErro("Membro não encontrado. Cadastre o membro antes de registrar o dízimo.");
        }
    }

    public void registrarOfertorio(double ofertorio, String observacao) throws SQLException {
        // Registra no painel financeiro
        painelFinanceiro.registrarOfertorio(ofertorio, observacao);

        // Adiciona a transação financeira ao banco de dados
        igrejaDAO.addTransacaoFinanceira("ofertorio", ofertorio, null, null, observacao); // CPF e nome são null
    }

    public void registrarDoacao(double doacao, String observacao) throws SQLException {
        // Registra no painel financeiro
        painelFinanceiro.registrarDoacao(doacao, observacao);

        // Adiciona a transação financeira ao banco de dados
        igrejaDAO.addTransacaoFinanceira("doacao", doacao, null, null, observacao); // CPF e nome são null
    }

    public void registrarRetirada(double valor, String cpfRetirante, String nomeRetirante, String observacao) throws SQLException {
        // Registra no painel financeiro
        painelFinanceiro.registrarRetirada(valor, cpfRetirante, nomeRetirante, observacao);

        // Adiciona a transação financeira ao banco de dados
        igrejaDAO.addTransacaoFinanceira("retirada", valor, cpfRetirante, nomeRetirante, observacao); // Agora passando o CPF do retirante
    }

    public PainelFinanceiro getPainelFinanceiro() {
        return painelFinanceiro;
    }

    public void criarGrupo(String nomeGrupo, String responsavel) {
        if (!grupos.containsKey(nomeGrupo)) {
            Grupo grupo = new Grupo(nomeGrupo, responsavel);
            grupos.put(nomeGrupo, grupo);
            try {
                igrejaDAO.addGrupo(nomeGrupo, responsavel);
            } catch (SQLException e) {
                throw new IllegalArgumentException("Erro ao criar grupo: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Grupo já existente com este nome.");
        }
    }

    // Método para obter o nome de um membro a partir do CPF
    public String obterNomePorCpf(String cpf) {
        Membro membro = membros.get(cpf); // Supondo que você armazena membros em um Map<String, Membro>
        if (membro != null) {
            return membro.getNome(); // Retorna o nome do membro
        }
        return null; // Retorna null se o membro não for encontrado
    }

    public void removerGrupo(String nomeGrupo) throws SQLException {
        if (grupos.remove(nomeGrupo) != null) { // Remove o grupo se existir
            igrejaDAO.deleteGrupo(nomeGrupo); // Remove do banco de dados
        } else {
            mostrarMensagemErro("Grupo não encontrado para remoção.");
        }
    }

    public boolean membroPodeSerRemovido(String cpf) throws SQLException {
        // Verifique se o membro tem dízimos associados
        String query = "SELECT COUNT(*) FROM transacoes_financeiras WHERE cpf_doador = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Retorna true se não houver dízimos
            }
        }
        return false; // Se ocorrer um erro, considerar como não removível
    }
}
