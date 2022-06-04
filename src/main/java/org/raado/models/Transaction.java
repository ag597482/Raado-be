package org.raado.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @JsonIgnore
    private String _id;

    @JsonIgnore
    private final String transactionId = UUID.randomUUID().toString();

    private ProcessName fromProcess;
    private ProcessName toProcess;
    private long timeOfTransaction;
    private long timeOfApproval;
    private TransactionStatus status;
    private String comment;
    private Map<String, Integer> entries;
    private String fromUserPhone;
    private String toUserPhone;
}
