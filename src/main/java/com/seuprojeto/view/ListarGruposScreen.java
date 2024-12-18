package com.seuprojeto.view;

import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;
import com.seuprojeto.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ListarGruposScreen extends JFrame {

    private Igreja igreja;
    private DefaultTableModel tableModel;
    private JTable groupTable;

    public ListarGruposScreen(Igreja igreja) {
        this.igreja = igreja;
        initUI();
        
    }

    private void initUI() {
        setTitle("Gerenciar Grupos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Tabela para exibir os grupos
        String[] columnNames = {"Nome do Grupo", "Responsável"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // As células não são editáveis
            }
        };
        groupTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(groupTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão para adicionar membros
        JButton addMemberButton = new JButton("Adicionar Membros");
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = groupTable.getSelectedRow();
                if (selectedRow != -1) {
                    String nomeGrupo = (String) tableModel.getValueAt(selectedRow, 0);
                    Grupo grupo = igreja.getGrupo(nomeGrupo);
                    new AdicionarMembroScreen(grupo, igreja); // Abre a tela para adicionar membros ao grupo selecionado
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo para adicionar membros.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botão para editar grupo
        JButton editGroupButton = new JButton("Editar Grupo");
        editGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = groupTable.getSelectedRow();
                if (selectedRow != -1) {
                    String nomeGrupo = (String) tableModel.getValueAt(selectedRow, 0);
                    Grupo grupo = igreja.getGrupo(nomeGrupo);
                    new EditarGrupoScreen(igreja, grupo); // Abre a tela de edição para o grupo selecionado
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botão para remover grupos
        JButton removeGroupButton = new JButton("Remover Grupo");
        removeGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = groupTable.getSelectedRow();
                if (selectedRow != -1) {
                    String nomeGrupo = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Tem certeza que deseja remover o grupo " + nomeGrupo + "?",
                            "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            // Remove o grupo da igreja
                            igreja.removerGrupo(nomeGrupo);
                            updateGroupList(); // Atualiza a lista de grupos
                            JOptionPane.showMessageDialog(null, "Grupo removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        } catch (SQLException ex) {
                            Logger.getLogger(ListarGruposScreen.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Erro ao remover grupo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo para remover.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addMemberButton);
        bottomPanel.add(editGroupButton); // Adiciona o botão de editar
        bottomPanel.add(removeGroupButton); // Adiciona o botão de remover
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Obtém todos os grupos e os adiciona à tabela
        updateGroupList();

        add(panel);
        setVisible(true);
    }

    private void updateGroupList() {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar os grupos
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Map.Entry<String, Grupo> entry : igreja.getGrupos().entrySet()) {
                Grupo grupo = entry.getValue();

                // Carregar membros do banco de dados
                grupo.carregarMembros(conn);

                // Coletar os nomes dos membros
                String membros = grupo.getMembros().stream()
                        .map(Membro::getNome) // Assume que Membro tem um método getNome()
                        .collect(Collectors.joining(", ")); // Junta os nomes em uma string separada por vírgulas

                tableModel.addRow(new Object[]{
                    grupo.getNome(),
                    grupo.getResponsavel(),
                    membros // Adiciona os nomes dos membros à tabela
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar grupos: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
