package foursomeSE.controller;

import foursomeSE.entity.communicate.CInspectionContract;
import foursomeSE.security.JwtUtil;
import foursomeSE.service.inspection.UpperInspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class InspectionController {
    private UpperInspectionService inspectionService;

    public InspectionController(UpperInspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @RequestMapping(value = "/inspect",
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> addInspections(@RequestHeader("Authorization") String token,
                                            @RequestBody CInspectionContract cInspectionContract) {
        String username = JwtUtil.getUsernameFromToken(token);
        inspectionService.add(cInspectionContract, username);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/inspect/imgName/{imgName}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<?> getBestKth(@RequestHeader("Authorization") String token,
                                           @PathVariable("imgName") String imgName) {
        String username = JwtUtil.getUsernameFromToken(token);
        return new ResponseEntity<>(inspectionService.getBestKth(imgName, username), HttpStatus.OK);
    }
}
