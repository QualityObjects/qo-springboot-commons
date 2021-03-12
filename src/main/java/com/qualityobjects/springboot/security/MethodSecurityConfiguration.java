package com.qualityobjects.springboot.security;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase para determinar qué tipo de anotaciones se van a tener en cuenta para la seguridad
 * 
 * La JSR-250 es la que usa anotaciones como {@link RolesAllowed} y {@link PermitAll} 
 * Se pueden poner a nivel de clase o a nivel de método.
 * 
 * @author rob
 */
//@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass = true)
//@EnableGlobalMethodSecurity(jsr250Enabled = true, securedEnabled = false)
//@Profile("!test")
@Slf4j
public class MethodSecurityConfiguration 
  extends GlobalMethodSecurityConfiguration {
	
	@SuppressWarnings("unused")

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		log.info("GlobalMethodSecurityConfiguration ... config");
	    return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
	}
	
	
}