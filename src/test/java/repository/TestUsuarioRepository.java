package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;
import java.util.List;

import javax.ejb.EJB;

import org.dbunit.operation.DatabaseOperation;
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

	private static final int ID_USUARIO_CONSULTA = 1;
	private static final int ID_USUARIO_REMOVER = 2;

	@EJB
	private UsuarioRepository usuarioRepository;

	@Before
	public void setUp() throws SQLException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/usuario.xml", DatabaseOperation.CLEAN_INSERT);
	}

	@Test
	public void testa_se_usuario_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = new Usuario();
		user.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(user);

		Usuario inserido = this.usuarioRepository.consultar(user.getId());

		assertThat(user.getId()).isGreaterThan(0);

		assertThat(inserido).isNotNull();
		assertThat(inserido.getNome()).isEqualTo(user.getNome());
		assertThat(inserido.getId()).isEqualTo(user.getId());
	}

	@Test
	public void testa_consultar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario usuarioConsultado = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);

		assertThat(usuarioConsultado).isNotNull();
		assertThat(usuarioConsultado.getId()).isEqualTo(ID_USUARIO_CONSULTA);
		assertThat(usuarioConsultado.getNome()).isEqualTo("Usuário 1");
	}

	@Test
	public void testa_atualizar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		// Insere um novo usuário
		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(novoUsuario);

		assertThat(novoUsuario.getId()).isGreaterThan(0);

		// Atualiza o nome do usuário inserido
		novoUsuario.setNome("Nome Alterado");

		this.usuarioRepository.atualizar(novoUsuario);

		// Verifica se a alteração foi bem sucedida
		Usuario alterado = this.usuarioRepository.consultar(novoUsuario.getId());

		assertThat(alterado.getNome()).isEqualTo("Nome Alterado");
	}

	@Test
	public void testa_remover_usuario_com_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		assertThatThrownBy(() -> {
			this.usuarioRepository.remover(ID_USUARIO_CONSULTA);
		}).isInstanceOf(ErroAoConsultarBaseException.class)
				.hasMessageContaining("Ocorreu um erro ao remover o usuário");
	}

	@Test
	public void testa_remover_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		this.usuarioRepository.remover(ID_USUARIO_REMOVER);

		Usuario usuarioRemovido = this.usuarioRepository.consultar(ID_USUARIO_REMOVER);
		assertThat(usuarioRemovido).isNull();
	}

	@Test
	public void testa_listar_todos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Usuario> usuarios = this.usuarioRepository.listarTodos();

		assertThat(usuarios).isNotNull().hasSize(3);

		assertThat(usuarios).extracting("nome").containsExactlyInAnyOrder("Usuário 1", "Usuário 2", "Usuário 3");

		assertThat(usuarios).extracting("id").containsExactlyInAnyOrder(1, 2, 3);
	}

}
