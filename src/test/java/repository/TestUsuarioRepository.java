package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestUsuarioRepository {

	private UsuarioRepository usuarioRepository;

	@Before
	public void setUp() throws SQLException {
		DatabaseHelper.getInstance("andorinhaDS").executeSqlScript("sql/prepare-database.sql");

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
		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(novoUsuario);

		assertThat( novoUsuario.getId() ).isGreaterThan( 0 );

		Usuario usuarioConsultado = this.usuarioRepository.consultar(novoUsuario.getId());

		assertThat( usuarioConsultado ).isNotNull();
		assertThat( usuarioConsultado.getNome() ).isEqualTo("Usuario do Teste de Unidade");
		assertThat( usuarioConsultado.getId() ).isEqualTo(novoUsuario.getId());
	}

	@Test
	public void testa_atualizar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		// Insere um novo usuário
		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(novoUsuario);

		assertThat( novoUsuario.getId() ).isGreaterThan( 0 );

		// Atualiza o nome do usuário inserido
		novoUsuario.setNome("Nome Alterado");

		this.usuarioRepository.atualizar(novoUsuario);

		// Verifica se a alteração foi bem sucedida
		Usuario alterado = this.usuarioRepository.consultar(novoUsuario.getId());

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

	@Test
	public void testa_listar_todos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		int numUsuariosTeste = 3;
		List<Usuario> usuariosEsperados = new ArrayList<>();

		for (int i = 0; i < numUsuariosTeste; i++) {
			Usuario novoUsuario = new Usuario();
			novoUsuario.setNome("Usuario Teste #" + i);
			this.usuarioRepository.inserir(novoUsuario);

			usuariosEsperados.add(novoUsuario);
		}

		List<Usuario> usuarios = this.usuarioRepository.listarTodos();

		assertThat(usuarios.size()).isEqualTo(numUsuariosTeste);
		assertThat(usuarios).containsExactlyInAnyOrderElementsOf(usuariosEsperados);
	}

}
