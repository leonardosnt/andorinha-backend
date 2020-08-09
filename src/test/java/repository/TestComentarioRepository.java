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

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.ComentarioSeletor;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestComentarioRepository {

	private static final int ID_TWEET_CONSULTA = 1;
	private static final int ID_COMENTARIO_CONSULTA = 1;
	private static final int ID_USUARIO_CONSULTA = 1;

	private static final long DELTA_MILIS = 500;

	@EJB
	private UsuarioRepository usuarioRepository;

	@EJB
	private TweetRepository tweetRepository;

	@EJB
	private ComentarioRepository comentarioRepository;

	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/andorinha.xml", DatabaseOperation.CLEAN_INSERT);
	}

	@Test
	public void testa_se_comentario_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);

		Comentario c = new Comentario();
		c.setConteudo("Meu comentário de teste");
		c.setUsuario(user);
		c.setTweet(tweet);

		this.comentarioRepository.inserir(c);

		assertThat( c.getId() ).isGreaterThan(0);

		Comentario inserido = this.comentarioRepository.consultar(c.getId());

		assertThat( inserido ).isNotNull();
		assertThat( inserido.getConteudo() ).isEqualTo(c.getConteudo());
		assertThat( Calendar.getInstance().getTime() )
			.isCloseTo(inserido.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_consultar_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario c =  this.comentarioRepository.consultar(ID_COMENTARIO_CONSULTA);

		assertThat( c ).isNotNull();
		assertThat( c.getConteudo() ).isEqualTo("Comentário 1");
		assertThat( c.getId() ).isEqualTo(ID_COMENTARIO_CONSULTA);
		assertThat( c.getUsuario() ).isNotNull();
		assertThat( c.getTweet() ).isNotNull();
		assertThat( c.getTweet().getUsuario() ).isNotNull();
	}

	@Test
	public void testa_alterar_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario c =  this.comentarioRepository.consultar(ID_COMENTARIO_CONSULTA);
		c.setConteudo("Alterado!");

		this.comentarioRepository.atualizar(c);

		Comentario alterado = this.comentarioRepository.consultar(ID_COMENTARIO_CONSULTA);

		assertThat( alterado.getConteudo() ).isEqualTo(c.getConteudo());
		assertThat( Calendar.getInstance().getTime() )
			.isCloseTo(alterado.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_remover_comentario() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Comentario c =  this.comentarioRepository.consultar(ID_COMENTARIO_CONSULTA);
		assertThat( c ).isNotNull();

		this.comentarioRepository.remover(ID_COMENTARIO_CONSULTA);

		Comentario removido =  this.comentarioRepository.consultar(ID_COMENTARIO_CONSULTA);
		assertThat( removido ).isNull();
	}

	@Test
	public void testa_listar_todos_os_comentarios() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Comentario> comentarios = this.comentarioRepository.listarTodos();

		assertThat( comentarios ).isNotNull()
							.isNotEmpty()
							.hasSize(10)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Comentário 1", "Comentário 2", "Comentário 3", "Comentário 4", "Comentário 5",
														"Comentário 6", "Comentário 7", "Comentário 8", "Comentário 9", "Comentário 10");

		comentarios.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
			assertThat(t.getTweet()).isNotNull();
			assertThat(t.getTweet().getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_pesquisar_comentarios_filtrado_por_tweet() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {

		ComentarioSeletor seletor = new ComentarioSeletor();
		seletor.setIdTweet( 2 );
		seletor.setIdUsuario( 1 );

		List<Comentario> comentarios = this.comentarioRepository.pesquisar( seletor );

		assertThat( comentarios ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("conteudo")
							.containsExactly("Comentário 5");

		comentarios.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
			assertThat(t.getTweet()).isNotNull();
			assertThat(t.getTweet().getUsuario()).isNotNull();
		});
	}

	@Test
	public void testa_contar_comentarios() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		{
			ComentarioSeletor seletor = new ComentarioSeletor();
			seletor.setIdTweet(2);

			assertThat(this.comentarioRepository.contar(seletor)).isEqualTo(2);
		}

		{
			ComentarioSeletor seletor = new ComentarioSeletor();
			seletor.setIdUsuario(1);

			assertThat(this.comentarioRepository.contar(seletor)).isEqualTo(2);
		}

		{
			ComentarioSeletor seletor = new ComentarioSeletor();
			seletor.setIdUsuario(1);
			seletor.setConteudo("Comentário");

			assertThat(this.comentarioRepository.contar(seletor)).isEqualTo(2);
		}

		{
			ComentarioSeletor seletor = new ComentarioSeletor();
			seletor.setIdUsuario(4);
			seletor.setIdTweet(1);
			seletor.setConteudo("Comentário 1" );
			seletor.setData(new GregorianCalendar(2020, 5, 1));

			long total = this.comentarioRepository.contar(seletor);
			assertThat(total).isEqualTo(1);

			seletor.setData(new GregorianCalendar(2020, 5, 5));

			assertThat(this.comentarioRepository.contar(seletor)).isEqualTo(0);
		}
	}

	@Test
	public void testa_paginacao() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		// Teste básico
		ComentarioSeletor seletor = new ComentarioSeletor();
		seletor.setLimite(5);
		seletor.setPagina(1);

		assertThat(seletor.possuiPaginacao());

		assertThat(this.comentarioRepository.pesquisar(seletor)).isNotNull().hasSize(5);

		seletor.setPagina(2);

		assertThat(this.comentarioRepository.pesquisar(seletor)).isNotNull().hasSize(5);

		// Página "não existe"
		seletor = new ComentarioSeletor();
		seletor.setLimite(10);
		seletor.setPagina(100);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.comentarioRepository.pesquisar(seletor)).isNotNull().hasSize(0);

		// Página com menos items que o limite
		seletor = new ComentarioSeletor();
		seletor.setLimite(6);
		seletor.setPagina(2);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.comentarioRepository.pesquisar(seletor))
			.isNotNull()
			.hasSize(4);
	}

	@Test
	public void testa_paginacao_com_filtro() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/paginacao.xml", DatabaseOperation.CLEAN_INSERT);

		ComentarioSeletor seletor = new ComentarioSeletor();
		seletor.setLimite(2);
		seletor.setPagina(1);

		// Este tweet possui 4 comentarios
		seletor.setIdTweet(3);

		assertThat(seletor.possuiPaginacao());
		assertThat(this.comentarioRepository.pesquisar(seletor)).isNotNull().hasSize(2);

		seletor.setPagina(2);

		assertThat(this.comentarioRepository.pesquisar(seletor)).isNotNull().hasSize(2);
	}

}