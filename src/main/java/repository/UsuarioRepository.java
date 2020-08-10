package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import model.Usuario;
import model.seletor.UsuarioSeletor;

@Stateless
public class UsuarioRepository extends AbstractCrudRepository {

	public void inserir(Usuario usuario) {
		em.persist(usuario);
	}

	public void atualizar(Usuario usuario) {
		em.merge(usuario);
	}

	public void remover(int id) {
		Usuario usuario = consultar(id);
		if (usuario != null) {
			em.remove(usuario);
		}
	}

	public Usuario consultar(int id) {
		return em.find(Usuario.class, id);
	}

	public List<Usuario> listarTodos() {
		return pesquisar(new UsuarioSeletor());
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> pesquisar(UsuarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT u FROM Usuario u");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(UsuarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder("SELECT COUNT(u) FROM Usuario u");
		adicionarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		adicionarParametros(query, seletor);

		return (Long) query.getSingleResult();
	}

	private void adicionarFiltros(StringBuilder sql, UsuarioSeletor seletor) {
		if (seletor.possuiFiltro()) {
			sql.append(" WHERE ");

			boolean primeiroFiltro = true;
			if (seletor.getId() != null) {
				primeiroFiltro = false;
				sql.append("u.id = :id ");
			}
			if (!StringUtils.isBlank(seletor.getNome())) {
				if (!primeiroFiltro) {
					sql.append("AND ");
				}
				sql.append("u.nome LIKE :nome ");
			}
		}

		if (seletor.possuiPaginacao()) {
			// Por padrão, ordena pelo id na paginação.
			sql.append(" ORDER BY u.id ");
		}
	}

	private void adicionarParametros(Query query, UsuarioSeletor seletor) {
		if (seletor.possuiFiltro()) {
			if (seletor.getId() != null) {
				query.setParameter("id", seletor.getId());
			}

			if (!StringUtils.isBlank(seletor.getNome())) {
				query.setParameter("nome", String.format("%%%s%%", seletor.getNome()));
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
