package ru.otus.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    private ServletContext context;

    private static final String LOGIN_PAGE = "/login";
    private static final String API_PREFIX = "/api/";
    private static final String STATIC_CSS = "/css/";
    private static final String STATIC_JS = "/js/";
    private static final String STATIC_IMG = "/images/";

    private static final String UNAUTHORIZED_JSON = "{\"error\":\"unauthorized\"}";
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String USER_SESSION_ATTR = "user";

    @Override
    public void init(FilterConfig filterConfig) {
        this.context = filterConfig.getServletContext();
        this.context.log("AuthorizationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        this.context.log("Requested Resource: " + uri);

        HttpSession session = request.getSession(false);
        Object user = (session == null) ? null : session.getAttribute(USER_SESSION_ATTR);

        boolean isLoggedIn = user != null;
        boolean isLoginPage = uri.equals(LOGIN_PAGE);
        boolean isStaticResource =
                uri.startsWith(STATIC_CSS) || uri.startsWith(STATIC_JS) || uri.startsWith(STATIC_IMG);
        boolean isApiRequest = uri.startsWith(API_PREFIX);

        if (isLoggedIn || isLoginPage || isStaticResource) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (isApiRequest) {
            sendUnauthorizedJson(response);
            return;
        }

        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(LOGIN_PAGE);
    }

    private void sendUnauthorizedJson(HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(UNAUTHORIZED_JSON);
        response.getWriter().flush();
    }

    @Override
    public void destroy() {
        this.context.log("AuthorizationFilter destroyed");
    }
}
