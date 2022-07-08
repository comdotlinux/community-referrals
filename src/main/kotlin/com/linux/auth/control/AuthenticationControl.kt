package com.linux.auth.control

import com.vaadin.flow.server.VaadinServletRequest
import io.quarkus.oidc.IdToken
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.RequestScoped
import javax.inject.Inject

@RequestScoped
class AuthenticationControl(@Inject @IdToken var jwt: JsonWebToken, @Inject var securityIdentity: SecurityIdentity) {
    private val l = Logger.getLogger(AuthenticationControl::class.java)

    @PostConstruct
    fun init() {
        l.infov("------idToken name : $jwt ------")
        l.infov("------ securityIdentity.roles : ${securityIdentity.roles} ------")
        l.info("------ VaadinServletRequest.getCurrent().userPrincipal : ${VaadinServletRequest.getCurrent().userPrincipal} ------")
    }

    fun email(): String = jwt.name

    fun givenName(): String = jwt.getClaim("given_name")

    fun familyName(): String = jwt.getClaim("family_name")

}