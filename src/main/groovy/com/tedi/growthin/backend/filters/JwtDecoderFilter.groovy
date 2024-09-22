package com.tedi.growthin.backend.filters

import com.tedi.growthin.backend.configuration.JwtUriConfiguration
import com.tedi.growthin.backend.services.jwt.PublicKeyService
import com.tedi.growthin.backend.services.users.UserService
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.lang.NonNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import java.security.PublicKey

@Component
@Slf4j
class JwtDecoderFilter extends OncePerRequestFilter {
    @Autowired
    JwtUriConfiguration jwtUriConfiguration

    @Autowired
    PublicKeyService publicKeyService

    @Autowired
    UserService userService

    @Autowired
    @Qualifier(value = "PUBLIC_URIS")
    final def PUBLIC_URIS

    private checkUriIsPublic(String URI, String method){
        def res = PUBLIC_URIS.find(x -> x["path"] == URI)
        if(!res)
            return false
        return (res["methods"] as List).contains(method)
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //SKIP URLS THAT DON'T REQUIRE AUTHENTICATION (PUBLICLY ACCESSIBLE)
        if(checkUriIsPublic(request.requestURI.toString(), request.method)){
            filterChain.doFilter(request, response)
            return
        }

//      THIS CODE WILL RUN IF ENDPOINT REQUIRES AUTHENTICATION
//      IF TOKEN IS MISSING OR INVALID -> RETURN UNAUTHORIZED
        String authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        String jwt = authHeader.substring("Bearer ".size())
        try {
            PublicKey publicKey = publicKeyService.getPublickKey()
            JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build()
            def parsedToken = parser.parse(jwt)
            def claimsMap = new HashMap(parsedToken.getBody() as Map)
            if (!(claimsMap["iss"] as String).contains(jwtUriConfiguration.issuerUri) &&
                    !(jwtUriConfiguration.issuerUri).contains(claimsMap["iss"] as String)) {
                throw new Exception("Issuer don't match with issuer's uri in config")
            }
            def userDto = userService.getUserByUsername(claimsMap['sub'] as String)
            if(userDto != null) {
                claimsMap["appUserId"] = userDto.id
                claimsMap["profilePicData"] = userDto.profilePic?Base64.getEncoder().encodeToString(userDto.profilePic.data):null
            }else{
                //user is registered in ouath server but not in application
                log.trace("User with username '${claimsMap['sub'] as String}' is registered in auth server but not in app. Must register to continue.")
                //TODO: REGISTER USER TO APPLICATION WITH DETAILS FROM AUTH SERVER
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE)
                return
            }
            Jwt oauth2Jwt = new Jwt(
                    jwt,
                    new Date(claimsMap['iat'] as Long).toInstant(),
                    new Date(claimsMap['exp'] as Long).toInstant(),
                    parsedToken.getHeader(),
                    claimsMap
            )
            def authorities = []
            claimsMap["authorities"].each { authority ->
                authorities.add(new SimpleGrantedAuthority(authority as String))
            }
            Authentication authentication = new JwtAuthenticationToken(oauth2Jwt, authorities)
            authentication.setAuthenticated(true)
            SecurityContextHolder.getContext().setAuthentication(authentication)
        } catch (ExpiredJwtException expiredJwtException) {
            log.trace("Token has expired: ${expiredJwtException.getMessage()}")
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        } catch (MalformedJwtException malformedJwtException) {
            log.trace("Token is malformed: ${malformedJwtException.getMessage()}")
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        } catch (SignatureException signatureException) {
            log.trace("Jwt signature couldn't be verified: ${signatureException.getMessage()}")
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        } catch (Exception e) {
            log.trace(e.getMessage())
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        filterChain.doFilter(request, response)
    }
}