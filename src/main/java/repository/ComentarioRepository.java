package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import model.Comentario;
import model.dto.ComentarioDTO;
import model.seletor.ComentarioSeletor;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository<Comentario> {

	public List<Comentario> listarTodos() {
		return pesquisar(new ComentarioSeletor());
	}

	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT c FROM Comentario c INNER JOIN FETCH c.usuario INNER JOIN FETCH c.tweet t INNER JOIN FETCH t.usuario");
		adicionarFiltros(jpql, seletor);

		TypedQuery<Comentario> query = super.em.createQuery(jpql.toString(), Comentario.class);
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public List<ComentarioDTO> pesquisarDTO(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT new model.dto.ComentarioDTO(c.id, t.id, u.id, u.nome, c.data, c.conteudo) ");
		jpql.append("FROM Comentario c ");
		jpql.append("INNER JOIN c.usuario u ");
		jpql.append("INNER JOIN c.tweet t ");
		jpql.append("INNER JOIN t.usuario ");
		adicionarFiltros(jpql, seletor);

		TypedQuery<ComentarioDTO> query = super.em.createQuery(jpql.toString(), ComentarioDTO.class);
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM Comentario c");
		adicionarFiltros(jpql, seletor);

		TypedQuery<Long> query = super.em.createQuery(jpql.toString(), Long.class);
		adicionarParametros(query, seletor);

		return query.getSingleResult();
	}

	private void adicionarFiltros(StringBuilder jpql, ComentarioSeletor seletor) {
		if (seletor.possuiFiltro()) {
			jpql.append(" WHERE ");

			boolean primeiroFiltro = true;

			if (seletor.getId() != null) {
				primeiroFiltro = false;
				jpql.append(" c.id = :id ");
			}

			if (seletor.getIdTweet() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" c.tweet.id = :id_tweet ");
			}

			if (seletor.getIdUsuario() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" c.usuario.id = :id_usuario ");
			}

			if (!StringUtils.isBlank(seletor.getConteudo())) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" c.conteudo LIKE :conteudo ");
			}

			if (seletor.getData() != null) {
				if (!primeiroFiltro) jpql.append(" AND ");
				primeiroFiltro = false;
				jpql.append(" date(c.data) = :data_postagem ");
			}
		}

		if (seletor.possuiPaginacao()) {
			// Por padrão, ordena pelo id na paginação.
			jpql.append(" ORDER BY c.id ");
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
