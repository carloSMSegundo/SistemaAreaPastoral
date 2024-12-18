package com.seuprojeto.main;

import com.formdev.flatlaf.FlatLightLaf; // Altere aqui para usar o tema claro
import com.seuprojeto.view.CadastroMembroScreen;
import com.seuprojeto.view.GerenciarGruposScreen;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.View.CadastrarEventoScreen;
import com.seuprojeto.View.GerenciarEventosScreen;
import com.seuprojeto.database.DatabaseConnection; // Importando a classe DatabaseConnection
import com.seuprojeto.database.IgrejaDAO;
import com.seuprojeto.view.ListarGruposScreen;
import com.seuprojeto.view.ListarMembrosScreen;
import com.seuprojeto.view.PainelFinanceiroScreen;
import com.seuprojeto.view.VerificarDizimosScreen;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScreen extends JFrame {

    private Igreja igreja;
    private Image backgroundImage;
    private Connection connection; // Declare a conexão como um campo da classe
    private IgrejaDAO igrejaDAO; // Declare o IgrejaDAO como um campo da classe
    private ListarMembrosScreen listarMembrosScreen;

    public MainScreen(Igreja igreja, String username) throws UnsupportedLookAndFeelException {
        this.igreja = igreja;

        // Inicializar a conexão
        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // Altere aqui para usar o tema claro
            backgroundImage = ImageIO.read(getClass().getResource("/images/fundo.jpg"));

            // Obtendo a conexão
            connection = DatabaseConnection.getConnection(); // Altere aqui se necessário
            igrejaDAO = new IgrejaDAO(connection); // Inicializa o IgrejaDAO aqui
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Sistema da Igreja");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
        menuBar.setBackground(new Color(255, 255, 255)); // Altere para uma cor clara
        menuBar.setOpaque(true);

        JMenu cadastroMenu = new JMenu("Cadastro");
        cadastroMenu.setForeground(Color.BLACK); // Mudar a cor do texto para preto
        cadastroMenu.setBackground(new Color(255, 255, 255)); // Cor de fundo clara
        cadastroMenu.setOpaque(true);

        JMenuItem cadastrarMembroItem = new JMenuItem("Cadastrar Membro");
        JMenuItem cadastrarGrupoItem = new JMenuItem("Cadastrar Grupo");
        JMenuItem cadastrarEventoItem = new JMenuItem("Cadastrar Evento");

        cadastroMenu.add(cadastrarMembroItem);
        cadastroMenu.add(cadastrarGrupoItem);
        cadastroMenu.add(cadastrarEventoItem);

        JMenu gerenciarMenu = new JMenu("Gerenciar");
        gerenciarMenu.setForeground(Color.BLACK); // Mudar a cor do texto para preto
        gerenciarMenu.setBackground(new Color(255, 255, 255)); // Cor de fundo clara
        gerenciarMenu.setOpaque(true);

        JMenuItem listarMembrosItem = new JMenuItem("Gerenciar Membros");
        JMenuItem listarGruposItem = new JMenuItem("Gerenciar Grupos");
        JMenuItem gerenciarEventosItem = new JMenuItem("Gerenciar Eventos");

        gerenciarMenu.add(listarMembrosItem);
        gerenciarMenu.add(listarGruposItem);
        gerenciarMenu.add(gerenciarEventosItem);

        JMenu financeiroMenu = new JMenu("Financeiro");
        financeiroMenu.setForeground(Color.BLACK); // Mudar a cor do texto para preto
        financeiroMenu.setBackground(new Color(255, 255, 255)); // Cor de fundo clara
        financeiroMenu.setOpaque(true);

        JMenuItem painelFinanceiroItem = new JMenuItem("Abrir Painel Financeiro");
        financeiroMenu.add(painelFinanceiroItem);

        // Criação da opção "Verificar Dízimos Pagos"
        JMenuItem verificarDizimosItem = new JMenuItem("Verificar Dízimos Pagos");
        verificarDizimosItem.addActionListener(e -> {
            // Abrir a tela de listagem de dízimos pagos
            VerificarDizimosScreen verificarDizimosScreen = new VerificarDizimosScreen();
            verificarDizimosScreen.setVisible(true);
        });

        financeiroMenu.add(verificarDizimosItem);
        menuBar.add(financeiroMenu);

        // Label de boas-vindas
        JLabel welcomeLabel = new JLabel("Bem vindo, " + username + " ");
        welcomeLabel.setForeground(Color.BLACK);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.BLACK); // Define a cor do texto do botão
        logoutButton.setBackground(new Color(255, 255, 255)); // Define a cor de fundo do botão
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false); // Remove bordas do botão para uma aparência mais limpa

        menuBar.add(cadastroMenu);
        menuBar.add(gerenciarMenu);
        menuBar.add(financeiroMenu);
        menuBar.add(Box.createHorizontalGlue()); // Para empurrar o botão de logout para a direita
        menuBar.add(welcomeLabel);
        menuBar.add(logoutButton);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);
        backgroundPanel.add(menuBar, BorderLayout.NORTH);

        // Ação para cadastrar membro
        cadastrarMembroItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadastroMembroScreen(igreja, listarMembrosScreen); // Passa a referência da ListarMembrosScreen
            }
        });

        // Ação para cadastrar grupo
        cadastrarGrupoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GerenciarGruposScreen(igreja);
            }
        });

        // Ação para cadastrar evento
        cadastrarEventoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadastrarEventoScreen(igrejaDAO); // Passa o IgrejaDAO para o construtor
            }
        });

        // Ação para listar membros
        listarMembrosItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListarMembrosScreen(igreja);
            }
        });

        // Ação para listar grupos
        listarGruposItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListarGruposScreen listarGruposScreen = new ListarGruposScreen(igreja);
                listarGruposScreen.setVisible(true); // Agora abre como um diálogo modal
            }
        });

        // Ação para gerenciar eventos
        gerenciarEventosItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GerenciarEventosScreen(igreja, igrejaDAO); // Passe ambos, igreja e igrejaDAO
            }
        });

        // Ação para visualizar painel financeiro
        painelFinanceiroItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new PainelFinanceiroScreen(igreja.getPainelFinanceiro());
                } catch (SQLException ex) {
                    Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Ação para o botão de logout
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "Confirmar Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Limpe o estado da aplicação se necessário, como:
                    dispose(); // Fecha a tela atual
                    new LoginScreen().setVisible(true); // Abre a tela de login
                }
            }
        });

        setVisible(true);
    }

    // Método para fechar a conexão (se necessário)
    @Override
    public void dispose() {
        super.dispose();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
