package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TemporalType;

import model.Tweet;
import model.dto.TweetDTO;
import model.seletor.TweetSeletor;
import repository.base.EntityQuery;

@Stateless
public class TweetRepository extends AbstractCrudRepository<Tweet> {

	@Override
	public List<Tweet> listarTodos() {
		return pesquisar(new TweetSeletor());
	}

	public List<Tweet> pesquisar(TweetSeletor seletor) {
		return super.createEntityQuery()
			.innerJoinFetch("usuario")
			.apply(this::aplicaFiltros, seletor)
			.list();
	}

	public List<TweetDTO> pesquisarDTO(TweetSeletor seletor) {
		return super.createTupleQuery()
				.select("id", "usuario.id as idUsuario", "usuario.nome as nomeUsuario", "data", "conteudo")
				.join("usuario")

				// TODO: Não dá para usar o aplicaFiltro porque TupleQuery e EntityQuery são
				// tipos diferentes. Seria bom ter uma interface que ambos compartilhassem...
				.equal("id", seletor.getId())
				.equal("usuario.id", seletor.getIdUsuario())
				.like("conteudo", seletor.getConteudo())
				.equal("data", seletor.getData(), TemporalType.DATE)
				.setFirstResult(seletor.getOffset())
				.setMaxResults(seletor.getLimite())
				.addOrderBy("asc", seletor.possuiPaginacao() ? "id" : null) // Por padrão, ordena pelo id ao usar paginação.

				.list(TweetDTO.class);
	}

	public long contar(TweetSeletor seletor) {
		return super.createCountQuery().apply(this::aplicaFiltros, seletor).count();
	}

	private void aplicaFiltros(EntityQuery<Tweet> query, TweetSeletor seletor) {
		query.equal("id", seletor.getId())
			.equal("usuario.id", seletor.getIdUsuario())
			.like("conteudo", seletor.getConteudo())
			.equal("data", seletor.getData(), TemporalType.DATE)
			.setFirstResult(seletor.getOffset())
			.setMaxResults(seletor.getLimite())
			.addOrderBy("asc", seletor.possuiPaginacao() ? "id" : null); // Por padrão, ordena pelo id ao usar paginação.
	}
}
