package com.seuprojeto.view;

import com.seuprojeto.Dados.PainelFinanceiro;
import com.seuprojeto.Dados.Log;
import com.seuprojeto.Dados.Membro; // Adicione o import para a classe Membro
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.twilio.Twilio;
import static com.twilio.example.Example.ACCOUNT_SID;
import static com.twilio.example.Example.AUTH_TOKEN;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.math.BigDecimal;

public class PainelFinanceiroScreen extends JFrame {

    private JLabel saldoLabel;
    public static final String ACCOUNT_SID = "ACcd337fc7b54e9299d164b3b46faff448";
    public static final String AUTH_TOKEN = "bdecabf4860b470c4f7e8bc860b2db93";
    private PainelFinanceiro painelFinanceiro;
    private JComboBox<String> retiranteComboBox; // Declare o JComboBox como variável de classe

    public PainelFinanceiroScreen(PainelFinanceiro painelFinanceiro) throws SQLException {
        this.painelFinanceiro = painelFinanceiro;

        setTitle("Painel Financeiro");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Criação do JLabel para o saldo
        saldoLabel = new JLabel("Saldo: R$ 0,00");
        saldoLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Alinha o texto à direita
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Estilo do texto

        add(saldoLabel, BorderLayout.NORTH); // Colocando no topo

        JTabbedPane tabbedPane = new JTabbedPane();

        // Aba Dízimo
        JPanel dizimoPanel = new JPanel();
        dizimoPanel.setLayout(new GridBagLayout());
        dizimoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Membro
        gbc.gridx = 0;
        gbc.gridy = 0;
        dizimoPanel.add(new JLabel("Membro:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        JComboBox<String> membroComboBox = new JComboBox<>(getMembros());
        dizimoPanel.add(membroComboBox, gbc);

        // Valor
        gbc.gridx = 0;
        gbc.gridy = 1;
        dizimoPanel.add(new JLabel("Valor:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField valorDizimoTextField = new JTextField();
        dizimoPanel.add(valorDizimoTextField, gbc);

        // Tipo de Pagamento
        gbc.gridx = 0;
        gbc.gridy = 3;
        dizimoPanel.add(new JLabel("Tipo de Pagamento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        JComboBox<String> tipoPagamentoComboBox = new JComboBox<>(new String[]{"Dinheiro", "Transferência", "Cartão"});
        dizimoPanel.add(tipoPagamentoComboBox, gbc);

        // Observação
        gbc.gridx = 0;
        gbc.gridy = 4;
        dizimoPanel.add(new JLabel("Observação:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField observacaoDizimoTextField = new JTextField();
        dizimoPanel.add(observacaoDizimoTextField, gbc);

        // Botão de Registro
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registrarDizimoButton = new JButton("Registrar Dízimo");
        dizimoPanel.add(registrarDizimoButton, gbc);

        tabbedPane.addTab("Dízimo", dizimoPanel);

        // Aba Doação
        JPanel doacaoPanel = new JPanel();
        doacaoPanel.setLayout(new GridBagLayout());
        doacaoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcDoacao = new GridBagConstraints();
        gbcDoacao.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes
        gbcDoacao.fill = GridBagConstraints.HORIZONTAL;

// Valor
        gbcDoacao.gridx = 0;
        gbcDoacao.gridy = 0;
        doacaoPanel.add(new JLabel("Valor:"), gbcDoacao);
        gbcDoacao.gridx = 1;
        gbcDoacao.gridy = 0;
        JTextField valorDoacaoTextField = new JTextField();
        doacaoPanel.add(valorDoacaoTextField, gbcDoacao);

// Tipo de Pagamento
        gbcDoacao.gridx = 0;
        gbcDoacao.gridy = 2;
        doacaoPanel.add(new JLabel("Tipo de Pagamento:"), gbcDoacao);
        gbcDoacao.gridx = 1;
        gbcDoacao.gridy = 2;
        JComboBox<String> tipoPagamentoDoacaoComboBox = new JComboBox<>(new String[]{"Dinheiro", "Transferência", "Cartão"});
        doacaoPanel.add(tipoPagamentoDoacaoComboBox, gbcDoacao);

// Observação
        gbcDoacao.gridx = 0;
        gbcDoacao.gridy = 3;
        doacaoPanel.add(new JLabel("Observação:"), gbcDoacao);
        gbcDoacao.gridx = 1;
        gbcDoacao.gridy = 3;
        JTextField observacaoDoacaoTextField = new JTextField();
        doacaoPanel.add(observacaoDoacaoTextField, gbcDoacao);

// Botão de Registro
        gbcDoacao.gridx = 0;
        gbcDoacao.gridy = 4;
        gbcDoacao.gridwidth = 2;
        gbcDoacao.anchor = GridBagConstraints.CENTER;
        JButton registrarDoacaoButton = new JButton("Registrar Doação");
        doacaoPanel.add(registrarDoacaoButton, gbcDoacao);

        tabbedPane.addTab("Doação", doacaoPanel);

        // Aba Retirada
        JPanel retiradaPanel = new JPanel();
        retiradaPanel.setLayout(new GridBagLayout());
        retiradaPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcRetirada = new GridBagConstraints();
        gbcRetirada.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes
        gbcRetirada.fill = GridBagConstraints.HORIZONTAL;

        // Nome do Retirante
        gbcRetirada.gridx = 0;
        gbcRetirada.gridy = 0;
        retiradaPanel.add(new JLabel("Nome do Retirante:"), gbcRetirada);
        gbcRetirada.gridx = 1;
        gbcRetirada.gridy = 0;

        // Use o método getMembrosDizimistas para preencher o JComboBox
        retiranteComboBox = new JComboBox<>(getMembrosDizimistas());
        retiradaPanel.add(retiranteComboBox, gbcRetirada);

        // Valor
        gbcRetirada.gridx = 0;
        gbcRetirada.gridy = 1;
        retiradaPanel.add(new JLabel("Valor:"), gbcRetirada);
        gbcRetirada.gridx = 1;
        gbcRetirada.gridy = 1;
        JTextField valorRetiradaTextField = new JTextField();
        retiradaPanel.add(valorRetiradaTextField, gbcRetirada);

        // Observação
        gbcRetirada.gridx = 0;
        gbcRetirada.gridy = 2;
        retiradaPanel.add(new JLabel("Observação:"), gbcRetirada);
        gbcRetirada.gridx = 1;
        gbcRetirada.gridy = 2;
        JTextField observacaoRetiradaTextField = new JTextField();
        retiradaPanel.add(observacaoRetiradaTextField, gbcRetirada);

        // Botão de Registro
        gbcRetirada.gridx = 0;
        gbcRetirada.gridy = 3;
        gbcRetirada.gridwidth = 2;
        gbcRetirada.anchor = GridBagConstraints.CENTER;
        JButton registrarRetiradaButton = new JButton("Registrar Retirada");
        retiradaPanel.add(registrarRetiradaButton, gbcRetirada);

        tabbedPane.addTab("Retirada", retiradaPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Painel de Log
        //JButton visualizarLogButton = new JButton("Visualizar Log");
       // add(visualizarLogButton, BorderLayout.SOUTH);

        registrarDizimoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String membroNome = (String) membroComboBox.getSelectedItem();
                double valor;
                String tipoPagamento = (String) tipoPagamentoComboBox.getSelectedItem();
                String observacao = observacaoDizimoTextField.getText();

                try {
                    valor = Double.parseDouble(valorDizimoTextField.getText());

                    // Obter CPF do membro selecionado
                    String cpfDoador = getCpfByMembroName(membroNome);

                    // Verificar se o CPF do doador foi encontrado
                    if (cpfDoador == null) {
                        JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "CPF doador não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Registrar o dízimo e obter o valor com desconto
                    double valorComDesconto = painelFinanceiro.registrarDizimo(valor, cpfDoador, membroNome, observacao);
                    atualizarSaldo();

                    try {
                        // Enviar SMS
                        sendSms(cpfDoador, valorComDesconto, tipoPagamento, observacao);
                    } catch (ApiException smsException) {
                        // Captura a exceção de envio da SMS
                        System.out.println("Erro ao enviar mensagem: " + smsException.getMessage());
                        // Não interrompe a execução, apenas loga o erro
                    }

                    // Exibir a mensagem de sucesso com o valor final
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this,
                            "Dízimo registrado com sucesso! Valor final com desconto: R$" + valorComDesconto,
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    Logger.getLogger(PainelFinanceiroScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        registrarDoacaoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double valor;

                try {
                    valor = Double.parseDouble(valorDoacaoTextField.getText());
                    // Captura a observação do campo de texto
                    String observacao = observacaoDoacaoTextField.getText();

                    // Chamada corrigida
                    painelFinanceiro.registrarDoacao(valor, observacao); // Agora inclui a observação
                    atualizarSaldo();

                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Doação registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

// Ação do Botão
        registrarRetiradaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double valor;
                // Captura o nome do retirante a partir do JComboBox
                String nomeRetirante = (String) retiranteComboBox.getSelectedItem();
                // Captura a observação do campo de texto
                String observacao = observacaoRetiradaTextField.getText().trim();

                // Verifica se um retirante foi selecionado
                if (nomeRetirante == null || nomeRetirante.isEmpty()) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Selecione um retirante.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (observacao.isEmpty()) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "A observação não pode estar vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    valor = Double.parseDouble(valorRetiradaTextField.getText().trim());

                    // Obter CPF do retirante selecionado
                    String cpfRetirante = getCpfByMembroName(nomeRetirante);

                    // Verificar se o CPF do retirante foi encontrado
                    if (cpfRetirante == null) {
                        JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "CPF retirante não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Chamada corrigida
                    painelFinanceiro.registrarRetirada(valor, cpfRetirante, nomeRetirante, observacao); // Passando o CPF do retirante
                    atualizarSaldo();

                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Retirada registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(PainelFinanceiroScreen.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    Logger.getLogger(PainelFinanceiroScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        atualizarSaldo();
        setVisible(true);
    }

    // Método para buscar membros (pode ser adaptado para o que você já tem)
    // Método para buscar membros do banco de dados
    private String[] getMembros() throws SQLException {
        List<Membro> membrosList = painelFinanceiro.listMembers(); // Chame o método que busca os membros
        String[] membrosArray = new String[membrosList.size()];
        for (int i = 0; i < membrosList.size(); i++) {
            membrosArray[i] = membrosList.get(i).getNome(); // Assumindo que você tem um método getNome() na classe Membro
        }
        return membrosArray; // Retorna a lista de nomes
    }

    // Método para obter CPF pelo nome do membro
    private String getCpfByMembroName(String nome) throws SQLException {
        List<Membro> membrosList = painelFinanceiro.listMembers(); // Chama o método que busca os membros
        for (Membro membro : membrosList) {
            if (membro.getNome().equals(nome)) {
                return membro.getCpf(); // Assumindo que você tem um método getCpf() na classe Membro
            }
        }
        return null; // Retorna null se não encontrar
    }

    // Método para buscar membros com cargo de dizimista
    private String[] getMembrosDizimistas() throws SQLException {
        List<Membro> membrosList = painelFinanceiro.listMembers(); // Chame o método que busca os membros
        List<String> dizimistas = new ArrayList<>();

        for (Membro membro : membrosList) {
            // Verifique se o membro tem o cargo de dizimista
            if ("dizimista".equalsIgnoreCase(membro.getCargo())) { // Certifique-se de que você tem o método getCargo() na classe Membro
                dizimistas.add(membro.getNome()); // Adiciona o nome à lista de dizimistas
            }
        }

        // Converte a lista para um array e retorna
        return dizimistas.toArray(new String[0]);
    }

    // Método para atualizar o saldo
    private void atualizarSaldo() {
        try {
            BigDecimal saldo = painelFinanceiro.obterSaldo(); // Supondo que você tenha um método que retorna o saldo atual
            saldoLabel.setText(String.format("Saldo: R$ %.2f", saldo));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter saldo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendSms(String cpfDoador, double valor, String tipoPagamento, String observacao) {
        // Obter o número de telefone do membro a partir do CPF
        String numeroTelefone = getTelefoneByCpf(cpfDoador);

        if (numeroTelefone == null) {
            JOptionPane.showMessageDialog(this, "Número de telefone não encontrado para o CPF: " + cpfDoador, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Adicionar o código do país ao número de telefone
        numeroTelefone = "+55" + numeroTelefone;

        // Inicializar o Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Criar a mensagem
        String mensagem = String.format("Registro de Dízimo:\n• Valor: R$%.2f\n• Tipo: %s\n• Observação: %s", valor, tipoPagamento, observacao);

        Message message = Message.creator(
                new PhoneNumber(numeroTelefone), // Número do membro com o código do país
                new PhoneNumber("+14089566801"), // Seu número Twilio
                mensagem
        ).create();

        System.out.println("Mensagem enviada com SID: " + message.getSid());
    }

    private String getTelefoneByCpf(String cpf) {
        try {
            Membro membro = painelFinanceiro.getMembro(cpf); // Busca o membro pelo CPF

            if (membro != null) {
                return membro.getTelefone(); // Retorna o telefone se o membro for encontrado
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar telefone pelo CPF: " + e.getMessage());
        }

        return null; // Retorna nulo se não encontrar o membro ou se ocorrer um erro
    }

}
