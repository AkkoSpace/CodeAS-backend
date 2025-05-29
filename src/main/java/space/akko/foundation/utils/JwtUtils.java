package space.akko.foundation.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import space.akko.foundation.constant.SecurityConstants;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
public final class JwtUtils {

    private JwtUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 生成JWT令牌
     *
     * @param subject 主题（通常是用户ID）
     * @param claims 声明
     * @param expiration 过期时间（秒）
     * @param secret 密钥
     * @return JWT令牌
     */
    public static String generateToken(String subject, Map<String, Object> claims,
                                     long expiration, String secret) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成访问令牌
     */
    public static String generateAccessToken(Long userId, String username,
                                           Map<String, Object> claims,
                                           long expiration, String secret) {
        claims.put(SecurityConstants.USER_ID_CLAIM, userId);
        claims.put(SecurityConstants.USERNAME_CLAIM, username);
        claims.put(SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.ACCESS_TOKEN);

        return generateToken(String.valueOf(userId), claims, expiration, secret);
    }

    /**
     * 生成刷新令牌
     */
    public static String generateRefreshToken(Long userId, String username,
                                            long expiration, String secret) {
        Map<String, Object> claims = Map.of(
            SecurityConstants.USER_ID_CLAIM, userId,
            SecurityConstants.USERNAME_CLAIM, username,
            SecurityConstants.TOKEN_TYPE_CLAIM, SecurityConstants.REFRESH_TOKEN
        );

        return generateToken(String.valueOf(userId), claims, expiration, secret);
    }

    /**
     * 解析JWT令牌
     */
    public static Claims parseToken(String token, String secret) {
        if (StrUtil.isBlank(token)) {
            throw new IllegalArgumentException("JWT令牌不能为空");
        }

        // 移除Bearer前缀
        if (token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.TOKEN_PREFIX.length());
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw new IllegalArgumentException("JWT令牌已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT令牌: {}", e.getMessage());
            throw new IllegalArgumentException("不支持的JWT令牌");
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw new IllegalArgumentException("JWT令牌格式错误");
        } catch (SignatureException e) {
            log.warn("JWT令牌签名验证失败: {}", e.getMessage());
            throw new IllegalArgumentException("JWT令牌签名验证失败");
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌参数错误: {}", e.getMessage());
            throw new IllegalArgumentException("JWT令牌参数错误");
        }
    }

    /**
     * 验证JWT令牌
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public static Long getUserIdFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        Object userId = claims.get(SecurityConstants.USER_ID_CLAIM);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从令牌中获取用户名
     */
    public static String getUsernameFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return (String) claims.get(SecurityConstants.USERNAME_CLAIM);
    }

    /**
     * 从令牌中获取令牌类型
     */
    public static String getTokenTypeFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return (String) claims.get(SecurityConstants.TOKEN_TYPE_CLAIM);
    }

    /**
     * 从令牌中获取过期时间
     */
    public static Date getExpirationFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getExpiration();
    }

    /**
     * 从令牌中获取签发时间
     */
    public static Date getIssuedAtFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getIssuedAt();
    }

    /**
     * 检查令牌是否过期
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            Date expiration = getExpirationFromToken(token, secret);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取令牌剩余有效时间（秒）
     */
    public static long getTokenRemainingTime(String token, String secret) {
        try {
            Date expiration = getExpirationFromToken(token, secret);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining / 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 检查令牌是否需要刷新
     * 当令牌剩余时间少于总时间的1/3时，建议刷新
     */
    public static boolean shouldRefreshToken(String token, String secret) {
        try {
            Date issuedAt = getIssuedAtFromToken(token, secret);
            Date expiration = getExpirationFromToken(token, secret);
            Date now = new Date();

            long totalTime = expiration.getTime() - issuedAt.getTime();
            long remainingTime = expiration.getTime() - now.getTime();

            return remainingTime < totalTime / 3;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从请求头中提取令牌
     */
    public static String extractTokenFromHeader(String authHeader) {
        if (StrUtil.isBlank(authHeader)) {
            return null;
        }

        if (authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
        }

        return authHeader;
    }
}
