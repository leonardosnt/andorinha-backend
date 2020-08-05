package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TestComentarioRepository {

	private TweetRepository tweetRepository;
	private UsuarioRepository usuarioRepository;
	private ComentarioRepository comentarioRepository;
	private Connection connection;

	@Before
	public void setUp() throws SQLException {
		this.usuarioRepository = new UsuarioRepository();
		this.tweetRepository = new TweetRepository();
		this.comentarioRepository = new ComentarioRepository();

		this.connection = DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_test", "postgres", "postgres");
	}

	@After
	public void tearDown() throws SQLException {
		try (Statement st = this.connection.createStatement()) {
			st.addBatch("delete from comentario");
			st.addBatch("delete from tweet");
			st.addBatch("delete from usuario");
			st.addBatch("alter sequence seq_usuario restart");
			st.addBatch("alter sequence seq_tweet restart");
			st.addBatch("alter sequence seq_comentario restart");
			st.executeBatch();
		} finally {
			this.connection.close();
		}
	}

	@Test
	public void testa_inserir_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario comentario = inserirComentarioDeTeste();

		assertThat(comentario.getId()).isGreaterThan(0);
	}

	@Test
	public void testa_consultar_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario autorTweet = inserirUsuarioDeTeste("Autor Tweet");
		Usuario autorComentario = inserirUsuarioDeTeste("Autor Comentário");

		Tweet tweet = inserirTweetDeTeste(autorTweet, "Hello World");

		Comentario comentario = new Comentario();
		comentario.setConteudo("Teste");
		comentario.setDataCriacao(Instant.now());
		comentario.setUsuario(autorComentario);
		comentario.setTweet(tweet);

		this.comentarioRepository.inserir(comentario);

		assertThat(comentario.getId()).isGreaterThan(0);

		Comentario comentarioConsulta = this.comentarioRepository.consultar(comentario.getId());
		assertThat(comentarioConsulta).isNotNull();
		assertThat(comentarioConsulta.getId()).isEqualTo(comentario.getId());
		assertThat(comentarioConsulta.getUsuario()).isEqualTo(autorComentario);

		assertThat(comentarioConsulta.getTweet()).isEqualTo(tweet);
		assertThat(comentarioConsulta.getTweet().getUsuario()).isEqualTo(autorTweet);

		// Só isso já basta para comparar tudo já que todos os models implementam o equals,
		// porém o feedback, caso não sejam iguals, não é muito bom, então achei melhor deixar
		// os outros asserts também.
		assertThat(comentarioConsulta.getTweet()).isEqualTo(tweet);
	}

	@Test
	public void testa_atualizar_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario comentario = inserirComentarioDeTeste();

		assertThat(comentario.getId()).isGreaterThan(0);

		Usuario novoUsuario = inserirUsuarioDeTeste("Alterado");
		Tweet novoTweet = inserirTweetDeTeste();
		Instant dataAlterada = Instant.now().minusSeconds(100);

		comentario.setConteudo("Conteúdo alterado!");
		comentario.setDataCriacao(dataAlterada);
		comentario.setTweet(novoTweet);
		comentario.setUsuario(novoUsuario);

		this.comentarioRepository.atualizar(comentario);

		Comentario alterado = this.comentarioRepository.consultar(comentario.getId());

		assertThat(alterado.getConteudo()).isEqualTo("Conteúdo alterado!");
		assertThat(alterado.getTweet()).isEqualTo(novoTweet);
		assertThat(alterado.getUsuario()).isEqualTo(novoUsuario);
		assertThat(alterado.getDataCriacao()).isEqualTo(dataAlterada);
	}

	@Test
	public void testa_remover_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario comentario = inserirComentarioDeTeste();

		assertThat(comentario.getId()).isGreaterThan(0);

		this.comentarioRepository.remover(comentario.getId());

		Comentario removido = this.comentarioRepository.consultar(comentario.getId());

		assertThat(removido).isNull();
	}

	@Test
	public void testa_listar_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
	}

	private Comentario inserirComentarioDeTeste() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = inserirTweetDeTeste();
		Usuario autor = inserirUsuarioDeTeste("Teste");

		Comentario comentario = new Comentario();
		comentario.setConteudo("Teste");
		comentario.setDataCriacao(Instant.now());
		comentario.setUsuario(autor);
		comentario.setTweet(tweet);

		this.comentarioRepository.inserir(comentario);

		return comentario;
	}

	// TODO: código duplicado do TestTweetRepository
	private Usuario inserirUsuarioDeTeste(String nome) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		this.usuarioRepository.inserir(usuario);

		assertThat(usuario.getId()).isGreaterThan(0);

		return usuario;
	}

	private Tweet inserirTweetDeTeste(Usuario usuario, String conteudo) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = new Tweet();
		tweet.setConteudo("Hello World!");
		tweet.setDataCriacao(Instant.now());
		tweet.setUsuario(usuario);
		this.tweetRepository.inserir(tweet);

		return tweet;
	}

	private Tweet inserirTweetDeTeste() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario usuario = inserirUsuarioDeTeste("Usuário Teste");
		return inserirTweetDeTeste(usuario, "Conteúdo teste");
	}

}
