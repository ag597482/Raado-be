package org.raado;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Data;
import org.raado.configs.CacheConfig;
import org.raado.configs.MongoConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AppConfig extends Configuration {

    @NotNull
    private String defaultName;

    @NotNull
    private String userCollectionName;

    @NotNull
    private String transactionCollectionName;

    @NotNull
    private String staticResourceCollectionName;

    @NotNull
    @Valid
    private SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    @NotNull
    @Valid
    private MongoConfig mongoConfig;

    @NotNull
    @Valid
    private CacheConfig cacheConfig;
}
