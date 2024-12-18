package com.seuprojeto.view;

import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class PerfilMembroScreen extends JFrame {

    private Igreja igreja;
    private Membro membro;
    private ListarMembrosScreen listarMembrosScreen; // Adiciona a referência da tela de listagem

    public PerfilMembroScreen(Igreja igreja, Membro membro, ListarMembrosScreen listarMembrosScreen) {
        this.igreja = igreja;
        this.membro = membro;
        this.listarMembrosScreen = listarMembrosScreen; // Inicializa a referência da tela de listagem
        initUI();
    }

    private void initUI() {
        setTitle("Perfil do Membro");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(panel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("CPF:"), gbc);
        JTextField cpfTextField = new JTextField(20);
        cpfTextField.setText(membro.getCpf());
        cpfTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(cpfTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nome:"), gbc);
        JTextField nomeTextField = new JTextField(20);
        nomeTextField.setText(membro.getNome());
        nomeTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(nomeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Endereço:"), gbc);
        JTextField enderecoTextField = new JTextField(20);
        enderecoTextField.setText(membro.getEndereco());
        enderecoTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(enderecoTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Telefone:"), gbc);
        JTextField telefoneTextField = new JTextField(20);
        telefoneTextField.setText(membro.getTelefone());
        telefoneTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(telefoneTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Sexo:"), gbc);
        JTextField sexoTextField = new JTextField(20);
        sexoTextField.setText(membro.getSexo());
        sexoTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(sexoTextField, gbc);

        // Novo campo para Cargo
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Cargo:"), gbc);
        JTextField cargoTextField = new JTextField(20);
        cargoTextField.setText(membro.getCargo()); // Adiciona o campo de cargo
        cargoTextField.setEditable(false);
        gbc.gridx = 1;
        panel.add(cargoTextField, gbc);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        JButton editarButton = new JButton("Editar");
        buttonPanel.add(editarButton);

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre a tela de cadastro, passando o membro atual para edição
                new CadastroMembroScreen(igreja, listarMembrosScreen, membro.getCpf(), 0.0, true);
                dispose(); // Fecha a tela de perfil
            }
        });

        JButton removerButton = new JButton("Remover Membro");
        buttonPanel.add(removerButton);

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja remover este membro?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        igreja.removerMembro(membro.getCpf()); // Chama o método que remove o membro
                        JOptionPane.showMessageDialog(null, "Membro removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        listarMembrosScreen.updateMemberList(""); // Atualiza a lista na tela de listagem
                        dispose(); // Fecha a janela após a remoção
                    } catch (SQLException ex) {
                        // Exibe uma mensagem de erro se houver um problema no banco de dados
                        JOptionPane.showMessageDialog(null, "Erro ao remover o membro do banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        // Exibe uma mensagem de erro se o membro não for encontrado
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(true);
    }
}
