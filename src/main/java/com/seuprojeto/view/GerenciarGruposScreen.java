package com.seuprojeto.view;

import com.seuprojeto.Dados.Igreja;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GerenciarGruposScreen extends JFrame {

    private Igreja igreja;

    public GerenciarGruposScreen(Igreja igreja) {
        this.igreja = igreja;
        initUI();
    }

    private void initUI() {
        setTitle("Cadastrar Grupos");
        setSize(450, 300); // Tamanho da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal com layout de GridBag
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240)); // Cor de fundo neutra
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(panel);

        // Título estilizado
        JLabel titulo = new JLabel("Cadastro de Grupos", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20)); // Tamanho da fonte ajustado
        titulo.setForeground(new Color(30, 60, 100)); // Cor do título
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
        nomeGrupoTextField.setFont(new Font("Arial", Font.PLAIN, 14)); // Ajuste na fonte
        gbc.gridx = 1;
        panel.add(nomeGrupoTextField, gbc);

        // Label e campo de texto para Responsável
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Responsável:"), gbc);

        JTextField responsavelTextField = new JTextField(20);
        responsavelTextField.setFont(new Font("Arial", Font.PLAIN, 14)); // Ajuste na fonte
        gbc.gridx = 1;
        panel.add(responsavelTextField, gbc);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        JButton cadastrarButton = new JButton("Cadastrar");

        // Estilizando o botão de cadastrar
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 16)); // Ajuste no tamanho da fonte
        cadastrarButton.setBackground(new Color(0, 150, 0)); // Cor de fundo do botão
        cadastrarButton.setForeground(Color.WHITE); // Cor do texto
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cadastrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Muda o cursor ao passar por cima

        buttonPanel.add(cadastrarButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        // Ação do botão cadastrar
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeGrupo = nomeGrupoTextField.getText().trim();
                String responsavel = responsavelTextField.getText().trim();

                if (nomeGrupo.isEmpty() || responsavel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    igreja.criarGrupo(nomeGrupo, responsavel);
                    JOptionPane.showMessageDialog(null, "Grupo cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // Limpa os campos após o cadastro
                    nomeGrupoTextField.setText("");
                    responsavelTextField.setText("");

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
