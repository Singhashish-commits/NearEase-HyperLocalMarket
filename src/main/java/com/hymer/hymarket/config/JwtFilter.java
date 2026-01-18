package com.hymer.hymarket.config;
import com.hymer.hymarket.service.JwtService;
import com.hymer.hymarket.service.MyUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    JwtService jwtService;
    ApplicationContext context;

    @Autowired
    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // Get the path of the current request
        String path = request.getServletPath();

        // If the path is for auth, skip the filter and let the controller handle it
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
// request have many header  from which we need the header Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7); // Skip the fist string "Bearer";
            userName = jwtService.extractUserName(token); // Extract Username from the token written in JwtService
        }
//          if the username is not null from the token and is not already authenticated,authenticate it
        if(userName != null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails = context.getBean(MyUserDetailService.class).loadUserByUsername(userName);

            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                // Asked three things in the token principle ,credentials and the Authorities
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // webAuthenticationDetailSource is a spring helper class that extract detail from the HttpServletRequest
                //Builds a WebAuthenticationDetails object
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
