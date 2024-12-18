# 📋 Banco de Dados - Sistema de Gestão da Igreja

Bem-vindo ao repositório do **banco de dados** do sistema de gestão da igreja! Este projeto foi desenvolvido para atender às necessidades administrativas de uma igreja, oferecendo suporte ao gerenciamento de membros, grupos e finanças.

---

## 🛠️ Tecnologias Utilizadas

- **Banco de Dados:** PostgreSQL  
- **Ferramenta de Gerenciamento:** PGAdmin  
- **Modelo:** Normalização até a 3ª Forma Normal (3FN)  

---

## 📐 Estrutura do Banco de Dados

O banco de dados foi projetado com base em um **diagrama Entidade-Relacionamento (ER)** e dividido em tabelas para organizar os dados de maneira eficiente. Abaixo estão as principais tabelas e suas funções:

### 🗂️ Tabelas Principais

1. **Membros**
   - Armazena informações dos membros da igreja.
   - **Atributos:** 
     - `id_membro` (PK)
     - `nome`
     - `cpf`
     - `endereco`
     - `telefone`

2. **Grupos**
   - Gerencia os grupos existentes na igreja.
   - **Atributos:** 
     - `id_grupo` (PK)
     - `nome`
     - `responsavel`

3. **Participantes**
   - Relaciona membros com os grupos.
   - **Atributos:** 
     - `id_participacao` (PK)
     - `id_membro` (FK)
     - `id_grupo` (FK)

4. **Financeiro**
   - Controla as entradas e saídas financeiras.
   - **Atributos:** 
     - `id_transacao` (PK)
     - `tipo` (dízimo, ofertório, doação, retirada)
     - `valor`
     - `data_hora`
     - `id_membro` (FK, opcional)

---

## 💻 Como Usar o Banco de Dados

### 1. Pré-requisitos
- Instalar o **PostgreSQL**.
- Instalar o **PGAdmin** (opcional, mas recomendado para gerenciar o banco).

### 2. Importando o Banco de Dados
1. Clone o repositório:
   ```bash
   git clone https://github.com/carloSMSegundo/SistemaAreaPastoral.git
