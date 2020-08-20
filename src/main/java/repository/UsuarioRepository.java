package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import model.Usuario;
import model.seletor.UsuarioSeletor;
import repository.base.BaseQuery;

@Stateless
public class UsuarioRepository extends AbstractCrudRepository<Usuario> {

	public Usuario login(String usuario, String senha) {
		try {
			return super.createEntityQuery()
					.equal("login", usuario)
					.equal("senha", senha)
					.uniqueResult();
		}
		catch (NoResultException ex) {
			return null;
		}
	}

	public List<Usuario> pesquisar(UsuarioSeletor seletor) {
		return super.createEntityQuery().apply(this::aplicaFiltros, seletor).list();
	}

	public long contar(UsuarioSeletor seletor) {
		return super.createCountQuery().apply(this::aplicaFiltros, seletor).count();
	}

	private void aplicaFiltros(BaseQuery<Usuario> query, UsuarioSeletor seletor) {
		query.equal("id", seletor.getId())
			.like("nome", seletor.getNome())
			.setFirstResult(seletor.getOffset())
			.setMaxResults(seletor.getLimite())
			.addOrderBy(seletor.getOrderType(), seletor.getOrderField());
	}

}