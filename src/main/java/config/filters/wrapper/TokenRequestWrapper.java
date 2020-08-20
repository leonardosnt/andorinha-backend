package config.filters.wrapper;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import model.Usuario;


public class TokenRequestWrapper extends HttpServletRequestWrapper  {

	private Usuario principal;

	public TokenRequestWrapper(HttpServletRequest request, Usuario user) {
	    super(request);
	    this.principal = user;
    }

	@Override
	public Principal getUserPrincipal() {
        return this.principal;
    }

	@Override
	public String getRemoteUser() {
        return this.principal != null ? this.principal.getName() : null;
    }

}