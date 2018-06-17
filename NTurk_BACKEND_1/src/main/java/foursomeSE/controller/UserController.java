package foursomeSE.controller;

import foursomeSE.entity.communicate.ExchangeRequest;
import foursomeSE.entity.communicate.SimpleCWorker;
import foursomeSE.entity.statistics.UserActivity;
import foursomeSE.entity.statistics.UserDistribution;
import foursomeSE.entity.statistics.UserGrowth;
import foursomeSE.entity.statistics.UserNum;
import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.error.MyErrorType;
import foursomeSE.error.MyNotValidException;
import foursomeSE.error.MyObjectNotFoundException;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.user.HybridUserService;
import foursomeSE.service.user.upper.UpperRequesterService;
import foursomeSE.service.user.upper.UpperUserService;
import foursomeSE.service.user.upper.UpperWorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static foursomeSE.util.ConvenientFunctions.listConvert;

@RestController
public class UserController {
    //  这里三个Service感觉怪怪的，别的地方好像也有这种情况
    private UpperWorkerService workerService;
    private UpperRequesterService requesterService;
    private HybridUserService userUtilService;

    public UserController(UpperWorkerService workerService, UpperRequesterService requesterService, HybridUserService userUtilService) {
        this.workerService = workerService;
        this.requesterService = requesterService;
        this.userUtilService = userUtilService;
    }

    @RequestMapping(value = "/auth/username/{username}",
            method = RequestMethod.POST) // 我也不知道应该用哪个method
    public ResponseEntity<Boolean> isExistUsername(@PathVariable String username) {
        boolean b = userUtilService.isUsernameExist(username);
        return ResponseEntity.ok(b);
    }

    @RequestMapping(value = "/auth/worker",
            method = RequestMethod.POST)
    public ResponseEntity<?> signUpWorker(@RequestBody Worker worker) {
        if (userUtilService.isUsernameExist(worker.getEmailAddress())) {
            return new ResponseEntity<>(new MyErrorType("username exists"), HttpStatus.BAD_REQUEST);
        }
        workerService.add(worker);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/auth/requester",
            method = RequestMethod.POST)
    public ResponseEntity<?> signUpRequester(@RequestBody Requester requester) {
        if (userUtilService.isUsernameExist(requester.getEmailAddress())) {
            return new ResponseEntity<>(new MyErrorType("username exists"), HttpStatus.BAD_REQUEST);
        }
        requesterService.add(requester);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/worker",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> updateWorker(@RequestBody Worker worker) {
        try {
            workerService.update(worker);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MyObjectNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/requester/allWorkers",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<List<SimpleCWorker>> getAllWorkers() {
        List<SimpleCWorker> results = workerService.getAllSimple();

        if (results.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/myInfo",
            method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('REQUESTER', 'WORKER')")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        if (workerService.isUsernameExist(username)) {
            return new ResponseEntity<>(workerService.getById(workerService.usernameToId(username)), HttpStatus.OK);
        }
        if (requesterService.isUsernameExist(username)) {
            return new ResponseEntity<>(requesterService.getById(requesterService.usernameToId(username)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 这一句不可能走到的
    }

    @RequestMapping(value = "/myInfo/editTags",
    method = RequestMethod.POST)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> editTags(@RequestHeader("Authorization") String token,
                                      @RequestBody List<String> tagNames) {
        String username = JwtUtil.getUsernameFromToken(token);

        workerService.editTags(tagNames, username);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }


    @RequestMapping(value = "/requester",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> updateRequester(@RequestBody Requester requester) {
        try {
            requesterService.update(requester);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MyObjectNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/admin/overview/userNum",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserNum>> getUserNum() {
        List<UserNum> result = new ArrayList<>();

        result.add(workerService.getUserNum());
        result.add(requesterService.getUserNum());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/worker/workerGrowth",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGrowth>> getWorkerGrowth() {
        List<UserGrowth> result = workerService.getUserGrowth();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/worker/workerDis",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDistribution>> getWorkerDistribution() {
        List<UserDistribution> result = workerService.getUserDistribution();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/worker/workerActive",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserActivity>> getWorkerActive() {
        List<UserActivity> result = workerService.getUserActivity();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/requester/requesterGrowth",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGrowth>> getRequesterGrowth() {
        List<UserGrowth> result = requesterService.getUserGrowth();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/requester/requesterDis",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDistribution>> getRequesterDistribution() {
        List<UserDistribution> result = requesterService.getUserDistribution();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/requester/requesterActive",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserActivity>> getRequesterActive() {
        List<UserActivity> result = requesterService.getUserActivity();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/userProfile/worker/exchange/",
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> workerExchange(
            @RequestHeader("Authorization") String token,
            @RequestBody ExchangeRequest exchangeRequest) {
        String username = JwtUtil.getUsernameFromToken(token);

        try {
            workerService.exchange(exchangeRequest, username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MyNotValidException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/userProfile/requester/exchange/",
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> requesterExchange(
            @RequestHeader("Authorization") String token,
            @RequestBody ExchangeRequest exchangeRequest) {
        String username = JwtUtil.getUsernameFromToken(token);

        requesterService.exchange(exchangeRequest, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
