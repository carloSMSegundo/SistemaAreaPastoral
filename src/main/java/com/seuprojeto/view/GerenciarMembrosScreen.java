package com.seuprojeto.view;

import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GerenciarMembrosScreen extends JFrame {

    private Igreja igreja;
    private Grupo grupo;
    private JTextField nomeMembroTextField;
    private JTextField cpfMembroTextField;
    private JTextField enderecoMembroTextField;
    private JTextField telefoneMembroTextField;
    private JComboBox<String> sexoComboBox;
    private JTextField cargoMembroTextField;

    public GerenciarMembrosScreen(Igreja igreja, Grupo grupo) {
        this.igreja = igreja;
        this.grupo = grupo;
        initUI();
    }

    private void initUI() {
        setTitle("Gerenciar Membros - Grupo: " + grupo.getNome());
        setSize(400, 400); // Ajusta o tamanho da tela para acomodar os novos campos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10)); // 7 linhas agora para acomodar todos os campos
        add(panel);

        // Nome
        JLabel nomeMembroLabel = new JLabel("Nome do Membro:");
        panel.add(nomeMembroLabel);
        nomeMembroTextField = new JTextField();
        panel.add(nomeMembroTextField);

        // CPF
        JLabel cpfMembroLabel = new JLabel("CPF do Membro:");
        panel.add(cpfMembroLabel);
        cpfMembroTextField = new JTextField();
        panel.add(cpfMembroTextField);

        // Endereço
        JLabel enderecoMembroLabel = new JLabel("Endereço do Membro:");
        panel.add(enderecoMembroLabel);
        enderecoMembroTextField = new JTextField();
        panel.add(enderecoMembroTextField);

        // Telefone
        JLabel telefoneMembroLabel = new JLabel("Telefone do Membro:");
        panel.add(telefoneMembroLabel);
        telefoneMembroTextField = new JTextField();
        panel.add(telefoneMembroTextField);

        // Sexo
        JLabel sexoLabel = new JLabel("Sexo do Membro:");
        panel.add(sexoLabel);
        sexoComboBox = new JComboBox<>(new String[]{"Masculino", "Feminino", "Outro"});
        panel.add(sexoComboBox);

        // Cargo
        JLabel cargoMembroLabel = new JLabel("Cargo do Membro:");
        panel.add(cargoMembroLabel);
        cargoMembroTextField = new JTextField();
        panel.add(cargoMembroTextField);

        // Botão Adicionar Membro
        JButton adicionarMembroButton = new JButton("Adicionar Membro");
        panel.add(adicionarMembroButton);

        adicionarMembroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomeMembroTextField.getText().trim();
                String cpf = cpfMembroTextField.getText().trim();
                String endereco = enderecoMembroTextField.getText().trim();
                String telefone = telefoneMembroTextField.getText().trim();
                String sexo = sexoComboBox.getSelectedItem().toString();
                String cargo = cargoMembroTextField.getText().trim();

                if (nome.isEmpty() || cpf.isEmpty() || endereco.isEmpty() || telefone.isEmpty() || sexo.isEmpty() || cargo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Adiciona o membro ao grupo
                try {
                    Membro novoMembro = new Membro(cpf, nome, endereco, telefone, sexo, cargo); // Usando o construtor completo
                    grupo.adicionarMembro(novoMembro);
                    JOptionPane.showMessageDialog(null, "Membro adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // Limpa os campos
                    nomeMembroTextField.setText("");
                    cpfMembroTextField.setText("");
                    enderecoMembroTextField.setText("");
                    telefoneMembroTextField.setText("");
                    cargoMembroTextField.setText("");
                    sexoComboBox.setSelectedIndex(0); // Redefine a seleção para "Masculino"
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao adicionar membro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
