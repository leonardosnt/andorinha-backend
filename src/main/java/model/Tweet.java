package model;

import java.util.Calendar;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tweet")
public class Tweet {

	@Id
	@SequenceGenerator(name = "seq_tweet", sequenceName = "seq_tweet", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tweet")
	@Column(name = "id")
	private int id;

	@Column(name = "conteudo")
	private String conteudo;

	@Column(name = "data_postagem")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar data;

	@ManyToOne
	@JoinColumn(name = "id_usuario", referencedColumnName = "id")
	private Usuario usuario;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@PrePersist
	@PreUpdate
	private void preencheData() {
		this.data = Calendar.getInstance();
	}

	@Override
	public int hashCode() {
		return Objects.hash(conteudo, data, id, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tweet other = (Tweet) obj;
		return Objects.equals(conteudo, other.conteudo) && Objects.equals(data, other.data) && id == other.id
				&& Objects.equals(usuario, other.usuario);
	}

}
