package foursomeSE.service.user;

import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.service.user.lower.LowerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Qualifier("myUserDetailService")
public class HybridUserDetailsServiceImpl implements UserDetailsService, HybridUserService {

    private LowerUserService<Worker> workerService;
    private LowerUserService<Requester> requesterService;

    @Autowired
    public HybridUserDetailsServiceImpl(LowerUserService<Worker> workerService,
                                        LowerUserService<Requester> requesterService) {
        this.workerService = workerService;
        this.requesterService = requesterService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s.equals("admin@nturk.com")) {
            return User.withUsername("admin@nturk.com")
                    .password(new BCryptPasswordEncoder().encode("admin"))
                    .roles("ADMIN").build();
        }

        try {
            long id = requesterService.usernameToId(s);
            Requester requester = requesterService.getById(id);
            return User.withUsername(requester.getEmailAddress())
                    .password(requester.getPassword())
                    .roles("REQUESTER").build();
        } catch (MyObjectNotFoundException e) {
            try {
                long id = workerService.usernameToId(s);
                Worker worker = workerService.getById(id);
                return User.withUsername(worker.getEmailAddress())
                        .password(worker.getPassword())
                        .roles("WORKER").build();
            } catch (MyObjectNotFoundException e2) {
                throw new UsernameNotFoundException("username " + s + " is not found");
            }
        }

//        return User.withUsername("user")
//                .password(new BCryptPasswordEncoder().encode("pass"))
//                .roles("WORKER").build();
    }

    @Override
    public long usernameToId(String username) {
        if (username.equals("admin@nturk.com")) {
            return 0;
        }
        try {
            return workerService.usernameToId(username);
        } catch (MyObjectNotFoundException e) {
            return requesterService.usernameToId(username);
        }
    }

    @Override
    public boolean isUsernameExist(String username) {
        return workerService.isUsernameExist(username) || requesterService.isUsernameExist(username);
    }
}
