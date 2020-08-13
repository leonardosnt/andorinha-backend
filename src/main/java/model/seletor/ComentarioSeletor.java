package model.seletor;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class ComentarioSeletor {

	private Integer id;
	private String conteudo;
	private Calendar data;
	private Integer idUsuario;
	private Integer idTweet;

	private int limite;
	private int pagina;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getConteudo() {
		return this.conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Calendar getData() {
		return this.data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public Integer getIdTweet() {
		return this.idTweet;
	}

	public void setIdTweet(Integer idTweet) {
		this.idTweet = idTweet;
	}

	public Integer getLimite() {
		return this.limite;
	}

	public void setLimite(Integer limite) {
		this.limite = limite;
	}

	public Integer getPagina() {
		return this.pagina;
	}

	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public boolean possuiFiltro() {
		return this.id != null || this.idTweet != null || this.idUsuario != null || this.data != null
				|| !StringUtils.isBlank(this.conteudo);
	}

	public boolean possuiPaginacao() {
		return this.limite > 0 && this.pagina > 0;
	}

}
