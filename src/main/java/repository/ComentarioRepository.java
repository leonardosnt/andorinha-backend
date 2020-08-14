package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TemporalType;

import model.Comentario;
import model.dto.ComentarioDTO;
import model.seletor.ComentarioSeletor;
import repository.base.EntityQuery;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository<Comentario> {

	@Override
	public List<Comentario> listarTodos() {
		return pesquisar(new ComentarioSeletor());
	}

	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		return super.createEntityQuery()
				.innerJoinFetch("usuario")
				.innerJoinFetch("tweet")
				//.innerJoinFetch("tweet.usuario") // TODO: isso não funciona.
				.apply(this::aplicaFiltros, seletor)
				.list();
	}

	public List<ComentarioDTO> pesquisarDTO(ComentarioSeletor seletor) {
		return super.createTupleQuery()
				.select("id", "tweet.id as idTweet", "usuario.id as idUsuario", "usuario.nome as nomeUsuario", "data", "conteudo")
				.join("usuario")
				.join("tweet")

				// TODO: Não dá para usar o aplicaFiltro porque TupleQuery e EntityQuery são
				// tipos diferentes. Seria bom ter uma interface que ambos compartilhassem...
				.equal("id", seletor.getId())
				.equal("tweet.id", seletor.getIdTweet())
				.equal("usuario.id", seletor.getIdUsuario())
				.like("conteudo", seletor.getConteudo())
				.equal("data", seletor.getData(), TemporalType.DATE)
				.setFirstResult(seletor.getOffset())
				.setMaxResults(seletor.getLimite())
				.addOrderBy("asc", seletor.possuiPaginacao() ? "id" : null) // Por padrão, ordena pelo id ao usar paginação.

				.list(ComentarioDTO.class);
	}

	public long contar(ComentarioSeletor seletor) {
		return super.createCountQuery().apply(this::aplicaFiltros, seletor).count();
	}

	private void aplicaFiltros(EntityQuery<Comentario> query, ComentarioSeletor seletor) {
		query.equal("id", seletor.getId())
			.equal("tweet.id", seletor.getIdTweet())
			.equal("usuario.id", seletor.getIdUsuario())
			.like("conteudo", seletor.getConteudo())
			.equal("data", seletor.getData(), TemporalType.DATE)
			.setFirstResult(seletor.getOffset())
			.setMaxResults(seletor.getLimite())
			.addOrderBy("asc", seletor.possuiPaginacao() ? "id" : null); // Por padrão, ordena pelo id ao usar paginação.
	}
}
