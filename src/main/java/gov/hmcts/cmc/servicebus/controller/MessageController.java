package gov.hmcts.cmc.servicebus.controller;

import gov.hmcts.cmc.servicebus.dto.Claim;
import gov.hmcts.cmc.servicebus.services.IClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    IClaimService claimService;

    @PostMapping("/queue")
    public ResponseEntity<Void> postMessageJoiningTransactionWithDB(@RequestBody Claim claim, @RequestParam("failure") boolean failure){

        logger.debug(String.format("Recieved the request to save claim with body %s ",claim.getClaim()));

        claimService.saveClaim(claim, failure);

        logger.debug(String.format("Completed the save operation for Claim with Id %d ",claim.getId()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/topic")
    public ResponseEntity<Void> postMessageForTopic(@RequestBody Claim claim, @RequestParam("failure") boolean failure){

        logger.debug(String.format("Recieved the request to Send TOpic Message with body %s ",claim.getClaim()));

        claimService.postTopicMessage(claim);

        logger.debug(String.format("Completed the send Topic Message operation for Claim with Id %d ",claim.getId()));
        return ResponseEntity.ok().build();
    }

}
