package com.project.back_end.services;

import com.project.back_end.repo.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    // Constructor Injection
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // -------------------------------------------------
    // Get Signing Key
    // -------------------------------------------------
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // -------------------------------------------------
    // Generate JWT Token (7 days validity)
    // -------------------------------------------------
    public String generateToken(String identifier) {

        Date now = new Date();
        Date expiry =
                new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    // -------------------------------------------------
    // Extract Identifier (email or username)
    // -------------------------------------------------
    public String extractIdentifier(String token) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return claims.getSubject();
    }

    // OPTIONAL helper (many of your services use email naming)
    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    // -------------------------------------------------
    // Validate Token for specific user type
    // -------------------------------------------------
    public boolean validateToken(String token, String user) {

        try {
            String identifier = extractIdentifier(token);

            switch (user.toLowerCase()) {

                case "admin":
                    return adminRepository
                            .findByUsername(identifier) != null;

                case "doctor":
                    return doctorRepository
                            .findByEmail(identifier) != null;

                case "patient":
                    return patientRepository
                            .findByEmail(identifier) != null;

                default:
                    return false;
            }

        } catch (JwtException | IllegalArgumentException e) {
            // Token invalid / expired / tampered
            return false;
        }
    }
}
