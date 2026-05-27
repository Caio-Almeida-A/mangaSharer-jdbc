# 📖 MangaSharer - Sistema de Gerenciamento de Mangás
Este projeto consiste em uma aplicação desktop integrada a um banco de dados relacional para o gerenciamento de obras de mangás, interações de leitores e auditoria administrativa. O sistema foi desenvolvido em Java utilizando JDBC puro

# 🛠️ Pré-requisitos
Antes de iniciar, certifique-se de ter instalado em sua máquina:

MySQL Server (v8.0 ou superior) e MySQL Workbench.

Visual Studio Code (VS Code) com o pacote de extensões Java Extension Pack instalado.

Java Development Kit (JDK 25): Caso não possua o motor do Java instalado, baixe a versão estável do Eclipse Temurin diretamente pelo site oficial: https://adoptium.net/pt-BR 


# 🎲 1. Configuração do Banco de Dados (MySQL)
Abra o MySQL Workbench e conecte-se à sua instância local.

Dentro de arquivosParte01:
 1. Execute o script de criação das tabelas base do sistema (tabela_mangaSharer.sql).
 2. Execute o script de população inicial de dados (insercao_mangaSharer.sql).

Execute as estruturas lógicas criadas em arquivosParte02:
 1. Consultas e Visões  (consulta_mangaSharer.sql ,visao_mangaSharer.sql)
 2. Procedimentos, Funções e Triggers  (procedure_mangaSharer.sql , funcao_mangaSharer.sql e trigger_mangaSharer.sql)

Instalação da Tabela de Auditoria: Para que o trigger de logs de ações administrativas funcione perfeitamente, execute o comando abaixo

```sql
USE manga_db2;

CREATE TABLE IF NOT EXISTS LogGerenciamento (
    idAdmin INT,
    idPadrao INT,
    dataAcao DATETIME
);
```

# 💻 2. Configuração do Projeto no VS Code
No VS Code, navegue até o arquivo `src/util/ConnectionFactory.java`
Altere o valor da constante URL,USER e PASS para o correspondente ao seu usuário local do MySQL


# 🚀 3. Como Rodar a Aplicação
Abra o arquivo `src/main/Main.java`.
Clique no botão Run (ou no ícone de Play)