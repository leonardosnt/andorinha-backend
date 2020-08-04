package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import model.Tweet;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public class TweetRepository extends AbstractCrudRepository {

	private UsuarioRepository usuarioRepostory;

	public TweetRepository(UsuarioRepository usuarioRepostory) {
		this.usuarioRepostory = usuarioRepostory;
	}

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

	public void remover(int id) {

	}

	public Tweet consultar(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("select conteudo, data_criacao, id_usuario from tweet where id = ?");
			ps.setInt(1, id);

			Tweet tweet = null;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tweet = new Tweet();
				tweet.setId(id);
				tweet.setConteudo(rs.getString("conteudo"));
				tweet.setDataCriacao(rs.getTimestamp("data_criacao").toInstant());
				tweet.setUsuario(this.usuarioRepostory.consultar(rs.getInt("id_usuario")));
			}
			ps.close();
			rs.close();

			return tweet;
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}
	}

	public List<Tweet> listarTodos() {
		return null;
	}
}
