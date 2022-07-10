package com.linux.auth.control;

import com.vaadin.flow.server.VaadinServletRequest;
import io.quarkus.oidc.*;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AuthenticationControl {

    private static final Logger l = Logger.getLogger(AuthenticationControl.class);

    @Inject
    @IdToken
    JsonWebToken jwt;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    OidcSession oidcSession;


    @PostConstruct
    void init() {
        l.infov("------idToken name : {0} ------", jwt);
        l.infov("------ securityIdentity.roles : {0} ------", securityIdentity.getRoles());
        l.infov("------ VaadinServletRequest.getCurrent().userPrincipal : {0} ------", VaadinServletRequest.getCurrent().getUserPrincipal());
    }

    public String email() {
        return jwt.getName();
    }

    public String givenName() {
        return jwt.getClaim("given_name");
    }

    public String familyName(){
        return jwt.getClaim("family_name");
    }

    public void logout() {
        oidcSession.logout().await().indefinitely();
    }
}