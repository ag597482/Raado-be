package org.raado.models;

import lombok.NonNull;
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
    @NonNull
    @BsonProperty
    private ProcessName fromProcess;
    @NonNull
    @BsonProperty
    private ProcessName toProcess;
    @BsonProperty
    private Date timeOfTransaction;
    @BsonProperty
    private Date timeOfApproval;
    @NonNull
    @BsonProperty
    private TransactionStatus status;
    @BsonProperty
    private String comment;
    @NonNull
    @BsonProperty
    private Map<String, Integer> entries;
    @NonNull
    @BsonProperty
    private String fromUserId;
    @NonNull
    @BsonProperty
    private String toUserId;
    private String fromUserName;
    private String toUserName;
    private int amount;
}
