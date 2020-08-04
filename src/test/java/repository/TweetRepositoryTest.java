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

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TweetRepositoryTest {

	private TweetRepository tweetRepository;
	private UsuarioRepository usuarioRepository;
	private Connection connection;

	@Before
	public void setUp() throws SQLException {
		this.usuarioRepository = new UsuarioRepository();
		this.tweetRepository = new TweetRepository();

		this.connection = DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_test", "postgres", "postgres");
	}

	@After
	public void tearDown() throws SQLException {
		try (Statement st = this.connection.createStatement()) {
			st.addBatch("delete from tweet");
			st.addBatch("delete from usuario");
			st.addBatch("alter sequence seq_usuario restart");
			st.addBatch("alter sequence seq_tweet restart");
			st.executeBatch();
		} finally {
			this.connection.close();
		}
	}

	@Test
	public void testa_inserir_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = inserirTweetDeTeste();

		assertThat(tweet.getId()).isGreaterThan(0);
	}

	@Test
	public void testa_atualizar_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = inserirTweetDeTeste();

		tweet.setConteudo("Olá, mundo!");
		this.tweetRepository.atualizar(tweet);

		// TODO: é necessário implementar o método consultar para usar aqui!
		// TODO: consultar o tweet atualizado e verificar se os campos foram atualizados.
	}

	private Tweet inserirTweetDeTeste() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		// Primeiro criamos e inserimos o autor do tweet
		Usuario usuario = new Usuario();
		usuario.setNome("João");
		this.usuarioRepository.inserir(usuario);

		assertThat(usuario.getId()).isGreaterThan(0);

		// Depois criamos e inserimos o Tweet
		Tweet tweet = new Tweet();
		tweet.setConteudo("Hello World!");
		tweet.setDataCriacao(Instant.now());
		tweet.setUsuario(usuario);
		this.tweetRepository.inserir(tweet);

		return tweet;
	}

}
