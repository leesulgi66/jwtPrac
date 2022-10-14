package com.example.jwtprac.config.jwt;

import com.example.jwtprac.auth.PrincipalDetails;
import com.example.jwtprac.dto.TokenDto;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.model.RefreshToken;
import com.example.jwtprac.repository.RefreshTokenJpaRepository;
import com.example.jwtprac.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final UserRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private Key key;

    //1. Bean이 생성이 되고 의존성 주입까지 받은 다음에
    public TokenProvider(
            UserRepository userRepository,
            RefreshTokenJpaRepository refreshTokenJpaRepository,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds){
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
        this.userRepository = userRepository;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    //2. 주입받은 secret값을 Base64 Decode해서 key 변수에 할당
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Authentication정보를 받아서 토큰 생성
    public TokenDto createToken(String username) {

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);
        Date validity2 = new Date(now + this.refreshTokenValidityInMilliseconds);

        String accessToken = Jwts.builder()
                .setSubject("accessToken")
                .claim("username", username)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject("refreshToken")
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity2)
                .compact();

        return TokenDto.builder()
                .grantType("bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(tokenValidityInMilliseconds)
                .build();
    }


    //Token에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드 생성
    public Authentication getAuthentication(String username) {

        Member memberEntity = null;
        if (username != null) {
            memberEntity = userRepository.findByUsername(username).orElseThrow(
                    () -> new IllegalArgumentException("username이 없습니다.")
            );
        }

        PrincipalDetails userDetails = new PrincipalDetails(memberEntity);

        //유저,토큰,권한을 이용해서 Authentication 객체를 리턴
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //토큰을 받아서 유효성 검사
    public JwtCode validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e ){
            return JwtCode.EXPIRED;
        } catch (UnsupportedJwtException e ){
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e ) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return JwtCode.DENIED;
    }

    //토큰에서 usernmae 꺼내기
    String JwtUsername(String username) {
        //토큰을 이용해서 claim 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(username)
                .getBody();

        return claims.get("username").toString();

//        Base64.Decoder decoder = Base64.getDecoder();
//        String[] jwtPayload = jwt.split("\\.");
//        byte[] decodedBytes = decoder.decode(jwtPayload[1].getBytes());
//        String tokenString = new String(decodedBytes);
//        return tokenString.split(":")[1].split(",")[0].replace("\"","");
    }

    String userToken(String username) {
        Optional<Member> user = userRepository.findByUsername(username);
        Long userKey = user.get().getId();
        Optional<RefreshToken> refreshToken = refreshTokenJpaRepository.findByTokenKey(userKey);
        logger.info("represhToken 확인 : {}",refreshToken.get().getRefreshToken());
        return refreshToken.get().getRefreshToken();
    }

    //refresh save
    public void refreshTokneSave(Long id, String refToken){
        RefreshToken refreshToken = RefreshToken.builder()
                .key(id)
                .Token(refToken)
                .build();

        if (refreshTokenJpaRepository.findByTokenKey(id).isPresent()) {
            logger.info("리프레쉬 나오나? : : : : {}", refreshTokenJpaRepository.findByTokenKey(id).get().getRefreshToken());
            refreshTokenJpaRepository.deleteAllByTokenKey(id);
        }
        refreshTokenJpaRepository.save(refreshToken);
    }

    public static enum JwtCode{
        DENIED,
        ACCESS,
        EXPIRED;
    }
}
