package model.seletor;

public class AbstractBaseSeletor {

	private Integer limite;
	private Integer pagina;

	public boolean possuiPaginacao() {
		return (this.pagina != null && this.pagina > 0) && (this.limite != null && this.limite > 0);
	}

	public Integer getOffset() {
		if (this.possuiPaginacao()) {
			return this.limite * (this.pagina - 1);
		}
		return null;
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
}
