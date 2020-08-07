package model.seletor;

import java.util.Calendar;

public class ComentarioSeletor {
	
	private Integer id;
	private String conteudo;
	private Calendar data;
	private Integer idUsuario;
	private Integer idTweet;

    private Integer limite; //10
    private Integer pagina; //3
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public Calendar getData() {
		return data;
	}
	public void setData(Calendar data) {
		this.data = data;
	}
	public Integer getIdTweet() {
		return idTweet;
	}
	public void setIdTweet(Integer idTweet) {
		this.idTweet = idTweet;
	}
	public Integer getLimite() {
		return limite;
	}
	public void setLimite(Integer limite) {
		this.limite = limite;
	}
	public Integer getPagina() {
		return pagina;
	}
	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}	
	
}
