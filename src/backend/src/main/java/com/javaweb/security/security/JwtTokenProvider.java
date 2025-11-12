package com.javaweb.security.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT令牌提供者
 *
 * <p>负责JWT令牌的生成、解析和验证
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Component
public class JwtTokenProvider {

  @Value("${app.jwt.secret:mySecretKey}")
  private String jwtSecret;

  @Value("${app.jwt.expiration:86400000}")
  private Long jwtExpirationMs;

  @Value("${app.jwt.refresh-expiration:604800000}")
  private Long refreshExpirationMs;

  private Key key;

  /** 初始化签名密钥 */
  @PostConstruct
  public void init() {
    if (!StringUtils.hasText(jwtSecret)) {
      generateDefaultKey("JWT secret is empty, generated a secure default key.");
      return;
    }

    if (jwtSecret.length() < 64) {
      generateDefaultKey(
          "JWT secret length is insufficient for HS512; generated a secure default key.");
      return;
    }

    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  private void generateDefaultKey(String reason) {
    Key generatedKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    this.key = generatedKey;
    this.jwtSecret = Encoders.BASE64.encode(generatedKey.getEncoded());
    System.out.println("WARNING: " + reason);
  }

  /**
   * 生成访问令牌
   *
   * @param userDetails 用户详细信息
   * @return JWT访问令牌
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDetails.getUsername());

    // 手动序列化权限，避免Jackson序列化问题
    List<String> authorityStrings = new ArrayList<>();
    for (GrantedAuthority authority : userDetails.getAuthorities()) {
      authorityStrings.add(authority.getAuthority());
    }
    claims.put("authorities", authorityStrings);

    return createToken(claims, userDetails.getUsername(), jwtExpirationMs);
  }

  /**
   * 生成访问令牌（通过Authentication，支持自定义过期时间）
   *
   * @param authentication 认证信息
   * @param expirationMs 过期时间（毫秒）
   * @return JWT访问令牌
   */
  public String generateToken(Authentication authentication, long expirationMs) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDetails.getUsername());

    // 手动序列化权限，避免Jackson序列化问题
    List<String> authorityStrings = new ArrayList<>();
    for (GrantedAuthority authority : userDetails.getAuthorities()) {
      authorityStrings.add(authority.getAuthority());
    }
    claims.put("authorities", authorityStrings);

    return createToken(claims, userDetails.getUsername(), expirationMs);
  }

  /**
   * 生成访问令牌（通过Authentication）
   *
   * @param authentication 认证信息
   * @return JWT访问令牌
   */
  public String generateToken(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return generateToken(userDetails);
  }

  /**
   * 生成刷新令牌
   *
   * @param userDetails 用户详细信息
   * @return JWT刷新令牌
   */
  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDetails.getUsername());
    claims.put("type", "refresh");
    return createToken(claims, userDetails.getUsername(), refreshExpirationMs);
  }

  /**
   * 创建JWT令牌
   *
   * @param claims 声明
   * @param subject 主题（用户名）
   * @param expiration 过期时间（毫秒）
   * @return JWT令牌
   */
  private String createToken(Map<String, Object> claims, String subject, Long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * 从令牌中获取用户名
   *
   * @param token JWT令牌
   * @return 用户名
   */
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * 从JWT中获取用户名（兼容方法）
   *
   * @param token JWT令牌
   * @return 用户名
   */
  public String getUsernameFromJWT(String token) {
    return getUsernameFromToken(token);
  }

  /**
   * 获取JWT过期时间（毫秒）
   *
   * @return 过期时间毫秒数
   */
  public int getJwtExpirationInMs() {
    return jwtExpirationMs.intValue();
  }

  /**
   * 从令牌中获取过期时间
   *
   * @param token JWT令牌
   * @return 过期时间
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  /**
   * 从令牌中获取指定声明
   *
   * @param token JWT令牌
   * @param claimsResolver 声明解析器
   * @param <T> 声明类型
   * @return 声明值
   */
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * 从令牌中获取所有声明
   *
   * @param token JWT令牌
   * @return 声明对象
   */
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  /**
   * 检查令牌是否过期
   *
   * @param token JWT令牌
   * @return 是否过期
   */
  public Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * 验证令牌
   *
   * @param token JWT令牌
   * @return 是否有效
   */
  public Boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return !isTokenExpired(token);
    } catch (SecurityException ex) {
      logger.error("Invalid JWT signature: " + ex.getMessage());
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token: " + ex.getMessage());
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token: " + ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token: " + ex.getMessage());
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty: " + ex.getMessage());
    }
    return false;
  }

  /**
   * 验证令牌与用户信息是否匹配
   *
   * @param token JWT令牌
   * @param userDetails 用户详细信息
   * @return 是否匹配
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * 刷新令牌
   *
   * @param refreshToken 刷新令牌
   * @param userDetails 用户详细信息
   * @return 新的访问令牌
   */
  public String refreshToken(String refreshToken, UserDetails userDetails) {
    if (validateToken(refreshToken)) {
      return generateToken(userDetails);
    }
    throw new IllegalArgumentException("Invalid refresh token");
  }

  /**
   * 获取令牌剩余有效时间（秒）
   *
   * @param token JWT令牌
   * @return 剩余有效时间
   */
  public Long getTokenValiditySeconds(String token) {
    Date expiration = getExpirationDateFromToken(token);
    Date now = new Date();
    return (expiration.getTime() - now.getTime()) / 1000;
  }

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class);
}
