-- Script de banco de dados do LabControl — Etapa 9.
--
-- Estrutura de tabelas idêntica à das Etapas 6/7 (UC_15_Etapa6.sql) —
-- nenhuma coluna foi adicionada ou removida, então o JPA (com
-- ddl-auto=update) não precisa alterar o schema.
--
-- ATENÇÃO — diferença importante em relação ao script da Etapa 6:
-- lá, as colunas 'perfil' e 'status' guardavam o RÓTULO em português
-- ('Administrador', 'Em uso'), porque eram lidas por código JDBC
-- manual que fazia essa conversão. Aqui, as entidades usam
-- @Enumerated(EnumType.STRING) do JPA, que grava e espera o NOME
-- do enum Java (ADMINISTRADOR, EM_USO) — por isso os valores de
-- exemplo abaixo foram ajustados. Se você reaproveitar um banco já
-- populado pela Etapa 6, rode as duas instruções UPDATE no final
-- deste arquivo para corrigir os dados existentes.

CREATE DATABASE IF NOT EXISTS labcontrol;

USE labcontrol;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS equipamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Dados de exemplo (só inseridos se as tabelas estiverem vazias, via
-- o script de verificação abaixo — em um banco novo, pode inserir
-- direto).

INSERT INTO usuarios (nome, email, perfil, login, senha) VALUES
('Administrador Geral', 'admin@lab.com', 'ADMINISTRADOR', 'admin', '123456'),
('Carlos Técnico', 'tecnico@lab.com', 'TECNICO_LABORATORIO', 'tecnico', '123456'),
('Ana Operadora', 'operador@lab.com', 'OPERADOR', 'operador', '123456');

INSERT INTO equipamentos (nome, descricao, quantidade, status) VALUES
('Microscópio óptico binocular', 'Microscópio para aulas práticas de biologia.', 8, 'DISPONIVEL'),
('Balança analítica 0,1mg', 'Balança de precisão para química analítica.', 2, 'DISPONIVEL'),
('Pipeta automática 1000µl', 'Pipeta volumétrica ajustável.', 0, 'MANUTENCAO'),
('Estufa de secagem e esterilização', 'Estufa para secagem de vidraria.', 4, 'DISPONIVEL'),
('Agitador magnético com aquecimento', 'Agitador para soluções em bancada.', 1, 'EM_USO'),
('Kit de vidraria volumétrica', 'Conjunto de béqueres e provetas.', 15, 'DISPONIVEL');

-- ---------------------------------------------------------------
-- Caso esteja reaproveitando um banco já criado pela Etapa 6 (com
-- os rótulos antigos em português), rode estas correções antes de
-- iniciar a aplicação:
--
-- UPDATE usuarios SET perfil = 'ADMINISTRADOR' WHERE perfil = 'Administrador';
-- UPDATE usuarios SET perfil = 'TECNICO_LABORATORIO' WHERE perfil = 'Técnico de Laboratório';
-- UPDATE usuarios SET perfil = 'OPERADOR' WHERE perfil = 'Operador';
--
-- UPDATE equipamentos SET status = 'DISPONIVEL' WHERE status = 'Disponível';
-- UPDATE equipamentos SET status = 'EM_USO' WHERE status = 'Em uso';
-- ---------------------------------------------------------------
