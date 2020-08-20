package repository;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.Usuario;
import model.exceptions.TokenException;

public class JwtRepository {

	private static final String SECRET = "chave.secreta";

	public Usuario getFromToken(String token) throws TokenException{
		try {
			byte[] encodedKey = Base64.getEncoder().encode(SECRET.getBytes());
			Key key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES");

			//faz o parse do token e monta o body
			Claims claim = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

			//pega o usuario do subject
			String subject = claim.getSubject();
			ObjectMapper mapper = new ObjectMapper();
			Usuario user = mapper.readValue(subject, Usuario.class);
			return user;

		}
		catch(IOException ex){
			ex.printStackTrace();
			throw new TokenException("Token inválido", ex);
		}

	}

	public String generateToken(Usuario u, Date dataExpiracao){
		String s = null;
		try {
			byte[] encodedKey = Base64.getEncoder().encode(SECRET.getBytes());
			Key key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES");


			//to json string
			StringWriter writer = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(writer, u);

			//monta o token passando o subject, data de expiracao e geracao
			s = Jwts.builder()
					.signWith(SignatureAlgorithm.HS512, key)
					.setSubject(writer.toString())
					.setIssuedAt(new Date())
					.setExpiration(dataExpiracao)
					.compact();

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

}