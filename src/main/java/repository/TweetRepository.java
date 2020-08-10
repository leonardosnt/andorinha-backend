package repository;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import model.Tweet;
import model.seletor.TweetSeletor;

@Stateless
public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) {
		tweet.setData(Calendar.getInstance());
		em.persist(tweet);
	}

	public void atualizar(Tweet tweet) {
		tweet.setData(Calendar.getInstance());
		em.merge(tweet);
	}

	public void remover(int id) {
		Tweet tweet = consultar(id);
		if (tweet != null) {
			em.remove(tweet);
		}
	}

	public Tweet consultar(int id) {
		return em.find(Tweet.class, id);
	}

	public List<Tweet> listarTodos() {
		return pesquisar(new TweetSeletor());
	}

	@SuppressWarnings("unchecked")
	public List<Tweet> pesquisar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT t FROM Tweet t");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM Tweet t");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return (Long) query.getSingleResult();
	}

	private void adicionarFiltros(StringBuilder sql, TweetSeletor seletor) {
		if (seletor.possuiFiltro()) {
			sql.append(" WHERE ");

			boolean primeiroFiltro = true;

			if (seletor.getId() != null) {
				primeiroFiltro = false;
				sql.append(" id = :id ");
			}

			if (seletor.getIdUsuario() != null) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" id_usuario = :id_usuario ");
			}

			if (!StringUtils.isBlank(seletor.getConteudo())) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" conteudo LIKE :conteudo ");
			}

			if (seletor.getData() != null) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" date(data_postagem) = :data_postagem ");
			}
		}
	}

	private void adicionarParametros(Query query, TweetSeletor seletor) {
		if (seletor.possuiFiltro()) {
			if (seletor.getId() != null) {
				query.setParameter("id", seletor.getId());
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
