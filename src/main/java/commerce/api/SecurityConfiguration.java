package commerce.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfiguration {

    @Bean
    Pbkdf2PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    JwtKeyHolder jwtKeyHolder(@Value("${security.jwt.secret}") String jwtSecret) {
        var secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return new JwtKeyHolder(secretKey);
    }

    @Bean
    DefaultSecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                requests -> requests
                    .requestMatchers("/seller/signUp").permitAll()
                    .requestMatchers("/seller/issueToken").permitAll()
                    .requestMatchers("/shopper/signUp").permitAll()
                    .requestMatchers("/shopper/issueToken").permitAll()
            )
            .build();
    }
}
