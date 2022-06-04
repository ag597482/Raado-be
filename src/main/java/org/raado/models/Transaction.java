package org.raado.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @ObjectId
    @Id
    private String transactionId;
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
