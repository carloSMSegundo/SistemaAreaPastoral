package com.seuprojeto.Dados;

public class Membro {

    private String cpf;
    private String nome;
    private String endereco;
    private String telefone;
    private String sexo;
    private String cargo; // Novo atributo

    // Construtor
    public Membro(String cpf, String nome, String endereco, String telefone, String sexo, String cargo) {
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.sexo = sexo;
        this.cargo = cargo; // Inicializa o novo atributo
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCargo() { // Getter para o cargo
        return cargo;
    }

    public void setCargo(String cargo) { // Setter para o cargo
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", CPF: " + cpf + ", Telefone: " + telefone + ", Endereço: " + endereco + ", Sexo: " + sexo + ", Cargo: " + cargo;
    }
}
