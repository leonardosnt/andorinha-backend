package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TemporalType;

import model.Comentario;
import model.dto.ComentarioDTO;
import model.seletor.ComentarioSeletor;
import repository.base.BaseQuery;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository<Comentario> {

	@Override
	public List<Comentario> listarTodos() {
		return pesquisar(new ComentarioSeletor());
	}

	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		return super.createEntityQuery()
				.innerJoinFetch("usuario")
				.innerJoinFetch("tweet.usuario")
				.apply(this::aplicaFiltros, seletor)
				.list();
	}

	public List<ComentarioDTO> pesquisarDTO(ComentarioSeletor seletor) {
		return super.createTupleQuery()
				.select("id", "tweet.id as idTweet", "usuario.id as idUsuario", "usuario.nome as nomeUsuario", "data", "conteudo")
				.join("usuario")
				.join("tweet")
				.apply(this::aplicaFiltros, seletor)
				.list(ComentarioDTO.class);
	}

	public long contar(ComentarioSeletor seletor) {
		return super.createCountQuery().apply(this::aplicaFiltros, seletor).count();
	}

	private void aplicaFiltros(BaseQuery<Comentario> query, ComentarioSeletor seletor) {
		query.equal("id", seletor.getId())
			.equal("tweet.id", seletor.getIdTweet())
			.equal("usuario.id", seletor.getIdUsuario())
			.like("conteudo", seletor.getConteudo())
			.equal("data", seletor.getData(), TemporalType.DATE)
			.setFirstResult(seletor.getOffset())
			.setMaxResults(seletor.getLimite())
			.addOrderBy(seletor.getOrderType(), seletor.getOrderField());
	}
}
