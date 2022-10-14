package com.example.jwtprac.service;

import com.example.jwtprac.auth.PrincipalDetails;
import com.example.jwtprac.config.jwt.TokenProvider;
import com.example.jwtprac.dto.SocialReponseDto;
import com.example.jwtprac.dto.TokenDto;
import com.example.jwtprac.model.KakaoProfile;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.model.OAuthToken;
import com.example.jwtprac.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor

public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;


    //카카오 사용자 로그인요청
    public SocialReponseDto requestKakao(String code, HttpServletResponse response) {
        //REstTemplate을 이용해 POST방식으로 Key=value 데이터를 요청 (카카오쪽으로)
        RestTemplate rt = new RestTemplate();

        //Httpheader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        //컨텐트 타입
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); //내가 전송한 body의 내용이 key=value 임을 알림.
        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // 실제 코드를 쓸 시 아래의 값들을 변수화 해서 쓰는 것이 더 좋다.
        params.add("grant_type", "authorization_code");
//        params.add("client_id", "ddb938f8fed6079e90564fca875e2903");  //수
        params.add("client_id", "811b32c1569bba53dd9f8984c4dd9ac3");
//        params.add("redirect_uri", "http://localhost:3000/auth/kakao/callback");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");  //수
        params.add("code", code);

        //HttpHeader와 HttpBdoy를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = //바디와 헤더값을 넣어준다
                new HttpEntity<>(params, headers); //아래의 exchange가 HttpEntity 오브젝트를 받게 되어있다.

        //Http요청하기 - Post방식으로 - 그리고 responseEntity 변수의 응답 받음.
        ResponseEntity<String> responseEntity = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        //Gson, Json Simple, ObjectMapper 중 하나로 json 데이터를 담는다
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(responseEntity.getBody(), OAuthToken.class);
            System.out.println(oauthToken); //oauthToken 값 확인해 보기
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }
        //엑세스 토큰만 뽑아서 확인
        System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());

        RestTemplate rt2 = new RestTemplate();

        //Httpheader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); //내가 전송한 body의 내용이 key=value 임을 알림.

        //HttpHeader와 HttpBdoy를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = //바디와 헤더값을 넣어준다
                new HttpEntity<>(headers2); //아래의 exchange가 HttpEntity 오브젝트를 받게 되어있다.

        //Http요청하기 - Post방식으로 - 그리고 responseEntity 변수의 응답 받음.
        //사용자 정보를 post로 요청함
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        //KakaoProfile오브젝트를 ObjectMapper로 담는다.
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //User 오브젝트 : username, password
        System.out.println("카카오 닉네임 : " + kakaoProfile.getProperties().getNickname());
        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 프로필 사진 : " + kakaoProfile.getProperties().getProfile_image());
        System.out.println("클라이언트 서버 유저네임 : " + "Kakaoname" + kakaoProfile.getId());

        Member kakaoMember = Member.builder()
                .socialNickname(kakaoProfile.getProperties().getNickname())
                .username("Kakaoname" + kakaoProfile.getId())
                .password(kakaoProfile.getId().toString()) //임시 비밀번호
                .profileImage(kakaoProfile.getProperties().getProfile_image())
                .oauth("kakao")
                .build();

        // 가입자 혹은 비가입자 체크 해서 처리
        Member originMember = findByUser(kakaoMember.getUsername());

        if (originMember.getUsername() == null) {
            System.out.println("신규 회원입니다.");
            SignupKakaoUser(kakaoMember); //자동 회원가입
            return SocialReponseDto.builder()
                    .username("Kakaoname" + kakaoProfile.getId())
                    .nickname(kakaoMember.getNickname())
                    .profileImage(kakaoMember.getProfileImage())
                    .build();
        }

        // kakao 로그인 처리
        System.out.println("kakao 로그인 진행중");
        if (kakaoMember.getUsername() != null) {
            Member memberEntity = userRepository.findByUsername(kakaoMember.getUsername()).orElseThrow(
                    () -> new IllegalArgumentException("kakao username이 없습니다.")
            );
            PrincipalDetails principalDetailis = new PrincipalDetails(memberEntity);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetailis, null, principalDetailis.getAuthorities());
            //홀더에 검증이 완료된 정보 값 넣어준다. -> 이제 controller 에서 @AuthenticationPrincipal principalDetailis principalDetailis 로 정보를 꺼낼 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //JWT 토큰 발급!
            TokenDto jwtToken = tokenProvider.createToken(principalDetailis.getMember().getUsername());
            //refresh 토큰 저장
            tokenProvider.refreshTokneSave(principalDetailis.getMember().getId(), jwtToken.getRefreshToken());

            response.addHeader("Authorization", jwtToken.getAccessToken());
            System.out.println("JWT토큰 : " + jwtToken.getAccessToken());
        }
        Member loginMember = userRepository.findByUsername(kakaoMember.getUsername()).orElseThrow(
                ()-> new IllegalArgumentException("카카오 사용자가 없습니다.")
        );

        return SocialReponseDto.builder()
                .id(loginMember.getId())
                .nickname(loginMember.getNickname())
                .profileImage(loginMember.getProfileImage())
                .age(loginMember.getAge())
                .gender(loginMember.getGender())
                .address(loginMember.getAddress())
                .build();
    }

    //신규 카카오 강제 회원가입
    public String SignupKakaoUser(Member kakaoMember) {
        String error = "";
        String username = kakaoMember.getUsername();
        String password = kakaoMember.getPassword();
        String profileImage = kakaoMember.getProfileImage();
        String kakaoNickname = kakaoMember.getSocialNickname();
        String oauth = kakaoMember.getOauth();

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        kakaoMember.setPassword(password);

        Member member = new Member(username, password, profileImage, oauth, kakaoNickname);
        member.setRole("ROLE_USER");
        userRepository.save(member);

        return error;
    }

    //회원찾기
    @Transactional(readOnly = true)
    public Member findByUser(String username) {
        Member member = userRepository.findByUsername(username).orElseGet(
                ()-> {return new Member();}
        );
        return member;
    }
}
