package repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exception.ErroAoAcessarBaseDeDadosException;
import model.exception.ErroNoBancoDeDadosException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TestUsuarioRepository {

	
	private UsuarioRepository usuarioRepository;
	
	@Before
	public void setUp() {
		this.usuarioRepository = new UsuarioRepository();
	}
	
	
	@Test
	public void testa_se_inseriu_novo_usuario() throws ErroAoAcessarBaseDeDadosException, ErroNoBancoDeDadosException {
		Usuario user = new Usuario();
		user.setNome("User teste de Unidade");
		this.usuarioRepository.inserir(user);
		
		assertTrue( user.getId() > 0 );
	}
	
	@Test
	public void testa_consultar_usuario() throws ErroAoAcessarBaseDeDadosException, ErroNoBancoDeDadosException {
		Usuario user = this.usuarioRepository.consultar(1);
		
		assertNotNull(user);
		assertEquals(1, user.getId());
	}
	
}
