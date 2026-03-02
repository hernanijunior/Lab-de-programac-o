-- ============================================================
--  SISTEMA DE CLÍNICA MÉDICA - Script MySQL
--  UFMT - Banco de Dados
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica;

-- ======================== TABELAS ========================

DROP TABLE IF EXISTS Consultas;
DROP TABLE IF EXISTS Medicos;
DROP TABLE IF EXISTS Pacientes;
DROP TABLE IF EXISTS Ambulatorios;
DROP TABLE IF EXISTS Funcionarios;

CREATE TABLE Ambulatorios (
    nroa        INT          NOT NULL,
    andar       NUMERIC(3)   NOT NULL,
    capacidade  SMALLINT,
    PRIMARY KEY (nroa)
);

CREATE TABLE Medicos (
    codm          INT          NOT NULL,
    nome          VARCHAR(40)  NOT NULL,
    idade         TINYINT      NOT NULL,
    especialidade CHAR(20),
    RG            NUMERIC(10)  UNIQUE,
    cidade        VARCHAR(30),
    nroa          INT,
    PRIMARY KEY (codm),
    FOREIGN KEY (nroa) REFERENCES Ambulatorios(nroa)
);

CREATE UNIQUE INDEX indMed_RG  ON Medicos (RG);
CREATE        INDEX indMed_nroa ON Medicos (nroa);

CREATE TABLE Pacientes (
    codp    INT          NOT NULL,
    nome    VARCHAR(40)  NOT NULL,
    idade   TINYINT      NOT NULL,
    cidade  VARCHAR(30),
    RG      NUMERIC(10)  UNIQUE,
    problema VARCHAR(40) NOT NULL,
    PRIMARY KEY (codp)
);

CREATE TABLE Consultas (
    codm           INT  NOT NULL,
    codp           INT  NOT NULL,
    data_consulta  DATE NOT NULL,
    hora           TIME NOT NULL,
    PRIMARY KEY (codm, codp, data_consulta, hora),
    FOREIGN KEY (codm) REFERENCES Medicos(codm),
    FOREIGN KEY (codp) REFERENCES Pacientes(codp)
);

CREATE TABLE Funcionarios (
    codf          INT            NOT NULL,
    nome          VARCHAR(40)    NOT NULL,
    idade         TINYINT        NOT NULL,
    RG            NUMERIC(10)    UNIQUE,
    salario       NUMERIC(8,2),
    departamento  VARCHAR(30),
    tempoServico  TINYINT,
    PRIMARY KEY (codf)
);

-- ======================== DADOS ========================

INSERT INTO Ambulatorios VALUES (1,1,30),(2,1,50),(3,2,40),(4,2,25),(5,2,50);

INSERT INTO Medicos VALUES
(1,'João',  40,'ortopedia',    1000010000,'Fpolis',  1),
(2,'Maria', 42,'traumatologia',1000011000,'Blumenau',2),
(3,'Pedro', 51,'pediatria',    1100010000,'Fpolis',  2),
(4,'Carlos',28,'ortopedia',    1100011000,'Joinville',NULL),
(5,'Márcia',33,'neurologia',   1100011100,'Biguaçu', 3);

INSERT INTO Pacientes VALUES
(1,'Ana',   20,'Fpolis',   2000020000,'gripe'),
(2,'Paulo', 24,'Palhoça',  2000022000,'fratura'),
(3,'Lúcia', 30,'Fpolis',   2200020000,'tendinite'),
(4,'Carlos',28,'Joinville',1100011000,'sarampo'),
(5,'Jorge',  9,'Joinville',2222022200,'câncer'),
(6,'Marcos',45,'Blumenau', 2222200000,'tendinite');

INSERT INTO Consultas VALUES
(1,1,'2002-11-12','14:00:00'),
(1,4,'2002-11-13','10:00:00'),
(2,1,'2002-11-13','09:00:00'),
(2,2,'2002-11-13','11:00:00'),
(2,3,'2002-11-14','14:00:00'),
(2,4,'2002-11-14','17:00:00'),
(3,1,'2002-11-17','18:00:00'),
(3,3,'2002-11-12','10:00:00'),
(3,4,'2002-11-15','13:00:00'),
(4,4,'2002-11-16','13:00:00');

INSERT INTO Funcionarios VALUES
(1,'Maria',  42,1000011000, 700.00,'pessoal', 2),
(2,'Lúcia',  30,2200020000,1500.00,'pessoal', 1),
(3,'Marcelo',27,3000030000,1000.00,'contábil',4),
(4,'Ricardo',33,3300030000,1200.00,'contábil',2),
(5,'José',   18,3330030000, 180.00,'limpeza', 3);
