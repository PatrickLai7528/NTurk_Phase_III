package foursomeSE.security;

import foursomeSE.security.filter.AuthorizationTokenFilter;
import foursomeSE.service.user.HybridUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private HybridUserDetailsServiceImpl hybridUserDetailsServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(HybridUserDetailsServiceImpl hybridUserDetailsServiceImpl, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.hybridUserDetailsServiceImpl = hybridUserDetailsServiceImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        super.configure(httpSecurity);
        httpSecurity.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/image/**").permitAll()
                .antMatchers("/h2-console/**/**").permitAll()
                .antMatchers(HttpMethod.GET, "/tasks/id/**").permitAll()
//                .antMatchers("/auth/**").permitAll()
//
//                .antMatchers("/tasks/**").hasRole("WORKER")
//                .antMatchers("/image/**").hasRole("ADMIN")
//                .anyRequest().authenticated();
                .and().httpBasic().disable()
        ;
        httpSecurity.addFilter(new AuthorizationTokenFilter(authenticationManager()
                , hybridUserDetailsServiceImpl)); // 这里改了一点，本来怎么会自己new的呃
    }

    // 这个和下面的？重吗？什么关系？不管了…
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hybridUserDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
        // AuthenticationTokenFilter will ignore the below paths
        web
                .ignoring()
                .antMatchers(HttpMethod.POST, "/auth/**")

                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/image/**",
                        "/tasks/id/**"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/image/**"
                )

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(hybridUserDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder);
    }
}
