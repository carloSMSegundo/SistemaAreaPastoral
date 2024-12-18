package com.seuprojeto.database;

public class Member {
    private String cpf;
    private String nome;
    private String endereco;
    private String telefone;
    private String sexo;

    // Construtor
    public Member(String cpf, String nome, String endereco, String telefone, String sexo) {
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.sexo = sexo;
    }

    // Getters
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSexo() {
        return sexo;
    }
}