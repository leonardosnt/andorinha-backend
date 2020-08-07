package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository {

	private static final String SELECIONAR_TODOS_COMENTARIOS_SQL =
			  "SELECT comentario.id, id_tweet, comentario.conteudo, comentario.data_postagem, "
			+ "  tweet.conteudo as conteudo_tweet, tweet.data_postagem as data_postagem_tweet, "
			+ "  autor.nome as nome_autor, autor.id as id_autor, "
			+ "  autor_tweet.nome as nome_autor_tweet, autor_tweet.id as id_autor_tweet "
			+ "  FROM comentario"
			+ " JOIN usuario as autor ON id_usuario = autor.id"
			+ " JOIN tweet ON id_tweet = tweet.id"
			+ " JOIN usuario as autor_tweet ON tweet.id_usuario = autor_tweet.id";

	private static final String SELECIONAR_COMENTARIO_SQL =
			SELECIONAR_TODOS_COMENTARIOS_SQL + " WHERE comentario.id = ?";

	public void inserir(Comentario comentario) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			int id = this.recuperarProximoValorDaSequence("seq_comentario");
			comentario.setId(id);

			String sql = "INSERT INTO comentario (id, conteudo, data_postagem, id_tweet, id_usuario) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, comentario.getId());
			ps.setString(2, comentario.getConteudo());
			ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ps.setInt(4, comentario.getTweet().getId());
			ps.setInt(5, comentario.getUsuario().getId());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o comentário", e);
		}
	}

	public void atualizar(Comentario comentario) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			String sql = "UPDATE comentario SET conteudo = ?, data_postagem = ?, id_tweet = ?, id_usuario = ? WHERE id = ?";
			PreparedStatement ps = c.prepareStatement(sql);
			ps.setString(1, comentario.getConteudo());
			ps.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ps.setInt(3, comentario.getTweet().getId());
			ps.setInt(4, comentario.getUsuario().getId());
			ps.setInt(5, comentario.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o comentário", e);
		}
	}

	public void remover(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("DELETE FROM comentario WHERE id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o comentário", e);
		}
	}

	public Comentario consultar(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			Comentario comentario = null;
			PreparedStatement ps = c.prepareStatement(SELECIONAR_COMENTARIO_SQL);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				comentario = fromResultSet(rs);
			}
			rs.close();
			ps.close();

			return comentario;
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar o comentário", e);
		}
	}

	public List<Comentario> listarTodos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Comentario> comentarios = new ArrayList<>();

		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement(SELECIONAR_TODOS_COMENTARIOS_SQL);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Comentario comentario = fromResultSet(rs);
				comentarios.add(comentario);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao listar os comentários", e);
		}

		return comentarios;
	}

	private Comentario fromResultSet(ResultSet rs) throws SQLException {
		Comentario comentario = new Comentario();
		comentario.setId(rs.getInt("id"));
		comentario.setConteudo(rs.getString("conteudo"));

		Calendar dataPostagemComentario = new GregorianCalendar();
		dataPostagemComentario.setTime(rs.getTimestamp("data_postagem"));
		comentario.setData(dataPostagemComentario);

		Usuario usuario = new Usuario();
		usuario.setId(rs.getInt("id_autor"));
		usuario.setNome(rs.getString("nome_autor"));

		comentario.setUsuario(usuario);

		Tweet tweet = new Tweet();
		tweet.setId(rs.getInt("id_tweet"));
		tweet.setConteudo(rs.getString("conteudo_tweet"));

		Calendar dataPostagemTweet = new GregorianCalendar();
		dataPostagemTweet.setTime(rs.getTimestamp("data_postagem_tweet"));
		tweet.setData(dataPostagemTweet);

		Usuario autorTweet = new Usuario();
		autorTweet.setId(rs.getInt("id_autor_tweet"));
		autorTweet.setNome(rs.getString("nome_autor_tweet"));
		tweet.setUsuario(autorTweet);

		comentario.setTweet(tweet);
		return comentario;
	}
}
