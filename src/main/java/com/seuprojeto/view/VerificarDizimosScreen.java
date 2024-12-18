package com.seuprojeto.view;

import com.seuprojeto.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class VerificarDizimosScreen extends JFrame {

    private JTable dizimosTable;
    private DefaultTableModel tableModel;

    public VerificarDizimosScreen() {
        setTitle("Dízimos Pagos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurando o layout da tabela
        String[] columnNames = {"CPF", "Nome", "Valor do Dízimo", "Data"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede que qualquer célula seja editável
            }
        };
        dizimosTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(dizimosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Preencher a tabela com os dízimos pagos
        carregarDizimosPagos();
    }

    private void carregarDizimosPagos() {
        Connection connection = DatabaseConnection.getConnection(); // Obtenha a conexão ao banco de dados
        if (connection != null) {
            try {
                String sql = "SELECT m.cpf, m.nome, t.valor, t.data_transacao "
                        + "FROM membros m INNER JOIN transacoes_financeiras t "
                        + "ON m.cpf = t.cpf_doador "
                        + "WHERE t.tipo_transacao = 'dizimo'"; // Ajuste conforme a estrutura do seu banco

                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                // Limpar tabela antes de popular
                tableModel.setRowCount(0);

                // Adicionar dados à tabela
                while (resultSet.next()) {
                    String cpf = resultSet.getString("cpf");
                    String nome = resultSet.getString("nome");
                    double valor = resultSet.getDouble("valor");
                    String data = resultSet.getString("data_transacao");

                    // Adicionando a linha à tabela
                    tableModel.addRow(new Object[]{cpf, nome, valor, data});
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar dízimos: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
