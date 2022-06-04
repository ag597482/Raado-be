package org.raado.models;

import lombok.Data;

@Data
public class Permission {
    private ProcessName processName;
    private boolean write;
}
