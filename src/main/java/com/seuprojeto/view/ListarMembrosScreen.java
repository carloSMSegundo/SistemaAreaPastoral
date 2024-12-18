package com.seuprojeto.view;

import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListarMembrosScreen extends JFrame {

    private Igreja igreja;
    private DefaultTableModel tableModel;
    private JTable memberTable;
    private JTextField searchField;
    private JButton btnRemover; // Botão para remover membro

    public ListarMembrosScreen(Igreja igreja) {
        this.igreja = igreja;
        initUI();
    }

    private void initUI() {
        setTitle("Gerenciar Membros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Campo de busca
        searchField = new JTextField();
        searchField.setToolTipText("Buscar membro pelo nome ou CPF");
        panel.add(searchField, BorderLayout.NORTH);

        // Tabela para exibir os membros
        String[] columnNames = {"CPF", "Nome", "Endereço", "Telefone", "Sexo", "Cargo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede que qualquer célula seja editável
            }
        };
        memberTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão para remover membro
        btnRemover = new JButton("Remover Membro");
        panel.add(btnRemover, BorderLayout.SOUTH);
        btnRemover.setEnabled(false); // Inicia desabilitado

        add(panel);

        updateMemberList("");

        // Listener para a barra de busca
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMemberList(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMemberList(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMemberList(searchField.getText());
            }
        });

// Listener para clique duplo na tabela
        memberTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = memberTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String cpf = (String) tableModel.getValueAt(selectedRow, 0);
                        Membro membro = igreja.getMembros().get(cpf);
                        if (membro != null) {
                            // Passar o valor correto, se necessário
                            CadastroMembroScreen cadastroScreen = new CadastroMembroScreen(igreja, ListarMembrosScreen.this, cpf, 0.0, true);
                            cadastroScreen.setVisible(true); // Torna a tela visível
                        }
                    }
                }
            }
        });

        // Listener para remover o membro
        btnRemover.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow != -1) {
                String cpf = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Deseja realmente remover o membro? (Caso esteja em um grupo, será deletado de lá também)", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        igreja.removerMembro(cpf); // Remove o membro da igreja
                        updateMemberList(searchField.getText()); // Atualiza a lista
                    } catch (SQLException ex) {
                        Logger.getLogger(ListarMembrosScreen.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, "Erro ao remover membro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Habilitar o botão de remoção ao selecionar um membro
        memberTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    btnRemover.setEnabled(memberTable.getSelectedRow() != -1); // Habilita o botão se houver uma linha selecionada
                }
            }
        });

        setVisible(true);
    }

    void updateMemberList(String filter) {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar os membros
        for (Map.Entry<String, Membro> entry : igreja.getMembros().entrySet()) {
            String cpf = entry.getKey();
            Membro membro = entry.getValue();
            if (filter.isEmpty() || membro.getNome().toLowerCase().contains(filter.toLowerCase()) || cpf.contains(filter)) {
                tableModel.addRow(new Object[]{
                    cpf,
                    membro.getNome(),
                    membro.getEndereco(),
                    membro.getTelefone(),
                    membro.getSexo(),
                    membro.getCargo()
                });
            }
        }
    }

    public void refreshMemberList() {
        updateMemberList(searchField.getText()); // Atualiza a lista com o filtro atual
    }
}
