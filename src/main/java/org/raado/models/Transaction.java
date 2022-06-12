package org.raado.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @BsonProperty
    private String transactionId;
    @BsonProperty
    private ProcessName fromProcess;
    @BsonProperty
    private ProcessName toProcess;
    @BsonProperty
    private Date timeOfTransaction;
    @BsonProperty
    private Date timeOfApproval;
    @BsonProperty
    private TransactionStatus status;
    @BsonProperty
    private String comment;
    @BsonProperty
    private Map<String, Integer> entries;
    @BsonProperty
    private String fromUserId;
    @BsonProperty
    private String toUserId;
}
