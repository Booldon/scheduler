package com.example.scheduler.jwt;

import com.example.scheduler.DTO.CustomUserDetails;
import com.example.scheduler.config.context.ResourceContext;
import com.example.scheduler.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies();


        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {

                    authorization = cookie.getValue();
                }
            }
        }

        System.out.println("Cookie Authorization : " + authorization);

        //Authorization 헤더 검증
        if (authorization == null) {

            System.out.println("token null");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("JWT token is null");

            //조건이 해당되면 filterchain 종료
            return;
        }

        //token 추출
        String token = authorization;

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        System.out.println("username : " + username);

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) { //true : JWT토큰 만료


            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT token is expired");
            return;
        }

        String role = jwtUtil.getRole(token);
        System.out.println("userrole : "+ role);

        //userEntity를 생성하여 값 set
        User userEntity = new User();
        userEntity.setUsername(username);
        userEntity.setEmail("temp@email.com");
        userEntity.setPassword("temppassword"); //임의의 값 지정
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체에 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        System.out.println("AuthToken : " + authToken);

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //ResourceContext에 사용자 등록
        ResourceContext.setUserContext(customUserDetails);

        System.out.println("SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("SecurityContextHolder Strategy: " + SecurityContextHolder.getContextHolderStrategy());

        try {
            filterChain.doFilter(request, response);
        }
        finally {
            ResourceContext.clear(); //ThreadLocal 클리어
        }
    }
}
