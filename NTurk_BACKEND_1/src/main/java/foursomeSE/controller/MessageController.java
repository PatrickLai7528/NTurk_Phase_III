package foursomeSE.controller;

import foursomeSE.entity.message.Message;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.message.UpperMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    private UpperMessageService upperMessageService;

    public MessageController(UpperMessageService upperMessageService) {
        this.upperMessageService = upperMessageService;
    }

    @RequestMapping(value = "/message",
            method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('WORKER', 'REQUESTER')")
    public ResponseEntity<?> getMessage(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsernameFromToken(token);

        List<Message> result = upperMessageService.getByUsername(username);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
