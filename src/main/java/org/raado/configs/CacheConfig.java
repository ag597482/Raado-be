package org.raado.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheConfig {
    public static final int DEFAULT_CONCURRENCY = 32;
    public static final int DEFAULT_EXPIRY = 90;  // 1.5 hours
    public static final int DEFAULT_REFRESH = 60; // 1 hour
    public static final int DEFAULT_MAX_ELEMENTS = 1024;

    @Min(30)
    @Max(86400)
    @Builder.Default
    private int concurrency = DEFAULT_CONCURRENCY;

    @Min(30)
    @Max(86400)
    @Builder.Default
    private int expiryInMinutes = DEFAULT_EXPIRY;

    @Min(30)
    @Max(86400)
    @Builder.Default
    private int refreshInMinutes = DEFAULT_REFRESH;

    @Min(10)
    @Max(1048576)
    @Builder.Default
    private int maxElements = DEFAULT_MAX_ELEMENTS;
}
