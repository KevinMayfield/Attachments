package virtually.healthcare.emis;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/R4/").permitAll().and().csrf().disable();

        http
                .authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers("/jolokia/**").hasRole("ACTUATOR")
                .antMatchers("/hawtio/**").hasRole("ACTUATOR")
                .and().httpBasic();

    }
}
