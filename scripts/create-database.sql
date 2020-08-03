

create table usuario (
	id integer,
	nome varchar,
	constraint pk_usuario primary key(id)
);

create sequence seq_usuario start with 1 increment by 1;