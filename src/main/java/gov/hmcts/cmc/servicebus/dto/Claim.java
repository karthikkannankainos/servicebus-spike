package gov.hmcts.cmc.servicebus.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Claim implements Serializable {
    @Id
    private int id;
    private String claim;
    private String poisonMessage;

    public Claim(int id, String claim, String poisonMessage) {
        this.id = id;
        this.claim = claim;
        this.poisonMessage = poisonMessage;
    }

    public String getPoisonMessage() {
        return poisonMessage;
    }

    public void setPoisonMessage(String poisonMessage) {
        this.poisonMessage = poisonMessage;
    }

    public Claim(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }
}
