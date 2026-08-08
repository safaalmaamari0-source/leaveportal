package com.agileoracles.leave_portal_app.config;

import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OciConfiguration {

    @Bean
    public ObjectStorage objectStorageClient(
            @Value("${oci.config-file}") String configFile,
            @Value("${oci.profile:DEFAULT}") String profile
    ) throws IOException {

        ConfigFileAuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(
                        configFile,
                        profile
                );

        return ObjectStorageClient.builder()
                .build(provider);
    }
}
