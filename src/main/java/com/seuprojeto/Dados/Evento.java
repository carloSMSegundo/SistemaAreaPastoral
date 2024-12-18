package com.seuprojeto.Dados;

import java.time.LocalDateTime;

public class Evento {

    private int id;  // Adicionando o campo ID
    private String nome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String localizacao;

    // Construtor para novos eventos (sem ID)
    public Evento(String nome, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String localizacao) {
        this.nome = nome;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.localizacao = localizacao;
    }

    // Construtor para eventos existentes (com ID)
    public Evento(int id, String nome, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String localizacao) {
        this.id = id;
        this.nome = nome;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.localizacao = localizacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
