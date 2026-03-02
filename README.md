# Sistema de Clínica Médica — UFMT
> Projeto Final — Disciplina: Banco de Dados  
> Linguagem: Java 17 + JavaFX 17 | Banco: MySQL 8

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Java JDK   | 17           |
| Maven      | 3.8+         |
| MySQL      | 8.0+         |

---

## 1. Configurar o Banco de Dados

1. Abra seu cliente MySQL (Workbench, DBeaver, terminal…)
2. Execute o script:
   ```
   source caminho/para/database.sql
   ```
   Isso cria o banco `clinica`, todas as tabelas e insere os dados iniciais do curso.

---

## 2. Configurar a Conexão

Edite o arquivo:
```
src/main/java/clinica/database/Database.java
```

Altere as constantes no topo da classe:
```java
private static final String URL  = "jdbc:mysql://localhost:3306/clinica?...";
private static final String USER = "root";      // seu usuário MySQL
private static final String PASS = "root";      // sua senha MySQL
```

---

## 3. Compilar e Executar

```bash
# Compilar e executar diretamente
mvn javafx:run

# OU gerar o JAR executável
mvn package
java -jar target/clinica-medica-1.0-SNAPSHOT.jar
```

---

## Funcionalidades Implementadas

### Cadastros (CRUD completo)
- **Ambulatórios** — nroa, andar, capacidade
- **Médicos** — codm, nome, idade, especialidade, RG, cidade, ambulatório
- **Pacientes** — codp, nome, idade, cidade, RG, problema
- **Funcionários** — codf, nome, idade, RG, salário, departamento, tempo de serviço
- **Consultas** — médico, paciente, data, hora

### Gerenciamento de Consultas
- Agendamento de novas consultas
- Remarcação (alterar data e hora de consulta existente)
- Remoção por médico (remove todas as consultas de um médico)
- Remoção por horário (remove todas as consultas em determinado horário)
- Remoção individual (selecionar na tabela)
- Filtragem por médico ou por paciente

### Relatórios (aba "📊 Relatórios")
| # | Relatório |
|---|-----------|
| a | Todas as consultas médicas |
| b | Ambulatório com maior capacidade onde nenhum médico atende |
| c | Funcionários com salário superior a todos os salários de um departamento |
| d | Salários de todos os funcionários ou filtrado por departamento |
| e | Médicos com consultas em uma data específica |
| f | Médicos com consulta com determinado paciente |
| g | Idades dos médicos e total de médicos com a mesma idade |
| h | Andares com ambulatórios e média de capacidade por andar |
| i | Pacientes com consultas em horários anteriores a todos os horários de uma data |

### Transações MySQL
Todas as operações de inserção, atualização e remoção utilizam:
- `connection.setAutoCommit(false)` — transações manuais
- `connection.commit()` — confirma após sucesso
- `connection.rollback()` — reverte em caso de erro

---

## Estrutura do Projeto

```
clinica-medica/
├── database.sql                          ← Script MySQL (criar e popular)
├── pom.xml                               ← Maven (dependências JavaFX + MySQL)
└── src/main/java/clinica/
    ├── Main.java                         ← Ponto de entrada JavaFX
    ├── database/
    │   └── Database.java                 ← Conexão + commit/rollback
    ├── model/
    │   ├── Ambulatorio.java
    │   ├── Medico.java
    │   ├── Paciente.java
    │   ├── Funcionario.java
    │   └── Consulta.java
    ├── dao/
    │   ├── AmbulatorioDAO.java
    │   ├── MedicoDAO.java
    │   ├── PacienteDAO.java
    │   ├── FuncionarioDAO.java
    │   └── ConsultaDAO.java
    └── ui/
        ├── MainWindow.java               ← Janela principal + TabPane
        ├── UiHelper.java                 ← Utilitários de UI
        ├── AmbulatorioTab.java
        ├── MedicoTab.java
        ├── PacienteTab.java
        ├── FuncionarioTab.java
        ├── ConsultaTab.java
        └── RelatoriosTab.java
```
