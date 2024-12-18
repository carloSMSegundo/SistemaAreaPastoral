package com.seuprojeto.Dados;

import java.time.LocalDateTime;

public class Log {
    private String tipo;
    private String pessoa;
    private double valor;
    private LocalDateTime dataHora;

    public Log(String tipo, String pessoa, double valor, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.pessoa = pessoa;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    // Getters e setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Log{" +
                "tipo='" + tipo + '\'' +
                ", pessoa='" + pessoa + '\'' +
                ", valor=" + valor +
                ", dataHora=" + dataHora +
                '}';
    }
}
