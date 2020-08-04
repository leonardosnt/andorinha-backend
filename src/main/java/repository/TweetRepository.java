package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			int id = this.recuperarProximoValorDaSequence("seq_tweet");
			tweet.setId(id);

			PreparedStatement ps = c.prepareStatement("insert into tweet (id, conteudo, data_criacao, id_usuario) values (?, ?, ?, ?)");
			ps.setInt(1, tweet.getId());
			ps.setString(2, tweet.getConteudo());
			ps.setTimestamp(3, Timestamp.from(tweet.getDataCriacao()));
			ps.setInt(4, tweet.getUsuario().getId());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o tweet", e);
		}
	}

	public void atualizar(Tweet tweet) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("update tweet set conteudo = ?, data_criacao = ?, id_usuario = ? where id = ?");
			ps.setString(1, tweet.getConteudo());
			ps.setTimestamp(2, Timestamp.from(tweet.getDataCriacao()));
			ps.setInt(3, tweet.getUsuario().getId());
			ps.setInt(4, tweet.getId());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}
	}

	public void remover(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("delete from tweet where id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o tweet", e);
		}
	}

	public Tweet consultar(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			String sql = "SELECT conteudo, data_criacao, id_usuario, nome FROM tweet"
					+ " JOIN usuario ON usuario.id = id_usuario"
					+ " WHERE tweet.id = ?";

			PreparedStatement ps = c.prepareStatement(sql);
			ps.setInt(1, id);

			Tweet tweet = null;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tweet = new Tweet();
				tweet.setId(id);
				tweet.setConteudo(rs.getString("conteudo"));
				tweet.setDataCriacao(rs.getTimestamp("data_criacao").toInstant());

				Usuario usuario = new Usuario();
				usuario.setId(rs.getInt("id_usuario"));
				usuario.setNome(rs.getString("nome"));

				tweet.setUsuario(usuario);
			}
			ps.close();
			rs.close();

			return tweet;
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}
	}

	public List<Tweet> listarTodos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		List<Tweet> tweets = new ArrayList<Tweet>();

		try (Connection c = this.abrirConexao()) {
			String sql = "SELECT tweet.id as id_tweet, conteudo, data_criacao, id_usuario, nome FROM tweet"
					+ " JOIN usuario ON usuario.id = id_usuario";

			PreparedStatement ps = c.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Tweet tweet = new Tweet();
				tweet.setId(rs.getInt("id_tweet"));
				tweet.setConteudo(rs.getString("conteudo"));
				tweet.setDataCriacao(rs.getTimestamp("data_criacao").toInstant());

				Usuario usuario = new Usuario();
				usuario.setId(rs.getInt("id_usuario"));
				usuario.setNome(rs.getString("nome"));

				tweet.setUsuario(usuario);

				tweets.add(tweet);
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}

		return tweets;
	}
}
