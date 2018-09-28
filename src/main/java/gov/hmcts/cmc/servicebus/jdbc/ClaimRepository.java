package gov.hmcts.cmc.servicebus.jdbc;

import gov.hmcts.cmc.servicebus.dto.Claim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, Long> {

    Claim save(Claim claim);

    Optional<Claim> findById(Long id);

}
