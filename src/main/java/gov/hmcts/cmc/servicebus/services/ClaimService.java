package gov.hmcts.cmc.servicebus.services;

import gov.hmcts.cmc.servicebus.dto.Claim;
import gov.hmcts.cmc.servicebus.jdbc.ClaimRepository;
import gov.hmcts.cmc.servicebus.queue.IQueueMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimService implements IClaimService{

    @Autowired
    IQueueMessageSender messageSender;

    @Autowired
    ClaimRepository claimRepository;


    private static Logger logger = LoggerFactory.getLogger(ClaimService.class);

    @Transactional()
    public int saveClaim(Claim claim, boolean failure){


        try {
            logger.debug("Before saving the claim repository");
            claimRepository.save(claim);
            logger.debug("After saving the claim repository & Before sending message to the Queue");
            messageSender.sendMessage(claim);
            logger.debug("After successfully sending message to the queue.");
        }catch (Exception gene){
            gene.printStackTrace();
            logger.error("Error while saveClaim ", gene.getCause());
            throw new RuntimeException(gene.getCause());
        }


        if(failure){
            throw new RuntimeException("Some failure in the ClaimService, unable to recover");
        }
        return 1;
    }
}
