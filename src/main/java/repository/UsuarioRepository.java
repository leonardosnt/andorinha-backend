package repository;

import java.util.List;

import javax.ejb.Stateless;

import model.Usuario;
import model.seletor.UsuarioSeletor;
import repository.base.EntityQuery;

@Stateless
public class UsuarioRepository extends AbstractCrudRepository<Usuario> {

	public List<Usuario> pesquisar(UsuarioSeletor seletor) {
		return super.createEntityQuery().apply(this::aplicaFiltros, seletor).list();
	}

	public long contar(UsuarioSeletor seletor) {
		return super.createCountQuery().apply(this::aplicaFiltros, seletor).count();
	}

	private void aplicaFiltros(EntityQuery<Usuario> query, UsuarioSeletor seletor) {
		query.equal("id", seletor.getId())
			.like("nome", seletor.getNome())
			.setFirstResult(seletor.getOffset())
			.setMaxResults(seletor.getLimite());
	}

}