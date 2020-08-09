package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.TweetSeletor;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestTweetRepository {

	private static final int ID_TWEET_CONSULTA = 1;
	private static final int ID_USUARIO_CONSULTA = 1;

	private static final long DELTA_MILIS = 500;

	@EJB
	private UsuarioRepository usuarioRepository;

	@EJB
	private TweetRepository tweetRepository;

	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/andorinha.xml", DatabaseOperation.CLEAN_INSERT);
	}

	@Test
	public void testa_se_tweet_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);

		Tweet tweet = new Tweet();
		tweet.setConteudo("Minha postagem de teste");
		tweet.setUsuario(user);

		this.tweetRepository.inserir(tweet);

		assertThat( tweet.getId() ).isGreaterThan(0);

		Tweet inserido = this.tweetRepository.consultar(tweet.getId());

		assertThat( inserido ).isNotNull();
		assertThat( inserido.getConteudo() ).isEqualTo(tweet.getConteudo());
		assertThat( Calendar.getInstance().getTime() )
			.isCloseTo(inserido.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_consultar_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);

		assertThat( tweet ).isNotNull();
		assertThat( tweet.getConteudo() ).isEqualTo("Minha postagem de teste");
		assertThat( tweet.getId() ).isEqualTo(ID_TWEET_CONSULTA);
		assertThat( tweet.getUsuario() ).isNotNull();
	}

	@Test
	public void testa_alterar_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		tweet.setConteudo("Alterado!");

		this.tweetRepository.atualizar(tweet);

		Tweet alterado = this.tweetRepository.consultar(ID_TWEET_CONSULTA);

		assertThat( alterado.getConteudo() ).isEqualTo(tweet.getConteudo());
		assertThat( Calendar.getInstance().getTime() )
			.isCloseTo(alterado.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_remover_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		assertThat( tweet ).isNotNull();

		this.tweetRepository.remover(ID_TWEET_CONSULTA);

		Tweet removido = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		assertThat( removido ).isNull();
	}

	@Test
	public void testa_listar_todos_os_tweets() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Tweet> tweets = this.tweetRepository.listarTodos();

		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(3)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste",
														"Minha postagem de teste 2",
														"Minha postagem de teste 3");

		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_pesquisar_tweets() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setConteudo("Minha postagem de teste");
		seletor.setIdUsuario(1);
		seletor.setData(new GregorianCalendar(2020, 3, 8));

		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);

		assertThat(tweets).isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.extracting("conteudo")
			.containsExactly("Minha postagem de teste");

		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_pesquisar_tweets_por_usuario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setIdUsuario(2);

		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);

		assertThat(tweets).isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.extracting("conteudo")
			.containsExactly("Minha postagem de teste 2");

		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_pesquisar_tweets_por_conteudo() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setConteudo("Minha postagem");

		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);

		assertThat(tweets).isNotNull()
			.isNotEmpty()
			.hasSize(3)
			.extracting("conteudo")
			.containsExactlyInAnyOrder("Minha postagem de teste",
					"Minha postagem de teste 2",
					"Minha postagem de teste 3");

		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_contar_tweets() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setConteudo("Minha postagem");
		assertThat(this.tweetRepository.contar(seletor)).isEqualTo(3);

		seletor = new TweetSeletor();
		seletor.setId(1);
		assertThat(this.tweetRepository.contar(seletor)).isEqualTo(1);

		seletor = new TweetSeletor();
		seletor.setIdUsuario(3);
		assertThat(this.tweetRepository.contar(seletor)).isEqualTo(1);

		seletor = new TweetSeletor();
		seletor.setIdUsuario(2);
		seletor.setConteudo("Minha postagem de teste 2");
		seletor.setData(new GregorianCalendar(2020, 4, 9));

		assertThat(this.tweetRepository.contar(seletor)).isEqualTo(1);

		seletor = new TweetSeletor();
		seletor.setData(new GregorianCalendar(2020, 10, 20));

		assertThat(this.tweetRepository.contar(seletor)).isEqualTo(0);
	}

	@Test
	public void testa_paginacao() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		// Teste básico
		TweetSeletor seletor = new TweetSeletor();
		seletor.setLimite(5);
		seletor.setPagina(1);

		assertThat(seletor.possuiPaginacao());

		assertThat(this.tweetRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(5)
			.extracting("id")
			.containsExactlyInAnyOrder(1, 2, 3, 4, 5);

		seletor.setPagina(2);

		assertThat(this.tweetRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(5)
			.extracting("id")
			.containsExactlyInAnyOrder(6, 7, 8, 9, 10);


		// Página "não existe"
		seletor = new TweetSeletor();
		seletor.setLimite(10);
		seletor.setPagina(100);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.tweetRepository.pesquisar(seletor)).isNotNull().hasSize(0);

		// Página com menos items que o limite
		seletor = new TweetSeletor();
		seletor.setLimite(6);
		seletor.setPagina(2);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.tweetRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(4);
	}

	@Test
	public void testa_paginacao_com_filtro() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		TweetSeletor seletor = new TweetSeletor();
		seletor.setLimite(2);
		seletor.setPagina(1);

		// Este usuário possui 3 tweets
		seletor.setIdUsuario(1);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.tweetRepository.pesquisar(seletor))
			.isNotNull().hasSize(2).extracting("id").containsExactlyInAnyOrder(1, 9);

		seletor.setPagina(2);

		assertThat(this.tweetRepository.pesquisar(seletor))
			.isNotNull().hasSize(1).extracting("id").containsExactlyInAnyOrder(10);
	}
}