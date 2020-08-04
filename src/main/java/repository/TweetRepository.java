package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import model.Tweet;
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
			ps.setTimestamp(3, Timestamp.from(tweet.getData()));
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
			ps.setTimestamp(2, Timestamp.from(tweet.getData()));
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

	public Tweet consultar(int id) {
		return null;
	}

	public List<Tweet> listarTodos() {
		return null;
	}
}
