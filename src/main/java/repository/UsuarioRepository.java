package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.UsuarioSeletor;

@Stateless
public class UsuarioRepository extends AbstractCrudRepository {

	public void inserir(Usuario usuario) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		//abrir uma conexao com o banco
		try (Connection c = this.abrirConexao()) {

			//proximo valor da sequence
			int id = this.recuperarProximoValorDaSequence("seq_usuario");
			usuario.setId(id);

			//criar e executar a sql
			PreparedStatement ps = c.prepareStatement("insert into usuario (id, nome) values (?, ?)");
			ps.setInt(1, usuario.getId());
			ps.setString(2, usuario.getNome());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o usuário", e);
		}
	}

	public void atualizar(Usuario usuario) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("update usuario set nome = ? where id = ?");
			ps.setString(1, usuario.getNome());
			ps.setInt(2, usuario.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o usuário", e);
		}
	}

	public void remover(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {
			PreparedStatement ps = c.prepareStatement("delete from usuario where id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o usuário", e);
		}
	}

	public Usuario consultar(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {

		//abrir uma conexao com o banco
		try (Connection c = this.abrirConexao()) {

			Usuario user = null;

			//criar e executar a sql
			PreparedStatement ps = c.prepareStatement("select id, nome from usuario where id = ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = new Usuario();
				user.setId( rs.getInt("id") );
				user.setNome( rs.getString("nome") );
			}
			rs.close();
			ps.close();

			return user;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar o usuário", e);
		}
	}

	public List<Usuario> listarTodos() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		return pesquisar(new UsuarioSeletor());
	}

	public List<Usuario> pesquisar(UsuarioSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		List<Usuario> resultado = new ArrayList<>();

		try (Connection c = this.abrirConexao()) {
			StringBuilder sql = new StringBuilder("SELECT id, nome FROM usuario");
			adicionarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			adicionarParametros(ps, seletor);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				resultado.add(criarModel(rs));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao listar todos os usuários", e);
		}

		return resultado;
	}

	public Long contar(UsuarioSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {
			StringBuilder sql = new StringBuilder("SELECT count(*) as total FROM usuario");
			adicionarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			adicionarParametros(ps, seletor);

			long total = 0;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				total = rs.getLong("total");
			}
			rs.close();
			ps.close();

			return total;
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao listar todos os usuários", e);
		}
	}

	private void adicionarFiltros(StringBuilder sql, UsuarioSeletor seletor) {
		if (!seletor.possuiFiltro()) return;
		sql.append(" WHERE ");

		boolean primeiroFiltro = true;
		if (seletor.getId() != null) {
			primeiroFiltro = false;
			sql.append("id = ? ");
		}
		if (!StringUtils.isBlank(seletor.getNome())) {
			if (!primeiroFiltro) {
				sql.append("AND ");
			}
			sql.append("nome LIKE ?");
		}
	}

	private void adicionarParametros(PreparedStatement ps, UsuarioSeletor seletor) throws SQLException {
		if (!seletor.possuiFiltro()) return;

		int index = 1;

		if (seletor.getId() != null) {
			ps.setInt(index++, seletor.getId());
		}

		if (!StringUtils.isBlank(seletor.getNome())) {
			ps.setString(index++, String.format("%%%s%%", seletor.getNome()));
		}
	}

	private Usuario criarModel(ResultSet rs) throws SQLException {
		Usuario user = new Usuario();
		user.setId(rs.getInt("id"));
		user.setNome(rs.getString("nome"));
		return user;
	}

}
