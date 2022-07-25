package com.example.jwtprac.config.auth;


import com.example.jwtprac.model.User;
import com.example.jwtprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//http://localhost:8080/login
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User useEntity = userRepository.findByUsername(username);
        return new PrincipalDetails(useEntity);
    }
}
