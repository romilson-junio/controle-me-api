package com.controle.controleme.service.impl;

import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiracao}")
    private String expiracao;

    @Value("${jwt.chave-assinatura}")
    private String chaveAssinatura;

    @Override
    public String getarToken(Usuario usuario) {
        Long exp = Long.valueOf(expiracao);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(exp);
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        String horaExpiracaoToken = DateTimeFormatter.ofPattern("HH:mm").format(dataHoraExpiracao.toLocalTime());

        String token = Jwts.builder()
                                .setExpiration(data)
                                .setSubject(usuario.getEmail())
                                .claim("nome", usuario.getNome())
                                .claim("horaExpiracao", horaExpiracaoToken)
                                .claim("userid", usuario.getId())
                                .signWith(SignatureAlgorithm.HS512,chaveAssinatura)
                                .compact();
        return token;
    }

    @Override
    public Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                    .setSigningKey(chaveAssinatura)
                    .parseClaimsJws(token)
                    .getBody();
    }

    @Override
    public boolean isTokenValido(String token) {
        try {
            Claims claims = this.obterClaims(token);
            Date dateExpiration = claims.getExpiration();
            LocalDateTime dataExpiracao = dateExpiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(dataExpiracao);
        } catch (ExpiredJwtException e){
            return false;
        }
    }

    @Override
    public String obterLoginUsuario(String token) {
        Claims claims = this.obterClaims(token);
        return claims.getSubject();
    }
}
