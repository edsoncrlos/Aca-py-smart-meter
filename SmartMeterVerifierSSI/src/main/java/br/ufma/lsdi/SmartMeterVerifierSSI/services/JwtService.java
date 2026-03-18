package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import br.ufma.lsdi.SmartMeterVerifierSSI.configs.JwtProperties;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.UserDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.UserPrincipalDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDTO userUserDTO) {
        Instant expirationTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Date expirationDate = Date.from(expirationTime);

        List<GrantedAuthority> role =
                List.of(new SimpleGrantedAuthority(userUserDTO.role()));

        Key key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
        String compactTokenString = Jwts.builder()
                .subject(userUserDTO.connectionId())
                .claim("role", userUserDTO.role())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return compactTokenString;
    }

    public UserPrincipalDTO parseToken(String token) {
        byte[] secretBytes = jwtProperties.secret().getBytes();

        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKey(secretBytes)
                .build()
                .parseClaimsJws(token);

        String connectionId = jwsClaims.getBody().getSubject();

        String role = jwsClaims.getBody().get("role", String.class);

        return new UserPrincipalDTO(connectionId, role);
    }
}
