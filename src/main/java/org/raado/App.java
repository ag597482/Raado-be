package org.raado;

import com.google.inject.Stage;
import com.mongodb.DB;
import com.mongodb.Mongo;
import io.dropwizard.Application;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import net.vz.mongodb.jackson.JacksonDBCollection;
import org.raado.exceptions.RaadoExceptionMapper;
import org.raado.models.User;
import org.raado.modules.GuiceModule;
import org.raado.resources.UserResource;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class App extends Application<AppConfig> {

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "raado";
    }

    @Override
    public void initialize(final Bootstrap<AppConfig> bootstrap) {
        final SwaggerBundle<AppConfig> swaggerBundle = new SwaggerBundle<AppConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.getSwagger();
            }
        };
        final GuiceBundle guiceBundle = GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new GuiceModule())
                .build(Stage.PRODUCTION);
        bootstrap.addBundle(swaggerBundle);
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(final AppConfig configuration,
                    final Environment environment) {
        environment.jersey().register(new RaadoExceptionMapper());
    }

}
