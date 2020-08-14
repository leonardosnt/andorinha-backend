package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import model.Tweet;
import model.dto.TweetDTO;
import model.seletor.TweetSeletor;

@Stateless
public class TweetRepository extends AbstractCrudRepository<Tweet> {

	public List<Tweet> listarTodos() {
		return pesquisar(new TweetSeletor());
	}

	public List<Tweet> pesquisar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT t FROM Tweet t INNER JOIN FETCH t.usuario");
		adicionarFiltros(jpql, seletor);

		TypedQuery<Tweet> query = super.em.createQuery(jpql.toString(), Tweet.class);
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public List<TweetDTO> pesquisarDTO(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT new model.dto.TweetDTO(t.id, u.id, u.nome, t.data, t.conteudo) FROM Tweet t INNER JOIN t.usuario u");
		adicionarFiltros(jpql, seletor);

		TypedQuery<TweetDTO> query = super.em.createQuery(jpql.toString(), TweetDTO.class);
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM Tweet t");
		adicionarFiltros(jpql, seletor);

		TypedQuery<Long> query = super.em.createQuery(jpql.toString(), Long.class);
		adicionarParametros(query, seletor);

		return query.getSingleResult();
	}

	private void adicionarFiltros(StringBuilder sql, TweetSeletor seletor) {
		if (seletor.possuiFiltro()) {
			sql.append(" WHERE ");

			boolean primeiroFiltro = true;

			if (seletor.getId() != null) {
				primeiroFiltro = false;
				sql.append(" t.id = :id ");
			}

			if (seletor.getIdUsuario() != null) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" t.usuario.id = :id_usuario ");
			}

			if (!StringUtils.isBlank(seletor.getConteudo())) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" t.conteudo LIKE :conteudo ");
			}

			if (seletor.getData() != null) {
				if (!primeiroFiltro) sql.append(" AND ");
				primeiroFiltro = false;
				sql.append(" date(t.data) = :data_postagem ");
			}
		}

		if (seletor.possuiPaginacao()) {
			// Por padrão, ordena pelo id na paginação.
			sql.append(" ORDER BY t.id ");
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
