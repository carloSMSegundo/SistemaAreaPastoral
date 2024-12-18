package com.seuprojeto.view;

import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro; // Supondo que você tenha uma classe Membro

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class EditarGrupoScreen extends JFrame {

    private Igreja igreja;
    private Grupo grupo; // Grupo a ser editado

    public EditarGrupoScreen(Igreja igreja, Grupo grupo) {
        this.igreja = igreja;
        this.grupo = grupo;
        initUI();
    }

    private void initUI() {
        setTitle("Editar Grupo");
        setSize(450, 400); // Aumentando a altura da tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Verifica se o grupo é nulo
        if (grupo == null) {
            JOptionPane.showMessageDialog(null, "Grupo não pode ser nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
            return; // Encerra o método se o grupo for nulo
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(230, 240, 255)); // Cor de fundo mais suave
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(panel);

        // Título estilizado
        JLabel titulo = new JLabel("Edição de Grupo", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(50, 50, 150)); // Cor do título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        // Label e campo de texto para Nome do Grupo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Nome do Grupo:"), gbc);

        JTextField nomeGrupoTextField = new JTextField(20);
        nomeGrupoTextField.setText(grupo.getNome()); // Preenche com o nome atual do grupo
        nomeGrupoTextField.setEditable(false); // Torna o campo não editável
        gbc.gridx = 1;
        panel.add(nomeGrupoTextField, gbc);

        // Label e campo de texto para Responsável
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Responsável:"), gbc);

        JTextField responsavelTextField = new JTextField(20);
        responsavelTextField.setText(grupo.getResponsavel()); // Preenche com o responsável atual
        gbc.gridx = 1;
        panel.add(responsavelTextField, gbc);

        // Lista de membros do grupo
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        DefaultListModel<Membro> listModel = new DefaultListModel<>();
        JList<Membro> membrosList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(membrosList);
        scrollPane.setPreferredSize(new Dimension(400, 100)); // Tamanho do painel
        panel.add(scrollPane, gbc);

        // Adicionando os membros do grupo à lista
        List<Membro> membros = grupo.getMembros(); // Supondo que você tenha um método para obter os membros do grupo
        for (Membro membro : membros) {
            listModel.addElement(membro); // Adiciona membros à lista
        }

        // Botão para remover membros
        JButton removerButton = new JButton("Remover Membro");
        gbc.gridy = 4;
        panel.add(removerButton, gbc);

        // Ação do botão remover
        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Membro membroSelecionado = membrosList.getSelectedValue();
                if (membroSelecionado != null) {
                    listModel.removeElement(membroSelecionado); // Remove da lista
                    grupo.removerMembro(membroSelecionado); // Chama método para remover do grupo
                    try {
                        igreja.atualizarGrupo(grupo); // Atualiza o grupo no banco de dados
                        JOptionPane.showMessageDialog(null, "Membro removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao atualizar grupo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhum membro selecionado para remoção.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        JButton salvarButton = new JButton("Salvar Alterações");

        // Estilizando o botão de salvar
        salvarButton.setFont(new Font("Arial", Font.BOLD, 14));
        salvarButton.setBackground(new Color(0, 150, 0));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(salvarButton);
        gbc.gridy = 5;
        panel.add(buttonPanel, gbc);

        // Ação do botão salvar
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String responsavel = responsavelTextField.getText().trim();

                if (responsavel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Atualiza o grupo com os novos dados
                grupo.setResponsavel(responsavel);

                // Tente atualizar o grupo no banco de dados
                try {
                    igreja.atualizarGrupo(grupo); // Chama o método de atualização
                    JOptionPane.showMessageDialog(null, "Grupo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Fecha a tela após salvar
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar grupo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
