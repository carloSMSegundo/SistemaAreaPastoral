package com.seuprojeto.view;

import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.Dados.Membro;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class CadastroMembroScreen extends JFrame {

    private Igreja igreja;
    private String cpf;
    private double valor;
    private ListarMembrosScreen listarMembrosScreen;
    private JTextField cpfTextField;
    private JTextField nomeTextField;
    private JTextField enderecoTextField;
    private JTextField telefoneTextField;
    private JRadioButton masculinoRadioButton;
    private JRadioButton femininoRadioButton;
    private JComboBox<String> cargoComboBox;
    private boolean isEditMode; // Nova variável para verificar o modo de edição

// Construtor principal
    public CadastroMembroScreen(Igreja igreja, ListarMembrosScreen listarMembrosScreen) {
        this(igreja, listarMembrosScreen, "", 0.0, false); // Modo de cadastro
    }

    public CadastroMembroScreen(Igreja igreja, ListarMembrosScreen listarMembrosScreen, String cpf, double valor) {
        this(igreja, listarMembrosScreen, cpf, valor, true); // Modo de edição
    }

    public CadastroMembroScreen(Igreja igreja, ListarMembrosScreen listarMembrosScreen, String cpf, double valor, boolean isEditMode) {
        this.igreja = igreja;
        this.listarMembrosScreen = listarMembrosScreen; // Armazena a referência da ListarMembrosScreen
        this.cpf = cpf;
        this.valor = valor;
        this.isEditMode = isEditMode; // Define se está em modo de edição
        initUI();
        if (!cpf.isEmpty()) {
            preFillFields();  // Preenche os campos apenas se o CPF não estiver vazio
        }
    }

    private void initUI() {
        setTitle("Cadastrar Membro");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(panel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("CPF:"), gbc);
        cpfTextField = new JTextField(20);
        ((AbstractDocument) cpfTextField.getDocument()).setDocumentFilter(new NumericDocumentFilter(11)); // Limitando o CPF para 11 caracteres
        gbc.gridx = 1;
        panel.add(cpfTextField, gbc);
        cpfTextField.setEditable(!isEditMode); // Define se o campo é editável

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nome:"), gbc);
        nomeTextField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nomeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Endereço:"), gbc);
        enderecoTextField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(enderecoTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Telefone:"), gbc);
        telefoneTextField = new JTextField(20);
        ((AbstractDocument) telefoneTextField.getDocument()).setDocumentFilter(new NumericDocumentFilter(11)); // Limitando o Telefone para 11 caracteres
        gbc.gridx = 1;
        panel.add(telefoneTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Sexo:"), gbc);
        masculinoRadioButton = new JRadioButton("Masculino");
        femininoRadioButton = new JRadioButton("Feminino");
        ButtonGroup sexoGroup = new ButtonGroup();
        sexoGroup.add(masculinoRadioButton);
        sexoGroup.add(femininoRadioButton);
        gbc.gridx = 1;
        panel.add(masculinoRadioButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(femininoRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Cargo:"), gbc);
        String[] cargos = {"Fiel", "Coral", "Coroinha", "Dizimista", "Acolhida", "Liturgia", "Padre", "Pascom", "Seminarista"};
        cargoComboBox = new JComboBox<>(cargos);
        gbc.gridx = 1;
        panel.add(cargoComboBox, gbc);

        JButton cadastrarButton = new JButton(isEditMode ? "Atualizar" : "Cadastrar");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 14));
        cadastrarButton.setBackground(new Color(0, 123, 255));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(cadastrarButton, gbc);

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cpf = cpfTextField.getText().trim();
                String nome = nomeTextField.getText().trim();
                String endereco = enderecoTextField.getText().trim();
                String telefone = telefoneTextField.getText().trim();
                String cargo = (String) cargoComboBox.getSelectedItem();

                String sexo = masculinoRadioButton.isSelected() ? "Masculino" : femininoRadioButton.isSelected() ? "Feminino" : "";

                if (cpf.isEmpty() || nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty() || sexo.isEmpty() || cargo == null) {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cpf.length() != 11) {
                    JOptionPane.showMessageDialog(null, "CPF deve ter 11 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (telefone.length() != 11) {
                    JOptionPane.showMessageDialog(null, "Telefone deve ter 11 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (!isEditMode) { // Se não for edição
                        igreja.cadastrarMembro(cpf, nome, endereco, telefone, sexo, cargo);
                        JOptionPane.showMessageDialog(null, "Membro cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else { // Se for edição
                        igreja.editarMembro(cpf, nome, endereco, telefone, sexo, cargo);
                        JOptionPane.showMessageDialog(null, "Membro atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                        // Atualiza a lista de membros
                        if (listarMembrosScreen != null) {
                            listarMembrosScreen.refreshMemberList();
                        }
                    }

                    if (valor > 0) {
                        String observacao = "Observação sobre o dízimo";
                        String nomeDoador = igreja.obterNomePorCpf(cpf);

                        if (nomeDoador != null) {
                            igreja.registrarDizimo(valor, cpf, nomeDoador, observacao);
                            JOptionPane.showMessageDialog(null, "Dízimo registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Membro não encontrado. Cadastre o membro antes de registrar o dízimo.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    Logger.getLogger(CadastroMembroScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        setVisible(true);
    }

    private void preFillFields() {
        Membro membro = igreja.getMembro(cpf);
        if (membro != null) {
            cpfTextField.setText(membro.getCpf()); // Preencher CPF aqui
            nomeTextField.setText(membro.getNome());
            enderecoTextField.setText(membro.getEndereco());
            telefoneTextField.setText(membro.getTelefone());
            if (membro.getSexo().equals("Masculino")) {
                masculinoRadioButton.setSelected(true);
            } else if (membro.getSexo().equals("Feminino")) {
                femininoRadioButton.setSelected(true);
            }
            cargoComboBox.setSelectedItem(membro.getCargo());
        }
    }

    private class NumericDocumentFilter extends DocumentFilter {

        private int maxLength;

        public NumericDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isNumeric(string) && (fb.getDocument().getLength() + string.length() <= maxLength)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isNumeric(text) && (fb.getDocument().getLength() - length + text.length() <= maxLength)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }

        private boolean isNumeric(String str) {
            return str.matches("\\d*");
        }
    }
}
