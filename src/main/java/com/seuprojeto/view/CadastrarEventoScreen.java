package com.seuprojeto.View;

import com.seuprojeto.Dados.Evento;
import com.seuprojeto.database.DatabaseConnection;
import com.seuprojeto.database.IgrejaDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CadastrarEventoScreen extends JFrame {

    private JTextField nomeField;
    private JSpinner dataInicioSpinner;
    private JSpinner horaInicioSpinner;
    private JSpinner dataFimSpinner;
    private JSpinner horaFimSpinner;
    private JTextField localField;
    private JButton cadastrarButton;
    private Evento eventoEdicao;
    private IgrejaDAO igrejaDAO;
    private ActionListener eventoAdicionadoListener; // Adicione essa variável

    public CadastrarEventoScreen(IgrejaDAO igrejaDAO) {
        this.igrejaDAO = igrejaDAO;
        setTitle("Cadastrar Evento");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nome do evento
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Nome do Evento:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        nomeField = new JTextField();
        add(nomeField, gbc);

        // Data e hora de início do evento
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Data Início (dd/MM/yyyy):"), gbc);
        dataInicioSpinner = createDateSpinner();
        gbc.gridx = 1;
        add(dataInicioSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Hora Início (HH:mm):"), gbc);
        horaInicioSpinner = createTimeSpinner();
        gbc.gridx = 1;
        add(horaInicioSpinner, gbc);

        // Data e hora de fim do evento
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Data Fim (dd/MM/yyyy):"), gbc);
        dataFimSpinner = createDateSpinner();
        gbc.gridx = 1;
        add(dataFimSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Hora Fim (HH:mm):"), gbc);
        horaFimSpinner = createTimeSpinner();
        gbc.gridx = 1;
        add(horaFimSpinner, gbc);

        // Local do evento
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(new JLabel("Local:"), gbc);
        localField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(localField, gbc);

        // Botão de cadastrar
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        cadastrarButton = new JButton("Cadastrar");
        add(cadastrarButton, gbc);

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarEvento();
            }
        });

        setVisible(true);
    }

    public CadastrarEventoScreen(Evento evento, IgrejaDAO igrejaDAO) {
        this(igrejaDAO);
        this.eventoEdicao = evento;
        preencherCamposEvento(evento);
    }

    public void setEventoAdicionadoListener(ActionListener listener) {
        this.eventoAdicionadoListener = listener;
    }

    private void cadastrarEvento() {
        String nome = nomeField.getText();
        String dataInicio = new SimpleDateFormat("dd/MM/yyyy").format((Date) dataInicioSpinner.getValue());
        String horaInicio = new SimpleDateFormat("HH:mm").format((Date) horaInicioSpinner.getValue());
        String dataFim = new SimpleDateFormat("dd/MM/yyyy").format((Date) dataFimSpinner.getValue());
        String horaFim = new SimpleDateFormat("HH:mm").format((Date) horaFimSpinner.getValue());
        String local = localField.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try {
            LocalDateTime dataHoraInicio = LocalDateTime.parse(dataInicio + " " + horaInicio, formatter);
            LocalDateTime dataHoraFim = LocalDateTime.parse(dataFim + " " + horaFim, formatter);

            // Verifique se os dados são válidos
            if (nome.isEmpty() || local.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos corretamente.");
                return;
            }

            // Verifique se a data de início é anterior à data de fim
            if (dataHoraInicio.isAfter(dataHoraFim)) {
                JOptionPane.showMessageDialog(null, "A data de início deve ser anterior à data de fim.");
                return;
            }

            int confirm;
            if (eventoEdicao != null) {
                confirm = JOptionPane.showConfirmDialog(null, "Deseja atualizar este evento?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    atualizarEvento(nome, dataHoraInicio, dataHoraFim, local);
                    JOptionPane.showMessageDialog(null, "Evento atualizado com sucesso!");
                }
            } else {
                confirm = JOptionPane.showConfirmDialog(null, "Deseja cadastrar este evento?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Evento evento = new Evento(nome, dataHoraInicio, dataHoraFim, local);
                    inserirEventoNoBanco(evento);
                    // Chame o listener, se não for nulo
                    if (eventoAdicionadoListener != null) {
                        eventoAdicionadoListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                    }
                    JOptionPane.showMessageDialog(null, "Evento cadastrado com sucesso!");
                }
            }
        } catch (Exception ex) {
            // Log de erro detalhado
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar/atualizar evento. Verifique os dados inseridos.");
            ex.printStackTrace(); // Adicione isto para ajudar no debug
        }
    }

    public void addEventoAdicionadoListener(ActionListener listener) {
        this.eventoAdicionadoListener = listener;
    }

    private void inserirEventoNoBanco(Evento evento) {
        String sql = "INSERT INTO eventos (nome, data_inicio, data_fim, local) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setTimestamp(2, Timestamp.valueOf(evento.getDataHoraInicio()));
            stmt.setTimestamp(3, Timestamp.valueOf(evento.getDataHoraFim()));
            stmt.setString(4, evento.getLocalizacao());
            stmt.executeUpdate();
            System.out.println("Evento inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir evento no banco de dados: " + e.getMessage());
        }
    }

    private void atualizarEvento(String nome, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String local) {
        eventoEdicao.setNome(nome);
        eventoEdicao.setDataHoraInicio(dataHoraInicio);
        eventoEdicao.setDataHoraFim(dataHoraFim);
        eventoEdicao.setLocalizacao(local);

        String sql = "UPDATE eventos SET nome = ?, data_inicio = ?, data_fim = ?, local = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventoEdicao.getNome());
            stmt.setTimestamp(2, Timestamp.valueOf(eventoEdicao.getDataHoraInicio()));
            stmt.setTimestamp(3, Timestamp.valueOf(eventoEdicao.getDataHoraFim()));
            stmt.setString(4, eventoEdicao.getLocalizacao());
            stmt.setInt(5, eventoEdicao.getId());
            stmt.executeUpdate();
            System.out.println("Evento atualizado com sucesso.");

            // Chame o método para atualizar a tabela de eventos
            if (eventoAdicionadoListener != null) {
                eventoAdicionadoListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar evento no banco de dados: " + e.getMessage());
        }
    }

    private void preencherCamposEvento(Evento evento) {
        if (evento != null) {
            nomeField.setText(evento.getNome());
            dataInicioSpinner.setValue(Date.from(evento.getDataHoraInicio().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            horaInicioSpinner.setValue(Date.from(evento.getDataHoraInicio().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            dataFimSpinner.setValue(Date.from(evento.getDataHoraFim().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            horaFimSpinner.setValue(Date.from(evento.getDataHoraFim().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            localField.setText(evento.getLocalizacao());
        } else {
            JOptionPane.showMessageDialog(null, "Evento não encontrado.");
        }
    }

    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        return spinner;
    }

    private JSpinner createTimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        return spinner;
    }

}
