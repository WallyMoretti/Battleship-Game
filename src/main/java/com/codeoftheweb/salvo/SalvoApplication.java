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


            // -- Ship -- //
            // Game 1
            Ship ship1 = shipRepository.save(new Ship(ShipType.SUBMARINE, gamePlayer1, Arrays.asList("E1", "F1", "G1")));
            Ship ship2 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer1, Arrays.asList("B4", "B5")));
            Ship ship3 = shipRepository.save(new Ship(ShipType.DESTROYER, gamePlayer2, Arrays.asList("B5", "C5", "D5")));
            Ship ship4 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer2, Arrays.asList("F1", "F2")));

            // Game 2
            Ship ship5 = shipRepository.save(new Ship(ShipType.DESTROYER, gamePlayer3, Arrays.asList("B5", "C5", "D5")));
            Ship ship6 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer3, Arrays.asList("C6", "C7")));
            Ship ship7 = shipRepository.save(new Ship(ShipType.SUBMARINE, gamePlayer4, Arrays.asList("A2", "A3", "A4")));
            Ship ship8 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer4, Arrays.asList("G6", "H6")));

            // Game 3
            Ship ship9 = shipRepository.save(new Ship(ShipType.DESTROYER, gamePlayer5, Arrays.asList("B5", "C5", "D5")));
            Ship ship10 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer5, Arrays.asList("C6", "C7")));
            Ship ship11 = shipRepository.save(new Ship(ShipType.SUBMARINE, gamePlayer6, Arrays.asList("A2", "A3", "A4")));
            Ship ship12 = shipRepository.save(new Ship(ShipType.PATROL_BOAT, gamePlayer6, Arrays.asList("G6", "H6")));


            // -- Salvo -- //
            // Game 1
            Salvo salvo1 = salvoRepository.save(new Salvo(1, gamePlayer1, Arrays.asList("B5", "C5", "F1")));
            Salvo salvo2 = salvoRepository.save(new Salvo(1, gamePlayer2, Arrays.asList("B4", "B5", "B6")));
            Salvo salvo3 = salvoRepository.save(new Salvo(2, gamePlayer1, Arrays.asList("F2", "D5")));
            Salvo salvo4 = salvoRepository.save(new Salvo(2, gamePlayer2, Arrays.asList("E1", "H3", "A2")));

            // Game 2
            Salvo salvo5 = salvoRepository.save(new Salvo(1, gamePlayer3, Arrays.asList("A2", "A4", "G6")));
            Salvo salvo6 = salvoRepository.save(new Salvo(1, gamePlayer4, Arrays.asList("B5", "D5", "C7")));
            Salvo salvo7 = salvoRepository.save(new Salvo(2, gamePlayer3, Arrays.asList("A3", "H6")));
            Salvo salvo8 = salvoRepository.save(new Salvo(2, gamePlayer4, Arrays.asList("C5", "C6")));

            // Game 3
            Salvo salvo9 = salvoRepository.save(new Salvo(1, gamePlayer5, Arrays.asList("G6", "H6", "A4")));
            Salvo salvo10 = salvoRepository.save(new Salvo(1, gamePlayer6, Arrays.asList("H1", "H2", "H3")));
            Salvo salvo11 = salvoRepository.save(new Salvo(2, gamePlayer5, Arrays.asList("A2", "A3", "D8")));
            Salvo salvo12 = salvoRepository.save(new Salvo(2, gamePlayer6, Arrays.asList("E1", "F2", "G3")));


            // -- Score -- //
            // Game 1
            Score score1 = scoreRepository.save(new Score(game1, player1, 1, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
            Score score2 = scoreRepository.save(new Score(game1, player2, 0, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));

            // Game 2
            Score score3 = scoreRepository.save(new Score(game2, player1, 0.5, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
            Score score4 = scoreRepository.save(new Score(game2, player2, 0.5, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));

            // Game 3
            Score score5 = scoreRepository.save(new Score(game3, player2, 1, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
            Score score6 = scoreRepository.save(new Score(game3, player4, 0, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
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
                .antMatchers("/web/").permitAll()
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

