package com.seuprojeto.main;

import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.database.IgrejaDAO; // Importa a classe IgrejaDAO
import com.seuprojeto.database.DatabaseConnection; // Importa a classe de conexão
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection; // Importa a classe Connection
import java.sql.PreparedStatement; // Para executar a consulta
import java.sql.ResultSet; // Para obter os resultados da consulta
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("Tela de Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Faça seu Login"));

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

        JButton loginButton = new JButton("Entrar");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        formPanel.add(new JLabel()); // Placeholder para manter o layout
        formPanel.add(loginButton);

        // Ajustar a aparência do botão "Cadastrar"
        JButton registerButton = new JButton("Cadastrar");
        registerButton.setContentAreaFilled(false); // Torna o fundo transparente
        registerButton.setBorderPainted(false); // Remove a borda
        registerButton.setForeground(Color.BLUE); // Cor do texto
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Fonte menor
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão para indicar que é clicável

        // Adiciona o botão "Cadastrar" logo abaixo da caixa de senha
        formPanel.add(new JLabel()); // Placeholder para manter o layout
        formPanel.add(registerButton);

        panel.add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                openRegisterScreen(); // Chama o método para abrir a tela de registro
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        // Definindo um tema para a interface
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Verifica as credenciais no banco de dados
        if (verifyCredentials(username, password)) {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");

                // Armazenar o nome do usuário logado na sessão
                UserSession.setLoggedInUser(username); // Use UserSession para armazenar o usuário logado
                //storeUserSession(username); // Armazenar no banco de dados se necessário

                // Abre a tela principal
                SwingUtilities.invokeLater(() -> {
                    IgrejaDAO igrejaDAO = new IgrejaDAO(connection); // Cria a instância de IgrejaDAO
                    Igreja igreja = null; // Inicialize a variável aqui
                    try {
                        igreja = new Igreja(igrejaDAO); // Cria a instância da Igreja com IgrejaDAO
                    } catch (SQLException ex) {
                        Logger.getLogger(LoginScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Certifique-se de que 'igreja' não é nulo antes de usá-la
                    if (igreja != null) {
                        MainScreen mainScreen = null;
                        try {
                            mainScreen = new MainScreen(igreja, username); // Passa o nome do usuário
                        } catch (UnsupportedLookAndFeelException ex) {
                            Logger.getLogger(LoginScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        mainScreen.setVisible(true);
                    }
                });
                dispose(); // Fecha a tela de login
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verifyCredentials(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Retorna true se o usuário foi encontrado
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar credenciais: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void openRegisterScreen() {
        SwingUtilities.invokeLater(() -> new RegisterScreen().setVisible(true));
    }

    // Método para armazenar a sessão do usuário
    private void storeUserSession(String username) {
        String sql = "INSERT INTO user_sessions (login) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para limpar a sessão do usuário
    private void clearUserSession() {
        String sql = "DELETE FROM user_sessions"; // Remove todas as sessões
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
