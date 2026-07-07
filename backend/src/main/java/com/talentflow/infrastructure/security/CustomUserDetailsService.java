package com.talentflow.infrastructure.security;

import com.talentflow.domain.identity.User;
import com.talentflow.domain.identity.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] parts = username.split(":", 2);
        if (parts.length != 2) throw new UsernameNotFoundException("Must provide tenantId:email format");

        UUID tenantId = UUID.fromString(parts[0]);
        String email = parts[1];

        User user = userRepository.findByEmail(tenantId, email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!user.isEnabled()) throw new DisabledException("User is disabled");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
