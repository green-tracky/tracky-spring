package com.example.tracky._core.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi401;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.kakaojwt.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {

    // 통합 테스트시 mock으로 대체하려면 스프링이 관리하는 bean 이어야 한다. 따라서 스프링이 주입하도록 사용한다
    private final RSAUtil rsaUtil;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String accessToken = request.getHeader("Authorization");

        try {
            if (accessToken == null || accessToken.isBlank()) throw new ExceptionApi401(ErrorCodeEnum.TOKEN_NOT_FOUND);
            if (!accessToken.startsWith("Bearer ")) throw new ExceptionApi401(ErrorCodeEnum.BEARER_PREFIX_MISSING);

            accessToken = accessToken.replace("Bearer ", "");

            OAuthProfile oAuthProfile = rsaUtil.verify(accessToken);

            HttpSession session = request.getSession();
            session.setAttribute(SessionKeys.PROFILE, oAuthProfile);

            chain.doFilter(request, response);
        } catch (TokenExpiredException e1) {
            e1.printStackTrace();
            exResponse(response, ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        } catch (JWTDecodeException | SignatureVerificationException e2) {
            e2.printStackTrace();
            exResponse(response, ErrorCodeEnum.INVALID_TOKEN.getMessage());
        } catch (RuntimeException e3) {
            e3.printStackTrace();
            exResponse(response, e3.getMessage());
        }

    }

    private void exResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter out = response.getWriter();

        Resp<?> resp = Resp.fail(HttpStatus.UNAUTHORIZED.value(), msg);
        String responseBody = new ObjectMapper().writeValueAsString(resp);
        out.println(responseBody);
        out.flush();
    }
}