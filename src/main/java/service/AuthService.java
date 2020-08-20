package service;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.Usuario;
import model.dto.AuthDTO;
import repository.JwtRepository;
import repository.UsuarioRepository;

@Path("/auth")
public class AuthService {

	@EJB
	UsuarioRepository usuarioRepository;

	@Inject
	JwtRepository jwtRepository;

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(AuthDTO authDTO) {
		Usuario user = this.usuarioRepository.login(authDTO.getUsuario(), authDTO.getSenha());
		if (user != null) {

			Calendar expiracao = Calendar.getInstance();
			expiracao.add(Calendar.HOUR_OF_DAY, 3);

			String jwt = this.jwtRepository.generateToken(user, expiracao.getTime());

			return Response.ok(user).header("x-token", jwt).build();
		} else {
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

}