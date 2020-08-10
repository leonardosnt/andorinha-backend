package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import javax.ejb.EJB;
import javax.transaction.RollbackException;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.UsuarioSeletor;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestUsuarioRepository {

	private static final int ID_USUARIO_CONSULTA = 1;
	private static final int ID_USUARIO_SEM_TWEET = 5;
	private static final int ID_USUARIO_INEXISTENTE = 1000;

	@EJB
	private UsuarioRepository usuarioRepository;

	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/andorinha.xml", DatabaseOperation.CLEAN_INSERT);
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
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);

		assertThat( user ).isNotNull();
		assertThat( user.getNome() ).isEqualTo("Usuário 1");
		assertThat( user.getId() ).isEqualTo(ID_USUARIO_CONSULTA);
	}

	@Test
	public void testa_alterar_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		user.setNome("Alterado!");

		this.usuarioRepository.atualizar(user);

		Usuario alterado = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);

		assertThat( alterado ).isEqualToComparingFieldByField(user);
	}

	@Test
	public void testa_remover_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_SEM_TWEET);
		assertThat( user ).isNotNull();

		this.usuarioRepository.remover(ID_USUARIO_SEM_TWEET);

		Usuario removido = this.usuarioRepository.consultar(ID_USUARIO_SEM_TWEET);
		assertThat( removido ).isNull();
	}

	@Test
	public void testa_remover_usuario_inexistente() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		assertThat(this.usuarioRepository.consultar(ID_USUARIO_INEXISTENTE)).isNull();

		this.usuarioRepository.remover(ID_USUARIO_INEXISTENTE);

		assertThat(this.usuarioRepository.consultar(ID_USUARIO_INEXISTENTE) ).isNull();
	}

	@Test
	public void testa_remover_usuario_com_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		assertThatThrownBy(() -> { this.usuarioRepository.remover(ID_USUARIO_CONSULTA); })
			.hasCauseInstanceOf(RollbackException.class);
	}

	@Test
	public void testa_listar_todos_os_usuarios() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Usuario> usuarios = this.usuarioRepository.listarTodos();

		assertThat( usuarios ).isNotNull()
							.isNotEmpty()
							.hasSize(10)
							.extracting("nome")
							.containsExactlyInAnyOrder("Usuário 1", "Usuário 2",
			                        "Usuário 3", "Usuário 4", "Usuário 5", "João", "José", "Maria", "Ana", "Joselito");
	}

	@Test
	public void testa_pesquisar_usuarios_por_nome() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		UsuarioSeletor seletor = new UsuarioSeletor();
		seletor.setNome("Jo");
		List<Usuario> usuarios = this.usuarioRepository.pesquisar(seletor);

		assertThat( usuarios ).isNotNull()
							.isNotEmpty()
							.hasSize(3)
							.extracting("nome")
							.containsExactlyInAnyOrder("João", "José", "Joselito");
	}

	@Test
	public void testa_contar_usuarios_por_nome() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		UsuarioSeletor seletor = new UsuarioSeletor();
		seletor.setNome("Usuário");
		Long total = this.usuarioRepository.contar(seletor);

		assertThat( total ).isNotNull()
							.isEqualTo(5L);
	}

	@Test
	public void testa_pesquisar_usuarios_por_id() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		UsuarioSeletor seletor = new UsuarioSeletor();
		seletor.setId(3);
		List<Usuario> usuarios = this.usuarioRepository.pesquisar(seletor);

		assertThat( usuarios ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("nome")
							.containsExactly("Usuário 3");
	}

	@Test
	public void testa_paginacao() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		// Teste básico
		UsuarioSeletor seletor = new UsuarioSeletor();
		seletor.setLimite(5);
		seletor.setPagina(1);

		assertThat(seletor.possuiPaginacao());

		assertThat(this.usuarioRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(5)
			.extracting("id")
			.containsExactly(1, 2, 3, 4, 5);

		seletor.setPagina(2);

		assertThat(this.usuarioRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(5)
			.extracting("id")
			.containsExactly(6, 7, 8, 9, 10);

		// Página "não existe"
		seletor = new UsuarioSeletor();
		seletor.setLimite(10);
		seletor.setPagina(100);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.usuarioRepository.pesquisar(seletor)).isNotNull().hasSize(0);

		// Página com menos items que o limite
		seletor = new UsuarioSeletor();
		seletor.setLimite(6);
		seletor.setPagina(2);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.usuarioRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(4);
	}

	@Test
	public void testa_paginacao_com_filtro() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		UsuarioSeletor seletor = new UsuarioSeletor();
		seletor.setLimite(5);
		seletor.setPagina(1);
		seletor.setId(7);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.usuarioRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(1)
			.extracting("id")
			.containsExactly(7);
	}

}