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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import model.Comentario;
import model.Usuario;
import model.dto.ComentarioDTO;
import model.seletor.ComentarioSeletor;
import repository.ComentarioRepository;

@Path("/comentario")
public class ComentarioService {

	@EJB
	private ComentarioRepository comentarioRepository;

	@Context
	private SecurityContext context;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> listarTodos() {
		return this.comentarioRepository.listarTodos();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Comentario inserir(Comentario comentario) {
		comentario.setUsuario((Usuario) context.getUserPrincipal());
		this.comentarioRepository.inserir(comentario);
		return comentario;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comentario consultar(@PathParam("id") Integer id) {
		return this.comentarioRepository.consultar(id);
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remover(@PathParam("id") Integer id) {
		this.comentarioRepository.remover(id);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void atualizar(Comentario comentario) {
		comentario.setUsuario((Usuario) context.getUserPrincipal());
		this.comentarioRepository.atualizar(comentario);
	}

	@POST
	@Path("/pesquisar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> pesquisar(ComentarioSeletor seletor) {
		return this.comentarioRepository.pesquisar(seletor);
	}

	@POST
	@Path("/dto")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ComentarioDTO> pesquisarDTO(ComentarioSeletor seletor) {
		return this.comentarioRepository.pesquisarDTO(seletor);
	}

}
