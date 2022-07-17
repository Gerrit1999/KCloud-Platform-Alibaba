package io.laokou.auth.infrastructure.common.oauth2;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * authorize
 * @author Kou Shenhai
 */
@Data
@AllArgsConstructor
public class Oauth2Token implements AuthenticationToken {

    private final String token;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}