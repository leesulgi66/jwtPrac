package com.example.jwtprac.config.jwt;

import com.example.jwtprac.dto.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//시큐리티가 filter 가지고 있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있음.
//권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.!!!!!!
//만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐.
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private TokenProvider tokenProvider;

    @Value("${secret.key}")
    private String secretKey;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        super(authenticationManager);
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        //헤더 확인
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        //JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
        String jwt = request.getHeader("Authorization")
                .replace("Bearer ", "");

        //username 꺼내기
        String username = tokenProvider.JwtUsername(jwt);

        log.info("파싱된 토큰 확인해보기 : {}", jwt);
        log.info("username 확인 해보기 : {}",tokenProvider.JwtUsername(jwt));

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt) == TokenProvider.JwtCode.ACCESS) { //받아온 토큰이 유효성 검증이 완료되면

            Authentication authentication = tokenProvider.getAuthentication(username); //authentication 객체를 반환하고
            SecurityContextHolder.getContext().setAuthentication(authentication); //securityContextHolder에 저장해준다
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        }
        else if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt) == TokenProvider.JwtCode.EXPIRED){
            log.info("만료된 토큰 확인");
            String refreshToken = tokenProvider.userToken(username);
            log.info("리프레쉬 토큰 확인 : {}" , refreshToken);
            if(tokenProvider.validateToken(refreshToken) == TokenProvider.JwtCode.ACCESS) {
                log.info("새로운 토큰으로 발급");
                TokenDto reJwt = tokenProvider.createToken(username);
                System.out.println("-새토큰 : " + reJwt.getAccessToken());

                response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + reJwt.getAccessToken());

            } else{
                log.info("유효한 refreshToken이 없습니다. {}" , "다시 로그인 해주세요");
            }
        }
        else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        chain.doFilter(request, response);


//        ------------------------------------------------------------------------------------------------------------
//        String username =
//                JWT.require(Algorithm.HMAC512("6dltmfrl")).build().verify(jwtToken).getClaim("username").asString();
//        //서명이 정상적으로 됨.
//        if(username != null) {
//            Member memberEntity = userRepository.findByUsername(username).orElseThrow(
//                    ()-> new IllegalArgumentException("username이 없습니다.")
//            );
//
//            PrincipalDetails userDetails = new PrincipalDetails(memberEntity);
//
//            //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
//            Authentication authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
//            //홀더에 검증이 완료된 정보 값 넣어준다. -> 이제 controller 에서 @AuthenticationPrincipal UserDetailsImpl userDetails 로 정보를 꺼낼 수 있다.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            chain.doFilter(request, response);
    }
}
