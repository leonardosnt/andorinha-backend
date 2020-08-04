package repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TestUsuarioRepository {

	private UsuarioRepository usuarioRepository;

	@Before
	public void setUp() {
		this.usuarioRepository = new UsuarioRepository();
	}

	@Test
	public void testa_se_usuario_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = new Usuario();
		user.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(user);

		Usuario inserido = this.usuarioRepository.consultar(user.getId());

		assertThat( user.getId() ).isGreaterThan(0);

		assertThat( inserido ).isNotNull();
		assertThat( inserido.getNome() ).isEqualTo(user.getNome());
		assertThat( inserido.getId() ).isEqualTo(user.getId());
	}

	@Test
	public void testa_consultar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		int idConsulta = 6;
		Usuario user = this.usuarioRepository.consultar(idConsulta);

		assertThat( user ).isNotNull();
		assertThat( user.getNome() ).isEqualTo("Usuario do Teste de Unidade");
		assertThat( user.getId() ).isEqualTo(idConsulta);
	}

	@Test
	public void testa_atualizar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		int idConsulta = 5;
		Usuario usuario = this.usuarioRepository.consultar(idConsulta);

		assertThat(usuario).isNotNull();

		usuario.setNome("Nome Alterado");

		this.usuarioRepository.atualizar(usuario);

		Usuario alterado = this.usuarioRepository.consultar(idConsulta);

		assertThat(alterado.getNome()).isEqualTo("Nome Alterado");
	}

	@Test
	public void testa_remover_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome("Usuario Teste");
		this.usuarioRepository.inserir(novoUsuario);

		assertThat(novoUsuario.getId()).isGreaterThan(0);

		this.usuarioRepository.remover(novoUsuario.getId());

		Usuario usuarioRemovido = this.usuarioRepository.consultar(novoUsuario.getId());
		assertThat(usuarioRemovido).isNull();
	}

}
