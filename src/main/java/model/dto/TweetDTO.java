package model.dto;

import java.util.Calendar;

public class TweetDTO {

	private int id;
	private int idUsuario;

	private String nomeUsuario;
	private Calendar data;
	private String conteudo;

	public TweetDTO() {
	}

	public TweetDTO(int id, int idUsuario, String nomeUsuario, Calendar data, String conteudo) {
		this.id = id;
		this.idUsuario = idUsuario;
		this.nomeUsuario = nomeUsuario;
		this.data = data;
		this.conteudo = conteudo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

}
