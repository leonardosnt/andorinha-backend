package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.TweetSeletor;

@Stateless
public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			int id = this.recuperarProximoValorDaSequence("seq_tweet");
			tweet.setId(id);

			PreparedStatement ps = c.prepareStatement("insert into tweet (id, conteudo, data_postagem, id_usuario) values (?, ?, ?, ?)");
			ps.setInt(1, tweet.getId());
			ps.setString(2, tweet.getConteudo());
			ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ps.setInt(4, tweet.getUsuario().getId());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o tweet", e);
		}
	}

	public void atualizar(Tweet tweet) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("update tweet set conteudo = ?, data_postagem = ?, id_usuario = ? where id = ?");
			ps.setString(1, tweet.getConteudo());
			ps.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
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
			String sql = "SELECT conteudo, data_postagem, id_usuario, nome FROM tweet"
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

				Calendar dataTweet = new GregorianCalendar();
				dataTweet.setTime(rs.getTimestamp("data_postagem"));
				tweet.setData(dataTweet);

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
		return pesquisar(new TweetSeletor());
	}

	public List<Tweet> pesquisar(TweetSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		List<Tweet> tweets = new ArrayList<Tweet>();

		try (Connection c = this.abrirConexao()) {
			StringBuilder sql = new StringBuilder(
					"SELECT tweet.id as id_tweet, conteudo, data_postagem, id_usuario, nome FROM tweet"
					+ " JOIN usuario ON usuario.id = id_usuario");
			adicionarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			adicionaParametros(ps, seletor);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				tweets.add(criarModel(rs));
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}

		return tweets;
	}

	public Long contar(TweetSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {
			StringBuilder sql = new StringBuilder("SELECT count(*) as total FROM tweet");
			adicionarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			adicionaParametros(ps, seletor);

			long total = 0;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				total = rs.getLong("total");
			}
			rs.close();
			ps.close();

			return total;
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao contar os tweets", e);
		}
	}

	private void adicionarFiltros(StringBuilder sql, TweetSeletor seletor) {
		if (!seletor.possuiFiltro()) return;

		sql.append(" WHERE ");

		boolean primeiroFiltro = true;

		if (seletor.getId() != null) {
			primeiroFiltro = false;
			sql.append(" id = ? ");
		}

		if (seletor.getIdUsuario() != null) {
			if (!primeiroFiltro) sql.append(" AND ");
			primeiroFiltro = false;
			sql.append(" id_usuario = ? ");
		}

		if (!StringUtils.isBlank(seletor.getConteudo())) {
			if (!primeiroFiltro) sql.append(" AND ");
			primeiroFiltro = false;
			sql.append(" conteudo LIKE ? ");
		}

		if (seletor.getData() != null) {
			if (!primeiroFiltro) sql.append(" AND ");
			primeiroFiltro = false;
			sql.append(" data_postagem::date = ? ");
		}
	}

	private void adicionaParametros(PreparedStatement ps, TweetSeletor seletor) throws SQLException {
		if (!seletor.possuiFiltro()) return;

		int index = 1;

		if (seletor.getId() != null) {
			ps.setInt(index++, seletor.getId());
		}

		if (seletor.getIdUsuario() != null) {
			ps.setInt(index++, seletor.getIdUsuario());
		}

		if (!StringUtils.isBlank(seletor.getConteudo())) {
			ps.setString(index++, String.format("%%%s%%", seletor.getConteudo()));
		}

		if (seletor.getData() != null) {
			ps.setDate(index++, new Date(seletor.getData().getTimeInMillis()));
		}
	}

	private Tweet criarModel(ResultSet rs) throws SQLException {
		Tweet tweet = new Tweet();
		tweet.setId(rs.getInt("id_tweet"));
		tweet.setConteudo(rs.getString("conteudo"));

		Calendar dataTweet = new GregorianCalendar();
		dataTweet.setTime(rs.getTimestamp("data_postagem"));
		tweet.setData(dataTweet);

		Usuario usuario = new Usuario();
		usuario.setId(rs.getInt("id_usuario"));
		usuario.setNome(rs.getString("nome"));

		tweet.setUsuario(usuario);

		return tweet;
	}
}
