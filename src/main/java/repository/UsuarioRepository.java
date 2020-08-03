package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Usuario;
import model.exception.ErroAoAcessarBaseDeDadosException;
import model.exception.ErroNoBancoDeDadosException;

public class UsuarioRepository extends AbstractCrudRepository {
	
	
	
	public void inserir(Usuario u) throws ErroAoAcessarBaseDeDadosException, ErroNoBancoDeDadosException {
		//abrir conexao com o banco de dados
		Connection c = this.criarConexao();
		
		try {
			//proximo valor da sequence
			u.setId( this.recuperarProximoValorDaSequence("seq_usuario") );
			
			//criar a sql de insert
			PreparedStatement ps = c.prepareStatement("insert into usuario (id, nome) values (?, ?)");
			ps.setInt(1, u.getId());
			ps.setString(2, u.getNome());
			
			//executar
			ps.execute();
		} catch (SQLException e) {
			throw new ErroNoBancoDeDadosException("Erro ao inserir novo usuário ", e);
		}
		finally {
			//fechar a conexao com o banco de dados
			this.fecharConexao(c);
		}
	}
	
	public void atualizar(Usuario u) {
		
	}
	
	public void remover(int id) {
		
	}
	
	public Usuario consultar(int id) throws ErroAoAcessarBaseDeDadosException, ErroNoBancoDeDadosException {
		Connection c = this.criarConexao();
		
		try {
			PreparedStatement ps = c.prepareStatement("select id, nome from usuario where id = ?");
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Usuario user = new Usuario();
				user.setId(rs.getInt("id"));
				user.setNome(rs.getString("nome"));
				
				return user;
			}
			
			return null;
		} catch (SQLException e) {
			throw new ErroNoBancoDeDadosException("Erro ao inserir novo usuário ", e);
		}
		finally {
			this.fecharConexao(c);
		}
	}
	

}
