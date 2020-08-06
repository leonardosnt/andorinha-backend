package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestTweetRepository {

	private TweetRepository tweetRepository;
	private UsuarioRepository usuarioRepository;

	@Before
	public void setUp() throws SQLException {
		DatabaseHelper.getInstance("andorinhaDS").executeSqlScript("sql/prepare-database.sql");

		this.usuarioRepository = new UsuarioRepository();
		this.tweetRepository = new TweetRepository();
	}

	@Test
	public void testa_inserir_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = inserirTweetDeTeste();

		assertThat(tweet.getId()).isGreaterThan(0);
	}

	@Test
	public void testa_consultar_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweetInserido = inserirTweetDeTeste();

		Tweet tweetConsulta = this.tweetRepository.consultar(tweetInserido.getId());

		assertThat(tweetConsulta).isNotNull();
		assertThat(tweetConsulta).isEqualTo(tweetInserido);
	}

	@Test
	public void testa_atualizar_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweetInserido = inserirTweetDeTeste();
		tweetInserido.setConteudo("Olá, mundo!");

		this.tweetRepository.atualizar(tweetInserido);

		Tweet tweetAtualizado = this.tweetRepository.consultar(tweetInserido.getId());
		assertThat(tweetAtualizado).isEqualTo(tweetInserido);
	}

	@Test
	public void testa_remover_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweetInserido = inserirTweetDeTeste();

		assertThat(tweetInserido.getId()).isGreaterThan(0);

		this.tweetRepository.remover(tweetInserido.getId());

		Tweet tweetRemovido = this.tweetRepository.consultar(tweetInserido.getId());
		assertThat(tweetRemovido).isNull();
	}

	@Test
	public void testa_listar_todos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario u0 = inserirUsuarioDeTeste("Usuario 1");
		Usuario u1 = inserirUsuarioDeTeste("Usuario 2");

		List<Tweet> tweetsEsperados = new ArrayList<>();
		tweetsEsperados.add(inserirTweetDeTeste(u0, "Hello World"));
		tweetsEsperados.add(inserirTweetDeTeste(u1, "Hello World"));

		List<Tweet> todosTweets = this.tweetRepository.listarTodos();
		assertThat(todosTweets).containsExactlyInAnyOrderElementsOf(tweetsEsperados);
	}

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
		tweet.setData(Instant.now());
		tweet.setUsuario(usuario);
		this.tweetRepository.inserir(tweet);

		return tweet;
	}

	private Tweet inserirTweetDeTeste() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario usuario = inserirUsuarioDeTeste("Usuário Teste");
		return inserirTweetDeTeste(usuario, "Conteúdo teste");
	}

}
