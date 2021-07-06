/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.egg.tinder;

import edu.egg.tinder.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author CARMEN
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SeguridadConfiguracion extends WebSecurityConfigurerAdapter{
    
    @Autowired
    public UsuarioServicio usuarioServicio;
    
    //ES PARA LA AUTENTIFICACION DEL USUARIO y lo del encoder
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth
                .userDetailsService(usuarioServicio)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    //ESTO ES PARA LOS ACCESOS A LA TEMPLATES Y LOS STATIC
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        
        http.headers().frameOptions().sameOrigin().and()
                .authorizeRequests()
                        .antMatchers("/css/*", "/js/*","/img/*")
                        .permitAll()
                //ESTO ES LO DEL MAPEO DE LO DEL LOGIN QUE TIENE EL CONTROLADOR
                .and().formLogin()
                        .loginPage("/login") //DONDE ESTA EL LOGIN PAGE
                                .loginProcessingUrl("/logincheck")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/inicio")
                                .permitAll()
                        .and().logout()
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll().and().csrf().disable().requiresChannel(channel -> channel
                                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null).requiresSecure());;
    }
}
