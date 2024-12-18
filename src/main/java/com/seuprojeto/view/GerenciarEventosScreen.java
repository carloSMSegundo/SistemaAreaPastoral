package com.seuprojeto.View;

import com.seuprojeto.Dados.Evento;
import com.seuprojeto.Dados.Igreja;
import com.seuprojeto.database.IgrejaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GerenciarEventosScreen extends JFrame {

    private JTable eventosTable;
    private DefaultTableModel tableModel;
    private Igreja igreja;
    private IgrejaDAO igrejaDAO;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public GerenciarEventosScreen(Igreja igreja, IgrejaDAO igrejaDAO) {
        this.igreja = igreja;
        this.igrejaDAO = igrejaDAO;
        setTitle("Gerenciar Eventos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Nome", "Data Início", "Data Fim", "Localização"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede que qualquer célula seja editável
            }
        };
        eventosTable = new JTable(tableModel);

        // Carregando eventos ao inicializar
        atualizarEventos(); // Chama o método para carregar eventos do banco de dados

        JScrollPane scrollPane = new JScrollPane(eventosTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editarButton = new JButton("Editar");
        JButton removerButton = new JButton("Remover");
        JButton adicionarButton = new JButton("Adicionar"); // Botão para adicionar eventos
        buttonPanel.add(adicionarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(removerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        editarButton.setBackground(new Color(76, 175, 80));
        editarButton.setForeground(Color.WHITE);
        removerButton.setBackground(new Color(244, 67, 54));
        removerButton.setForeground(Color.WHITE);
        adicionarButton.setBackground(new Color(33, 150, 243)); // Azul
        adicionarButton.setForeground(Color.WHITE);

        // Ações dos botões
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Passando igrejaDAO para a tela de cadastro
                CadastrarEventoScreen cadastrarEventoScreen = new CadastrarEventoScreen(igrejaDAO);
                cadastrarEventoScreen.setVisible(true);

                // Adicionar um listener para atualizar a tabela quando o evento for adicionado
                cadastrarEventoScreen.addEventoAdicionadoListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        atualizarEventos();
                    }
                });
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = eventosTable.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        Evento eventoSelecionado = igrejaDAO.getAllEventos().get(selectedRow);
                        CadastrarEventoScreen cadastrarEventoScreen = new CadastrarEventoScreen(eventoSelecionado, igrejaDAO);

                        // Define o listener para atualizar a tabela
                        cadastrarEventoScreen.setEventoAdicionadoListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                atualizarEventos(); // Chama o método para atualizar a tabela
                            }
                        });

                        cadastrarEventoScreen.setVisible(true);
                    } catch (IndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao acessar o evento. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um evento para editar.");
                }
            }
        });

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = eventosTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Deseja remover este evento?", "Confirmação", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            // Obter o evento da tabela antes de removê-lo
                            Evento eventoSelecionado = igrejaDAO.getAllEventos().get(selectedRow);
                            igrejaDAO.removeEvento(eventoSelecionado);
                            atualizarEventos(); // Atualiza a tabela após remoção
                        } catch (SQLException ex) {
                            Logger.getLogger(GerenciarEventosScreen.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Erro ao remover evento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um evento para remover.");
                }
            }
        });

        setVisible(true);
    }

    // Método modificado para receber uma lista de eventos
    void loadEventosIntoTable(List<Evento> eventos) {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar os eventos
        for (Evento evento : eventos) {
            tableModel.addRow(new Object[]{
                evento.getNome(),
                evento.getDataHoraInicio().format(dateTimeFormatter),
                evento.getDataHoraFim().format(dateTimeFormatter),
                evento.getLocalizacao()
            });
        }
    }

    // Novo método para atualizar a tabela de eventos
    public void atualizarEventos() {
        List<Evento> eventosAtualizados = igrejaDAO.getAllEventos(); // Busca os eventos do banco de dados
        loadEventosIntoTable(eventosAtualizados); // Chama o método existente para recarregar a tabela
    }
}
