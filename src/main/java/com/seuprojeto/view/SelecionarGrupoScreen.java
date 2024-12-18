package com.seuprojeto.view;

import com.seuprojeto.Dados.Grupo;
import com.seuprojeto.Dados.Igreja;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class SelecionarGrupoScreen extends JFrame {

    private Igreja igreja;

    public SelecionarGrupoScreen(Igreja igreja) {
        this.igreja = igreja;
        initUI();
    }

    private void initUI() {
        setTitle("Selecionar Grupo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        add(panel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel grupoLabel = new JLabel("Selecione um Grupo:");
        grupoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(grupoLabel, gbc);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> grupoList = new JList<>(listModel);
        grupoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(grupoList);
        scrollPane.setPreferredSize(new Dimension(360, 150));
        gbc.gridy = 1;
        panel.add(scrollPane, gbc);

        // Preencher a lista com os nomes dos grupos cadastrados
        Map<String, Grupo> gruposMap = igreja.getGrupos();
        if (gruposMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum grupo encontrado. Crie um grupo primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            dispose(); // Fecha a janela se não houver grupos
            return;
        } else {
            for (Grupo grupo : gruposMap.values()) {
                listModel.addElement(grupo.getNome());
            }
        }

        JButton selecionarButton = new JButton("Selecionar Grupo");
        selecionarButton.setFont(new Font("Arial", Font.BOLD, 14));
        selecionarButton.setBackground(new Color(0, 150, 0)); // Cor de fundo verde
        selecionarButton.setForeground(Color.WHITE); // Cor do texto
        selecionarButton.setFocusPainted(false); // Remove a borda ao clicar
        selecionarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Adiciona espaçamento interno
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(selecionarButton, gbc);

        selecionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeGrupoSelecionado = grupoList.getSelectedValue();

                if (nomeGrupoSelecionado == null) {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Grupo grupoSelecionado = igreja.getGrupos().get(nomeGrupoSelecionado);
                if (grupoSelecionado != null) {
                    new GerenciarMembrosScreen(igreja, grupoSelecionado);
                    dispose(); // Fecha a janela de seleção após selecionar o grupo
                } else {
                    JOptionPane.showMessageDialog(null, "Grupo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }
}
