package com.seuprojeto.Dados;

import com.seuprojeto.database.DatabaseConnection;
import static com.seuprojeto.database.DatabaseConnection.getConnection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.seuprojeto.database.IgrejaDAO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PainelFinanceiro {

    private double dizimo;
    private double ofertorio;
    private double doacao;
    private double saldo;
    private List<Log> logTransacoes;
    private IgrejaDAO igrejaDAO; // DAO

    // Construtor para receber o DAO e inicializar os valores
    public PainelFinanceiro(IgrejaDAO igrejaDAO) throws SQLException {
        this.igrejaDAO = igrejaDAO; // Inicializa o DAO
        this.dizimo = 0; // Inicializa o dízimo
        this.ofertorio = 0; // Inicializa o ofertório
        this.doacao = 0; // Inicializa a doação
        this.saldo = igrejaDAO.buscarSaldo(); // Carrega o saldo inicial do banco de dados
        this.logTransacoes = new ArrayList<>();
    }

    // Método para buscar membros do banco de dados via DAO
    public List<Membro> listMembers() throws SQLException {
        return igrejaDAO.listMembers(); // Chama o método do DAO que busca membros
    }

    // Método para buscar um membro pelo CPF
    public Membro getMembro(String cpf) throws SQLException {
        return igrejaDAO.buscarMembroPorCpf(cpf); // Chama o método do DAO que busca um membro específico pelo CPF
    }

    // Método para buscar o saldo do banco de dados
    public void carregarSaldo() throws SQLException {
        this.saldo = igrejaDAO.buscarSaldo(); // Atualiza o saldo a partir do banco de dados
    }

    // Registrar dízimo com detalhes
    public double registrarDizimo(double valor, String cpfDoador, String nomeDoador, String observacao) {
        double valorComDesconto = valor;

        // Chama a função no banco para aplicar o desconto, se aplicável
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT registrar_dizimo_com_desconto(?, ?, ?) AS valor_final")) {

            stmt.setString(1, cpfDoador);
            stmt.setBigDecimal(2, BigDecimal.valueOf(valor));
            stmt.setString(3, "dizimo"); // Define o tipo de transação

            // Executa a query e armazena o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    valorComDesconto = rs.getDouble("valor_final"); // Obtém o valor com desconto
                }
            }

            // Atualiza o saldo e o dízimo com o valor retornado
            this.dizimo += valorComDesconto;
            this.saldo += valorComDesconto;

            registrarLog("Dízimo", nomeDoador, valorComDesconto);
            System.out.println("Dízimo registrado com desconto: " + valorComDesconto + " - Doador: " + nomeDoador + " (CPF: " + cpfDoador + ")");

            // Registra a transação no banco
            //igrejaDAO.addTransacaoFinanceira("dizimo", valorComDesconto, cpfDoador, nomeDoador, observacao);

        } catch (SQLException e) {
            System.err.println("Erro ao registrar dízimo no banco de dados: " + e.getMessage());
        }

        // Retorna o valor final com desconto aplicado
        return valorComDesconto;
    }

    // Registrar ofertório sem identificar o doador
    public void registrarOfertorio(double valor, String observacao) {
        this.ofertorio += valor;
        this.saldo += valor; // Atualiza o saldo
        registrarLog("Ofertório", "Não Identificado", valor);

        try {
            igrejaDAO.addTransacaoFinanceira("ofertorio", valor, null, "Não Identificado", observacao); // Sem CPF
        } catch (SQLException e) {
            System.err.println("Erro ao registrar ofertório no banco de dados: " + e.getMessage());
        }
    }

    // Registrar doação sem identificar o doador
    public void registrarDoacao(double valor, String observacao) {
        this.doacao += valor;
        this.saldo += valor; // Atualiza o saldo
        registrarLog("Doação", "Não Identificado", valor);

        try {
            igrejaDAO.addTransacaoFinanceira("doacao", valor, null, "Não Identificado", observacao); // Sem CPF
        } catch (SQLException e) {
            System.err.println("Erro ao registrar doação no banco de dados: " + e.getMessage());
        }
    }

    // Registrar retirada de dinheiro com detalhes
    public void registrarRetirada(double valor, String cpfRetirante, String nomeRetirante, String observacao) throws SQLException {
        carregarSaldo(); // Atualiza o saldo antes de realizar a retirada
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a retirada.");
        }
        this.saldo -= valor; // Atualiza o saldo
        registrarLog("Retirada", nomeRetirante, valor);
        System.out.println("Retirada registrada: " + valor + " - Retirado por: " + nomeRetirante + " (CPF: " + cpfRetirante + ")");

        try {
            igrejaDAO.addTransacaoFinanceira("retirada", valor, cpfRetirante, nomeRetirante, observacao); // Agora passando o CPF do retirante
        } catch (SQLException e) {
            System.err.println("Erro ao registrar retirada no banco de dados: " + e.getMessage());
        }
    }

    // Método para obter a data e hora atuais no formato desejado
    private String obterDataHoraAtual() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy / HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    // Método para visualizar o log de transações
    public List<Log> getLogTransacoes() {
        return logTransacoes;
    }

    public double getDizimo() {
        return dizimo;
    }

    public double getOfertorio() {
        return ofertorio;
    }

    public double getDoacao() {
        return doacao;
    }

    public double getTotal() {
        return dizimo + ofertorio + doacao;
    }

    public double getSaldo() {
        return saldo; // Método para obter o saldo atual
    }

    // Método para registrar log de transações
    private void registrarLog(String tipo, String pessoa, double valor) {
        logTransacoes.add(new Log(tipo, pessoa, valor, LocalDateTime.now()));
    }

    public BigDecimal obterSaldo() throws SQLException {
        BigDecimal saldo = BigDecimal.ZERO; // Inicializa o saldo como zero

        String sql = "SELECT saldo FROM financeiro WHERE id = 1"; // Ajuste a consulta se necessário

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                saldo = rs.getBigDecimal("saldo"); // Obtém o saldo da tabela
            }
        }

        return saldo; // Retorna o saldo
    }
}
