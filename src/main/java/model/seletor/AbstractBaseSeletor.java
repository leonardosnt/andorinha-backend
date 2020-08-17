package model.seletor;

public class AbstractBaseSeletor {

	private Integer limite;
	private Integer pagina;

	private String orderField;
	private String orderType;

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

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderField() {
		if (this.orderField == null && this.possuiPaginacao()) {
			return "id";
		}
		return this.orderField;
	}

	public String getOrderType() {
		return this.orderType == null ? "asc" : this.orderType;
	}
}
