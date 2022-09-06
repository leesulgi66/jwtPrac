package com.example.jwtprac.auth;

import com.example.jwtprac.model.Member;
import com.example.jwtprac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class PrincipalDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username));

        return new PrincipalDetails(member);
    }
}