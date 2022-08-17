package com.example.jwtprac.config.jwt;

import com.example.jwtprac.auth.UserDetailsImpl;
import com.example.jwtprac.auth.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FormLoginProvider implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();

        UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        //인코딩된 암호는 시간마다 같은 값도 변경 되기 때문에 matches 함수를 이용해 비교.
        if(passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, null); //이미 인증이 끝났으므로 비밀번호 부분은 삭제한다.
        }else {
            throw new BadCredentialsException("잘못된 로그인 정보입니다."); //인증 실패
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {  //token 타입에 따라서 언제 provider를 사용할지 조건을 지정할 수 있다.
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication); //provider의 supports 값이 false를 리턴하면, provider의 authenticate 메소드가 호출되지 않는다.
    }
}

