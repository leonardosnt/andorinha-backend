package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public abstract class AbstractCrudRepository {
	
	protected Connection abrirConexao() throws ErroAoConectarNaBaseException {
		try {
			return DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_test", "postgres", "postgres");
		} catch (SQLException e) {
			throw new ErroAoConectarNaBaseException("Ocorreu um erro ao acesar a base de dados", e);
		}
	}
	
	protected int recuperarProximoValorDaSequence(String nomeSequence) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		//abrir uma conexao com o banco
		try ( Connection c = this.abrirConexao() ) {
			
			//recuperar proximo valor do id
			PreparedStatement ps = c.prepareStatement("select nextval(?)");
			ps.setString(1, nomeSequence);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			throw new ErroAoConsultarBaseException("Erro ao recuperar proximo valor da sequence " + nomeSequence, null);
			
		} catch (SQLException e) {
			throw new ErroAoConectarNaBaseException("Ocorreu um erro ao acesar a base de dados", e);
		}
	}

}
