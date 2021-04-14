package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.enums.ShipType;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {

        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean // @Bean --> Permite encapsular el contenido, con la finalidad de otorgar una mejor estructura.
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {


            // -- Players -- //
            Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov", passwordEncoder().encode("24")));
            Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov", passwordEncoder().encode("42")));
            Player player3 = playerRepository.save(new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb")));
            Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole")));


            // -- Games -- //
            Game game1 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
            Game game2 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
            Game game3 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
            Game game4 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(3)));
            Game game5 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(4)));
            Game game6 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(5)));
            Game game7 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(6)));
            Game game8 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(7)));


            // -- GamePlayer -- //
            // Game 1
            GamePlayer gamePlayer1 = gamePlayerRepository.save(new GamePlayer(player1, game1)); // j.bauer vs ________
            GamePlayer gamePlayer2 = gamePlayerRepository.save(new GamePlayer(player2, game1)); // _______ vs c.obrian

            // Game 2
            GamePlayer gamePlayer3 = gamePlayerRepository.save(new GamePlayer(player1, game2)); // j.bauer vs ________
            GamePlayer gamePlayer4 = gamePlayerRepository.save(new GamePlayer(player2, game2)); // _______ vs c.obrian

            // Game 3
            GamePlayer gamePlayer5 = gamePlayerRepository.save(new GamePlayer(player2, game3)); // c.obrian vs _________
            GamePlayer gamePlayer6 = gamePlayerRepository.save(new GamePlayer(player4, game3)); // ________ vs t.almeida

            // Game 4
            GamePlayer gamePlayer7 = gamePlayerRepository.save(new GamePlayer(player2, game4)); // c.obrian vs _______
            GamePlayer gamePlayer8 = gamePlayerRepository.save(new GamePlayer(player1, game4)); // ________ vs j.bauer

            // Game 5
            GamePlayer gamePlayer9 = gamePlayerRepository.save(new GamePlayer(player4, game5)); // t.almeida vs _______
            GamePlayer gamePlayer10 = gamePlayerRepository.save(new GamePlayer(player1, game5)); // ________ vs j.bauer

            // Game 6
            GamePlayer gamePlayer11 = gamePlayerRepository.save(new GamePlayer(player3, game6)); // kim_bauer vs ______

            // Game 7
            GamePlayer gamePlayer12 = gamePlayerRepository.save(new GamePlayer(player4, game7)); // t.almeida vs ______

            // Game 8
            GamePlayer gamePlayer13 = gamePlayerRepository.save(new GamePlayer(player3, game8)); // kim_bauer vs _________
            GamePlayer gamePlayer14 = gamePlayerRepository.save(new GamePlayer(player4, game8)); // _________ vs t.almeida
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/game_view/*").hasAuthority("USER")
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/games").permitAll()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();


        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");


        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

