package service;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.Comentario;
import model.seletor.ComentarioSeletor;
import repository.ComentarioRepository;

@Path("/comentario")
public class ComentarioService {

	@EJB
	private ComentarioRepository comentarioRepository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> listarTodos() {
		return comentarioRepository.listarTodos();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Comentario inserir(Comentario comentario) {
		comentarioRepository.inserir(comentario);
		return comentario;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comentario consultar(@PathParam("id") Integer id) {
		return comentarioRepository.consultar(id);
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remover(@PathParam("id") Integer id) {
		comentarioRepository.remover(id);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void atualizar(Comentario comentario) {
		comentarioRepository.atualizar(comentario);
	}

	@POST
	@Path("/pesquisar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		return comentarioRepository.pesquisar(seletor);
	}

}
