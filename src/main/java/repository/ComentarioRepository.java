package repository;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import model.Comentario;
import model.seletor.ComentarioSeletor;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository {

	public void inserir(Comentario comentario) {
		comentario.setData(Calendar.getInstance());
		em.persist(comentario);
	}

	public void atualizar(Comentario comentario) {
		comentario.setData(Calendar.getInstance());
		em.merge(comentario);
	}

	public void remover(int id) {
		Comentario comentario = consultar(id);
		if (comentario != null) {
			em.remove(comentario);
		}
	}

	public Comentario consultar(int id) {
		return em.find(Comentario.class, id);
	}

	public List<Comentario> listarTodos() {
		return pesquisar(new ComentarioSeletor());
	}

	@SuppressWarnings("unchecked")
	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT c FROM Comentario c");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM Comentario c");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return (Long) query.getSingleResult();
	}

	private void adicionarFiltros(StringBuilder jpql, ComentarioSeletor seletor) {
		if (seletor.possuiFiltro()) {
			jpql.append(" WHERE ");

			boolean primeiroFiltro = true;

			if (seletor.getId() != null) {
				primeiroFiltro = false;
				jpql.append(" id = :id ");
			}

			if (seletor.getIdTweet() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" id_tweet = :id_tweet ");
			}

			if (seletor.getIdUsuario() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" id_usuario = :id_usuario ");
			}

			if (!StringUtils.isBlank(seletor.getConteudo())) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" conteudo LIKE :conteudo ");
			}

			if (seletor.getData() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" date(data_postagem) = :data_postagem ");
			}
		}
	}

	private void adicionarParametros(Query query, ComentarioSeletor seletor) {
		if (seletor.possuiFiltro()) {
			if (seletor.getId() != null) {
				query.setParameter("id", seletor.getId());
			}

			if (seletor.getIdTweet() != null) {
				query.setParameter("id_tweet", seletor.getIdTweet());
			}

			if (seletor.getIdUsuario() != null) {
				query.setParameter("id_usuario", seletor.getIdUsuario());
			}

			if (!StringUtils.isBlank(seletor.getConteudo())) {
				query.setParameter("conteudo", String.format("%%%s%%", seletor.getConteudo()));
			}

			if (seletor.getData() != null) {
				query.setParameter("data_postagem", seletor.getData());
			}
		}

		if (seletor.possuiPaginacao()) {
			int limite = seletor.getLimite();
			int offset = limite * (seletor.getPagina() - 1); // Páginas iniciam em 1

			query.setFirstResult(offset);
			query.setMaxResults(limite);
		}
	}
}
