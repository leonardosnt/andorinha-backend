package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.exception.ErroAoAcessarBaseDeDadosException;
import model.exception.ErroNoBancoDeDadosException;

public class AbstractCrudRepository {
	
	protected Connection criarConexao() throws ErroAoAcessarBaseDeDadosException {
		try {
			return DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_test?stringtype=unspecified", "postgres", "postgres");
		} catch (SQLException e) {
			throw new ErroAoAcessarBaseDeDadosException("Erro ao conectar com o BD", e);
		}
	}
	
	protected void fecharConexao(Connection c) {
		try{
			if (!c.isClosed()) {
				c.close();
			}
		}
		catch(Exception silent) {
		}
	}
	
	protected int recuperarProximoValorDaSequence(String sequence) throws ErroAoAcessarBaseDeDadosException, ErroNoBancoDeDadosException {
		Connection c = this.criarConexao();
		PreparedStatement ps;
		try {
			ps = c.prepareStatement("select nextval(?)");
			ps.setString(1, "seq_usuario");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			throw new ErroNoBancoDeDadosException("Valor da sequence nao encontrado: " + sequence, null);
		} catch (SQLException e) {
			throw new ErroNoBancoDeDadosException("Erro ao recuperar proximo valor da sequence " + sequence, e);
		}
		finally {
			this.fecharConexao(c);
		}
	}

}
