package gov.hmcts.cmc.servicebus.services;

import gov.hmcts.cmc.servicebus.dto.Claim;

public interface IClaimService {

     int saveClaim(Claim claim, boolean failure);

     void postTopicMessage(Claim claim);
}
