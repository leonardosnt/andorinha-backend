package model.seletor;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class TweetSeletor extends AbstractBaseSeletor {

	private Integer id;
	private String conteudo;
	private Calendar data;
	private Integer idUsuario;

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

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public boolean possuiFiltro() {
		return this.id != null || this.idUsuario != null || this.data != null || StringUtils.isNotBlank(this.conteudo);
	}

}
