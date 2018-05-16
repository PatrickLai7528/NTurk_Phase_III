package foursomeSE.controller;

import foursomeSE.entity.user.UserType;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyAuthenticationException;
import foursomeSE.entity.communicate.jwt.JwtAuthenticationRequest;
import foursomeSE.entity.communicate.jwt.JwtAuthenticationResponse;
import foursomeSE.service.user.HybridUserService;
import foursomeSE.service.user.lower.LowerUserService;
import foursomeSE.util.MySecureConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static foursomeSE.security.JwtUtil.generateToken;

@RestController
public class AuthenticationController implements MySecureConstants {
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService; // 这两个其实是一个呃
    private HybridUserService hybridUserService;
    private LowerUserService<Worker> workerUtilService;
// 这里如果要拿id返回给client，但是也许也可以放到jwt里面，
    // 这里这么多service感觉不太好

    // @Qualifier("myUserDetailService") UserDetailsService userDetailsService


    public AuthenticationController(AuthenticationManager authenticationManager,
                                    @Qualifier("myUserDetailService") UserDetailsService userDetailsService,
                                    HybridUserService userUtilService,
                                    LowerUserService<Worker> workerUtilService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.hybridUserService = userUtilService;
        this.workerUtilService = workerUtilService;
    }

    @RequestMapping(value = "/auth",
            method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
        authenticate(authenticationRequest.getEmailAddress(), authenticationRequest.getPassword());
        // Reload password post-security so we can generate the token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmailAddress());
        final String token = generateToken(userDetails);

        UserType userType;

        if (userDetails.getUsername().equals("admin@nturk.com")) {
            userType = UserType.ADMIN;
        } else if (workerUtilService.isUsernameExist(authenticationRequest.getEmailAddress())) {
            userType = UserType.WORKER;
        } else {
            userType = UserType.REQUESTER;
        }

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(TOKEN_PREFIX + token
                , hybridUserService.usernameToId(userDetails.getUsername())
                , userType));
    }

    /**
     * private
     */
    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new MyAuthenticationException("MyUser is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new MyAuthenticationException("Bad credentials!", e);
        }
    }
}
