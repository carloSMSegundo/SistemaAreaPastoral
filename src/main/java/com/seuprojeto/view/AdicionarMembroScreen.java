package com.seuprojeto.view;

import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;
import com.seuprojeto.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class AdicionarMembroScreen extends JFrame {

    private Grupo grupo;
    private Igreja igreja; // Para acessar todos os membros cadastrados
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField; // Campo de busca

    public AdicionarMembroScreen(Grupo grupo, Igreja igreja) {
        this.grupo = grupo;
        this.igreja = igreja;
        initUI();
    }

    private void initUI() {
        setTitle("Adicionar Membros ao Grupo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Barra de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterMembers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterMembers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterMembers();
            }
        });
        searchPanel.add(new JLabel("Buscar Membro: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Tabela para exibir os membros cadastrados
        String[] columnNames = {"CPF", "Nome"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permitir seleção múltipla
        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão para adicionar membros selecionados ao grupo
        JButton addButton = new JButton("Adicionar Selecionados");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = memberTable.getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int row : selectedRows) {
                        String cpf = (String) tableModel.getValueAt(row, 0);
                        Membro membro = igreja.getMembro(cpf);
                        if (membro != null) {
                            addMembroToDatabase(membro);
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Membros adicionados ao grupo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione pelo menos um membro para adicionar.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Painel inferior com o botão de adicionar
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Preencher a tabela com os membros cadastrados
        updateMemberList();

        add(panel);
        setVisible(true);
    }

    private void updateMemberList() {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar os membros
        for (Map.Entry<String, Membro> entry : igreja.getMembros().entrySet()) {
            Membro membro = entry.getValue();
            tableModel.addRow(new Object[]{
                membro.getCpf(),
                membro.getNome()
            });
        }
    }

    private void filterMembers() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0); // Limpa a tabela para exibir apenas os resultados da busca
        for (Map.Entry<String, Membro> entry : igreja.getMembros().entrySet()) {
            Membro membro = entry.getValue();
            if (membro.getCpf().toLowerCase().contains(searchText) || membro.getNome().toLowerCase().contains(searchText)) {
                tableModel.addRow(new Object[]{
                    membro.getCpf(),
                    membro.getNome()
                });
            }
        }
    }

    private void addMembroToDatabase(Membro membro) {
        String sql = "INSERT INTO grupo_membros (grupo_id, membro_cpf, nome, data_adicao) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, grupo.getId()); // Assuming Grupo has a method getId()
            pstmt.setString(2, membro.getCpf());
            pstmt.setString(3, membro.getNome());

            pstmt.executeUpdate(); // Executa a inserção
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar membro ao banco de dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
