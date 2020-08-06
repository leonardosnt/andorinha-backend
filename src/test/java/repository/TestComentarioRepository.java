package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestComentarioRepository {

	private TweetRepository tweetRepository;
	private UsuarioRepository usuarioRepository;
	private ComentarioRepository comentarioRepository;

	@Before
	public void setUp() throws SQLException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/andorinha.xml", DatabaseOperation.CLEAN_INSERT);

		this.usuarioRepository = new UsuarioRepository();
		this.tweetRepository = new TweetRepository();
		this.comentarioRepository = new ComentarioRepository();
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
		comentario.setData(Instant.now());
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
		comentario.setData(dataAlterada);
		comentario.setTweet(novoTweet);
		comentario.setUsuario(novoUsuario);

		this.comentarioRepository.atualizar(comentario);

		Comentario alterado = this.comentarioRepository.consultar(comentario.getId());

		assertThat(alterado.getConteudo()).isEqualTo("Conteúdo alterado!");
		assertThat(alterado.getTweet()).isEqualTo(novoTweet);
		assertThat(alterado.getUsuario()).isEqualTo(novoUsuario);
		assertThat(alterado.getData()).isEqualTo(dataAlterada);
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
		Tweet tweet1 = inserirTweetDeTeste();
		Tweet tweet2 = inserirTweetDeTeste();

		Usuario autor1 = inserirUsuarioDeTeste("Autor 1");
		Usuario autor2 = inserirUsuarioDeTeste("Autor 2");

		List<Comentario> esperados = new ArrayList<>();

		esperados.add(inserirComentarioDeTeste(autor1, tweet1, "Teste comentário 1"));
		esperados.add(inserirComentarioDeTeste(autor1, tweet2, "Teste comentário 2"));

		esperados.add(inserirComentarioDeTeste(autor2, tweet1, "Teste comentário 3"));
		esperados.add(inserirComentarioDeTeste(autor2, tweet2, "Teste comentário 4"));

		List<Comentario> todos = this.comentarioRepository.listarTodos();

		assertThat(todos).containsExactlyInAnyOrderElementsOf(esperados);
	}

	private Comentario inserirComentarioDeTeste(Usuario autor, Tweet tweet, String conteudo) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario comentario = new Comentario();
		comentario.setConteudo(conteudo);
		comentario.setData(Instant.now());
		comentario.setUsuario(autor);
		comentario.setTweet(tweet);

		this.comentarioRepository.inserir(comentario);

		return comentario;
	}

	private Comentario inserirComentarioDeTeste() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = inserirTweetDeTeste();
		Usuario autor = inserirUsuarioDeTeste("Teste");
		return inserirComentarioDeTeste(autor, tweet, "Teste conteúdo");
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
