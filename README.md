# Board de Tarefas 📝

O **Board de Tarefas** é uma aplicação backend desenvolvida em Java projetada para gerenciar o fluxo de atividades de um projeto ou equipe. Semelhante a ferramentas como Trello ou Jira (em nível de lógica de dados), este sistema permite criar, organizar e acompanhar o status de tarefas desde o planejamento até a conclusão.

Este projeto tem um forte caráter educacional e técnico: seu objetivo principal é demonstrar a implementação de uma arquitetura de software profissional, saindo do básico e aplicando padrões de mercado como injeção de dependências, separação de camadas, migração de banco de dados versionada e tratamento robusto de exceções.

## 🚀 Tecnologias e Ferramentas utilizadas

Para garantir um ciclo de vida de desenvolvimento moderno, confiável e escalável, o projeto foi construído sobre uma stack tecnológica robusta. Utilizamos o **Gradle** na sua versão mais recente para automação de build, garantindo gestão eficiente de dependências, e o **Liquibase** para tratar o banco de dados como código (Database as Code), permitindo que a evolução do esquema do banco acompanhe a evolução do código Java.

* **Java (JDK 17+):** Linguagem core do projeto, utilizando recursos modernos da API.
* **Gradle 9.0:** Ferramenta de automação de build e gerenciamento de dependências.
* **MySQL:** Sistema gerenciador de banco de dados relacional (RDBMS).
* **MySQL Workbench:** Interface gráfica utilizada para modelagem e consulta manual.
* **Liquibase:** Ferramenta de *database migration* para versionamento e atualização automática do schema do banco.
* **JDBC:** Conectividade nativa para execução de SQL e manipulação de dados.

## 🏗 Arquitetura do Projeto

A organização do código segue rigorosamente a **Arquitetura em Camadas (Layered Architecture)**. Essa escolha foi feita para promover a "Separação de Responsabilidades" (SoC). Isso significa que a interface não sabe como o banco funciona, e o banco não sabe como os dados são mostrados. Isso facilita a manutenção, testes e futuras expansões do software.

A estrutura é dividida em:

1.  **UI (User Interface):** É a porta de entrada. Responsável apenas por interagir com o usuário (receber comandos e mostrar respostas), sem conter regras de negócio.
2.  **DTO (Data Transfer Object):** Objetos simples usados puramente para transportar dados entre a UI e o Service. Eles filtram o que entra e sai, protegendo a estrutura interna do banco.
3.  **Service (Camada de Serviço):** O "cérebro" da aplicação. Aqui residem as regras de negócio, validações lógicas e o controle de fluxo. O Service orquestra as chamadas para o banco.
4.  **DAO (Data Access Object):** A única camada que toca o banco de dados. Responsável por executar comandos SQL (INSERT, UPDATE, SELECT) e mapear os resultados.
5.  **Entidades (Entities):** Classes que espelham exatamente as tabelas do banco de dados.

## ⚙️ Funcionalidades

O sistema foi desenvolvido para cobrir o ciclo completo de gerenciamento de dados, garantindo integridade e persistência. Além das operações básicas de manipulação de tarefas, o projeto conta com funcionalidades de infraestrutura, como configuração dinâmica de ambiente e migração automática de dados, eliminando a necessidade de rodar scripts SQL manuais ao instalar o projeto.

As principais funcionalidades incluem:

* **CRUD de Tarefas:** Criação, Leitura, Atualização e Remoção de cards/tarefas.
* **Controle de Status:** Movimentação de tarefas entre estados (ex: A Fazer, Em Progresso, Concluído).
* **Migrations (Liquibase):** Criação e alteração automática de tabelas ao iniciar o projeto (`update`).
* **Gerenciamento de Erros:** Sistema centralizado de *Exception Handling* para capturar falhas e informar o usuário de forma amigável.
* **Configuração Externa:** Leitura do arquivo `db.properties` para conexão, permitindo trocar de ambiente (Dev/Prod) sem recompilar o código.

## 📥 Instalação e Configuração

Siga os passos abaixo para baixar, configurar e executar o projeto em sua máquina local.

### 1. Clonar o repositório

    git clone https://github.com/seu-usuario/board-de-tarefas.git
    cd board-de-tarefas

### 2. Preparar o Banco de Dados

Abra o MySQL Workbench (ou seu terminal) e crie apenas o schema (banco de dados) vazio. O Liquibase criará as tabelas para você depois.

    CREATE DATABASE taskboard_db;

### 3. Configurar Credenciais (`db.properties`)

Por segurança, as senhas não são versionadas. Crie um arquivo chamado `db.properties` dentro da pasta `src/main/resources/` com o seguinte conteúdo:

    db.url=jdbc:mysql://localhost:3306/taskboard_db
    db.user=seu_usuario_mysql
    db.password=sua_senha_mysql

### 4. Executar Migrations

Use o Gradle para rodar o Liquibase. Isso lerá os arquivos de *changelog* e criará todas as tabelas necessárias automaticamente.

    ./gradlew update

### 5. Executar a Aplicação

Compile e inicie o sistema:

    ./gradlew run

---

### 📂 Estrutura de Pastas

Para facilitar a navegação, entenda como os arquivos estão organizados:

    src/main/java/com/projeto/board/
    ├── ui/          # Classes de interação com o usuário (Console)
    ├── service/     # Lógica de negócio e validações
    ├── dto/         # Objetos para transporte de dados (Input/Output)
    ├── dao/         # Classes de persistência (JDBC/SQL)
    └── entity/      # Mapeamento das tabelas do banco

    src/main/resources/
    ├── db/changelog # Arquivos de versionamento do Liquibase (XML/YAML/SQL)
    └── db.properties # Arquivo de configuração (Ignorado pelo Git)

---
Desenvolvido com ☕ e Java.
