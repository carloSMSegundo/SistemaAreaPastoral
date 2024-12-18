package com.seuprojeto.main;

import com.seuprojeto.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;

public class RegisterScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterScreen() {
        setTitle("Tela de Registro");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Crie sua Conta"));

        JLabel usernameLabel = new JLabel("Usuário:");
        usernameLabel.setHorizontalAlignment(JLabel.RIGHT);
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        formPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setHorizontalAlignment(JLabel.RIGHT);
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Botão de cadastro
        JButton registerButton = new JButton("Cadastrar");
        registerButton.setBackground(new Color(0, 123, 255));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));

        formPanel.add(new JLabel()); // Placeholder
        formPanel.add(registerButton);

        // Ajustar a aparência do botão "Voltar ao Login"
        JButton backToLoginButton = new JButton("Voltar ao Login");
        backToLoginButton.setContentAreaFilled(false); // Torna o fundo transparente
        backToLoginButton.setBorderPainted(false); // Remove a borda
        backToLoginButton.setForeground(Color.BLUE); // Cor do texto
        backToLoginButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Fonte menor
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão para indicar que é clicável

        // Adiciona o botão "Voltar ao Login" logo abaixo do botão de registro
        formPanel.add(new JLabel()); // Placeholder
        formPanel.add(backToLoginButton);

        panel.add(formPanel, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a tela de registro
                new LoginScreen().setVisible(true); // Abre a tela de login
            }
        });
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insere o novo usuário no banco de dados
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (login, senha) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose(); // Fecha a tela de registro
            new LoginScreen().setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterScreen().setVisible(true));
    }
}
