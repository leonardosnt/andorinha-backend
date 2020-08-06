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
    data_postagem TIMESTAMP NOT NULL,
    id_usuario INTEGER,

    CONSTRAINT pk_tweet PRIMARY KEY (id),
    CONSTRAINT fk_tweet_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id) ON DELETE RESTRICT
);

CREATE TABLE comentario (
    id INTEGER,
    conteudo VARCHAR NOT NULL,
    data_postagem TIMESTAMP NOT NULL,
    id_usuario INTEGER,
    id_tweet INTEGER,

    CONSTRAINT pk_comentario PRIMARY KEY (id),
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_comentario_tweet FOREIGN KEY (id_tweet) REFERENCES tweet (id) ON DELETE CASCADE
);

CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_tweet START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_comentario START WITH 1 INCREMENT BY 1;

COMMIT;