package model.seletor;

import org.apache.commons.lang3.StringUtils;

public class UsuarioSeletor {

	private Integer id;
	private String nome;

	private int limite;
	private int pagina;

	public boolean possuiFiltro() {
		return this.id != null || !StringUtils.isBlank(this.nome);
	}

	public boolean possuiPaginacao() {
		return this.pagina > 0 && this.limite > 0;
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

	public int getLimite() {
		return this.limite;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}

	public int getPagina() {
		return this.pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

}