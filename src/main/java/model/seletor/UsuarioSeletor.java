package model.seletor;

import org.apache.commons.lang3.StringUtils;

public class UsuarioSeletor extends AbstractBaseSeletor {

	private Integer id;
	private String nome;

	public boolean possuiFiltro() {
		return this.id != null || !StringUtils.isBlank(this.nome);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}