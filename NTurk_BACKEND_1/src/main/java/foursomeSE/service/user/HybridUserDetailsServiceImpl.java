package foursomeSE.service.user;

import foursomeSE.entity.user.MyUser;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static foursomeSE.service.user.UserUtils.userByUsername;


@Service
@Qualifier("myUserDetailService")
public class HybridUserDetailsServiceImpl implements UserDetailsService, HybridUserService {

    private WorkerJPA workerJPA;
    private RequesterJPA requesterJPA;

    public HybridUserDetailsServiceImpl(WorkerJPA workerJPA, RequesterJPA requesterJPA) {
        this.workerJPA = workerJPA;
        this.requesterJPA = requesterJPA;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s.equals("admin@nturk.com")) {
            return User.withUsername("admin@nturk.com")
                    .password(new BCryptPasswordEncoder().encode("admin"))
                    .roles("ADMIN").build();
        }


        return requesterJPA.findByEmailAddress(s)
                .map((requester) -> User.withUsername(requester.getEmailAddress())
                        .password(requester.getPassword())
                        .roles("REQUESTER").build())
                .orElseGet(
                        () -> workerJPA.findByEmailAddress(s)
                                .map((worker) -> User.withUsername(worker.getEmailAddress())
                                        .password(worker.getPassword())
                                        .roles("WORKER").build())
                                .orElseThrow(() -> new UsernameNotFoundException("username " + s + " is not found"))
                );


//        try {
//            Requester requester = requesterJPA.findByEmailAddress(s).;
//            return User.withUsername(requester.getEmailAddress())
//                    .password(requester.getPassword())
//                    .roles("REQUESTER").build();
//        } catch (MyObjectNotFoundException e) {
//            try {
//                long id = workerService.usernameToId(s);
//                Worker worker = workerService.getById(id);
//                return User.withUsername(worker.getEmailAddress())
//                        .password(worker.getPassword())
//                        .roles("WORKER").build();
//            } catch (MyObjectNotFoundException e2) {
//                throw new UsernameNotFoundException("username " + s + " is not found");
//            }
//        }
//        return null;
    }

    @Override
    public long usernameToId(String username) {
        if (username.equals("admin@nturk.com")) {
            return 0;
        }

        return workerJPA.findByEmailAddress(username).map(MyUser::getId)
                .orElseGet(() -> userByUsername(requesterJPA, username).getId());

//        try {
//            return workerService.usernameToId(username);
//        } catch (MyObjectNotFoundException e) {
//            return requesterService.usernameToId(username);
//        }
    }

    @Override
    public boolean isUsernameExist(String username) {
        return workerJPA.findByEmailAddress(username).isPresent()
                || requesterJPA.findByEmailAddress(username).isPresent();
    }

    @Override
    public boolean isWorkerUsernameExist(String username) {
        return workerJPA.findByEmailAddress(username).isPresent();
    }
}
