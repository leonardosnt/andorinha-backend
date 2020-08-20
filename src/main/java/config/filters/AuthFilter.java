package config.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import config.filters.wrapper.TokenRequestWrapper;
import model.Usuario;
import model.exceptions.TokenException;
import repository.JwtRepository;

@WebFilter(filterName = "AuthFilter", urlPatterns = { "/api/*" })
public class AuthFilter implements Filter {

    private static final String URI_LOGIN = "/andorinha-backend/api/auth/login";

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    @Inject
    JwtRepository jwtRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!URI_LOGIN.equalsIgnoreCase(req.getRequestURI())) {
            Usuario user = null;
            String jwtHeader = req.getHeader(AUTH_HEADER);

            if (jwtHeader != null) {
                if (jwtHeader.startsWith(AUTH_HEADER_PREFIX)) {
                    jwtHeader = jwtHeader.replace(AUTH_HEADER_PREFIX, "");
                }

                try {
                    user = this.jwtRepository.getFromToken(jwtHeader);
                } catch (TokenException e) {
                    e.printStackTrace();
                }
            }

            if (user == null) {
                res.setStatus(Status.UNAUTHORIZED.getStatusCode());
                return;
            }

            chain.doFilter(new TokenRequestWrapper(req, user), response);
        } else {
            chain.doFilter(request, response);
        }
    }

}