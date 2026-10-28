package edu.classroom.board;

import java.util.HexFormat;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
  // JJWT 의존성은 pom.xml에 포함되어 있습니다. app.jwt.* 설정은 application.properties 참조.
  private final SecretKey key;    //JWT 서명 검증에 이용할 비밀키
  private final String issuer;    //발급자
  private final String audience;  //사용 대상
  private final int ttl;          //토큰 유효시간 (초 단위)
  private final Users users;      //토큰 사용자 ID가 실제 계정인지 확인하기 위함

  public AuthService(
    @Value("${app.jwt.secret}") String secret,
    @Value("${app.jwt.issuer}") String issuer,
    @Value("${app.jwt.audience}") String audience,
    @Value("${app.jwt.ttl}") int ttl,
    Users users
  ) {

    //secret이 없거나 64자리 16진수가 아닌 경우 시작을 막음
    if (secret == null || !secret.matches("[0-9a-fA-F]{64}")){
      throw new IllegalArgumentException("JWT_SECRET은 새로 생성한 64자리 16진수여야 합니다.");
    }

    //유효시간이 0초 이하로 설정되는 것을 막음
    if (ttl <= 0) {
      throw new IllegalArgumentException("TOKEN_TTL은 양수여야 합니다.");
    }

    //64자리 16진수 -> 32바이트로 변환
    byte[] keyBytes = HexFormat.of().parseHex(secret);

    //변환한 비이트를 HS256 서명용 키로 변환
    this.key = new SecretKeySpec(keyBytes, "HmacSHA256");

    this.issuer = issuer;
    this.audience = audience;
    this.ttl = ttl;
    this.users = users;
  }

  private String extractToken(HttpServletRequest request){
    // 요청의 Authorization 헤더 추출
    String header = request.getHeader("Authorization");

    //헤더가 없으면 오류
    if (header == null || header.isBlank()){
      throw new ApiError(401, "TOKEN_REQUIRED", "로그인 토큰이 필요합니다.");
    }

    //인증 방식과 토큰 분리
    String[] parts = header.strip().split("\\s+");

    //헤더가 bearer 방식인지, 토큰이 하나 전달되었는지 확인
    if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer")){
      throw new ApiError(401, "TOKEN_REQUIRED", "Bearer 토큰 형식을 확인하세요");
    }
    //이상 없으면 토큰만 추출해서 리턴
    return parts[1];
  }

  private Claims verifyToken(String token) {
    try{
      Claims claims = Jwts.parser()
        .verifyWith(key)                          // 발급할때 사용한 비밀키로 서명 검증
        .sig().clear().add(Jwts.SIG.HS256).and()  //허용 알고리즘 HS256으로 제한

        .requireIssuer(issuer)                    // 발급자와 사용 대상이 서버 설정과 일치해야함
        .requireAudience(audience)

        .build()                                  //JwtParser 빌드
        .parseSignedClaims(token)                 //검증
        .getPayload();                            //검증 통과한 payload를 꺼냄
      if (claims.getExpiration() == null){
        throw new ApiError(401, "INVALID_TOKEN", "만료 시각이 없는 토큰입니다.");
      }
        return claims;
    } catch (ExpiredJwtException e) {
      throw new ApiError(401, "TOKEN_EXPIRED", "로그인이 만료되었습니다.");
    } catch (JwtException | IllegalArgumentException e){
      throw new ApiError(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
    }
    
  }

  public String issueToken(Users.PublicUser user) {
    // TODO BE-01: 서버 키로 HS256 JWT 발급. sub, exp, iss, aud 필수.
    Instant now = Instant.now(); // 발급 시점의 현재 시각을 구함

    return Jwts.builder()
      .subject(Long.toString(user.id()))          //사용자 id설정
      .issuer(issuer)                             //발급자 설정
      .audience().add(audience).and()             //사용 대상 지정

      .expiration(Date.from(now.plusSeconds(ttl)))  //현재 시각에 유효시간 ttl을 더해 만료 시각을 설정

      .signWith(key, Jwts.SIG.HS256)              //비밀키로 서명, 알고리즘 지정
      .compact();                                 //header.payload.signature형태의 JWT문자열 반환
    //throw new ApiError(501, "NOT_IMPLEMENTED", "JWT 발급을 구현하세요. BE-01");
  }

  public long currentUserId(HttpServletRequest request) {
    // TODO BE-02: Bearer 추출 → 서명/알고리즘/만료/클레임 검증 → 실제 사용자 확인.
    // 완성 후 아래 데모 ID 반환을 제거하세요. 토큰 없음/변조/만료는 401입니다.

    String token = extractToken(request);           //Authorization 헤더에서 토큰 추출

    Claims claims = verifyToken(token);             //서명/알고리즘/만료/클레임 검증

    Object subject = claims.get("sub");
    if(!(subject instanceof String) || !((String) subject).matches("[1-9][0-9]*")){
      throw new ApiError(401, "INVALID_TOKEN", "사용자 ID가 올바르지 않습니다.");
    }

    long userId;
    try {
      userId = Long.parseLong((String) subject);
    } catch (NumberFormatException e) {
      throw new ApiError(401, "INVALID_TOKEN", "사용자 ID가 범위를 벗어났습니다.");
    }

    if (users.find(userId) == null){
      throw new ApiError(401, "INVALID_TOKEN", "존재하지 않는 사용자입니다.");
    }

    return userId;
  }

  public void assertOwner(long userId, long authorId) {
    // TODO BE-03: 다른 작성자이면 403 FORBIDDEN. 현재는 검사하지 않습니다.
    if (userId != authorId){
      throw new ApiError(403, "FORBIDDEN", "본인이 작성한 글만 변경할 수 있습니다.");
    }
  }
}
