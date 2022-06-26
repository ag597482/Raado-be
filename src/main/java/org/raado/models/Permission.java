package org.raado.models;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Map;

@Data
public class Permission {
    @BsonProperty
    private ProcessName processName;
    @BsonProperty
    private boolean write;
    @BsonProperty
    private Map<String, Integer> entriesRate;
}
