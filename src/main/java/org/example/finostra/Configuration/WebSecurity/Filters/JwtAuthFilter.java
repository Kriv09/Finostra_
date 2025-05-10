package org.example.finostra.Configuration.WebSecurity.Filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.finostra.Services.User.CustomUserDetailsService;
import org.example.finostra.Services.User.JWT.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService uds;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String jwt = extract(req, "access_token");
        Claims claims = null;

        if (jwt != null) {
            try {
                claims = jwtService.parse(jwt);
            }
            catch (io.jsonwebtoken.ExpiredJwtException ignored) {  }
            catch (io.jsonwebtoken.JwtException ex) {
                jwt = null;
            }
        }

        if (claims == null) {
            String refresh = extract(req, "refresh_token");
            if (refresh != null) {
                try {
                    Claims rClaims = jwtService.parseRefresh(refresh);
                    String uuid = rClaims.getSubject();
                    String login = rClaims.get("usr", String.class);

                    UserDetails user = uds.loadUserByUsername(login);

                    String newAccess = jwtService.generate(user, uuid);
                    addCookie(res, "access_token", newAccess, -1);
                    claims = jwtService.parse(newAccess);
                } catch (io.jsonwebtoken.JwtException ignored) {
                    clearCookies(res);
                }
            }
        }

        if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String uuid = claims.getSubject();
            String login = claims.get("usr", String.class);

            UserDetails user = uds.loadUserByUsername(login);
            var auth = new UsernamePasswordAuthenticationToken(
                    uuid, null, user.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }


    private String extract(HttpServletRequest req, String name) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies())
                if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void addCookie(HttpServletResponse res, String name, String val, int maxAge) {
        Cookie c = new Cookie(name, val);
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setPath("/");
        c.setMaxAge(maxAge);
        res.addCookie(c);
    }

    private void clearCookies(HttpServletResponse res) {
        addCookie(res, "access_token", "", 0);
        addCookie(res, "refresh_token", "", 0);
    }
}