package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

// 사용자의 인증을 위해 필요한 필터
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env;

    public AuthenticationFilter(UserService userService, Environment env,
                                AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.env = env;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            System.out.println("=== attemptAuthentication 호출됨 ===");

            RequestLogin creds = new ObjectMapper().readValue(req.getInputStream(), RequestLogin.class);

            System.out.println("입력된 Email: " + creds.getEmail());
            System.out.println("입력된 Password: " + creds.getPassword());

            Authentication authentication = getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
            System.out.println("인증 성공!");
            return authentication;

        } catch (IOException e) {
            System.err.println("IOException 발생: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (AuthenticationException e) {
            System.err.println("인증 실패: " + e.getMessage());
            throw e;
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        System.err.println("=== unsuccessfulAuthentication 호출됨 ===");
        System.err.println("실패 원인: " + failed.getMessage());
        failed.printStackTrace();

        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        System.out.println("=== successfulAuthentication 호출됨 ===");

        String userEmail = ((User)authResult.getPrincipal()).getUsername(); // email
        UserDto userDetails = userService.getUserDetailsByEmail(userEmail);

        byte[] secretKeyBytes = env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8);

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(userDetails.getUserId()) // UUID
                .expiration(Date.from(now.plusMillis(Long.parseLong(env.getProperty("token.expiration-time")))))
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        res.addHeader("token", token);
        res.addHeader("userId", userDetails.getUserId());
        // 클라이언트에서 복호화했을 때 한 번 더 검증할 수 있도록 암호화 되지 않은 아이디 값 보냄
    }
}
