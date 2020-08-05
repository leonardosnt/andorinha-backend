package model;

import java.time.Instant;

public class Comentario {
	
	private int id;
	private String conteudo;
	private Instant dataCriacao;
	private Usuario usuario;
	private Tweet tweet;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public Instant getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Instant dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Tweet getTweet() {
		return tweet;
	}
	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}
	
	
}
