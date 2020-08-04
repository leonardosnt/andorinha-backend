-- Para executar o script use: psql -U postgres -d andorinha_test -f init.sql

BEGIN TRANSACTION;

CREATE TABLE usuario (
    id INTEGER,
    nome VARCHAR NOT NULL,

    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

CREATE TABLE tweet (
    id INTEGER,
    conteudo VARCHAR NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    id_usuario INTEGER,

    CONSTRAINT pk_tweet PRIMARY KEY (id),
    CONSTRAINT fk_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_tweet START WITH 1 INCREMENT BY 1;

COMMIT;