package org.raado.models;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class Permission {
    @BsonProperty
    private ProcessName processName;
    @BsonProperty
    private boolean write;
}
