package com.vendora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vendora.model.DeliveryAgent;
import com.vendora.model.User;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //  Normal user / admin
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            System.out.println("✅ Authenticating USER/ADMIN: " + email);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())      // from DB
                    .authorities(user.getRole())       // ROLE_USER or ROLE_ADMIN
                    .disabled(!user.isEnabled())
                    .build();
        }

        // 🚚 Delivery Agent
        DeliveryAgent agent = deliveryAgentRepository.findByEmail(email);
        if (agent != null) {
            System.out.println("✅ Authenticating AGENT: " + email);
            return org.springframework.security.core.userdetails.User
                    .withUsername(agent.getEmail())
                    .password("vendora")         // plain text password
                    .authorities("ROLE_AGENT")
                    .disabled(!agent.isActive())
                    .build();
        }

        System.out.println("❌ No user/agent found: " + email);
        throw new UsernameNotFoundException("No user or agent found with email: " + email);
    }
}
