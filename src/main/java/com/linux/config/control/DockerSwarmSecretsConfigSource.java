package com.linux.config.control;

import io.quarkus.runtime.annotations.StaticInitSafe;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * <a href="https://docs.docker.com/engine/swarm/secrets">Docker Swarm Secrets</a> Config Source
 *
 * @see <a href="https://quarkus.io/guides/config-extending-support">quarkus-custom-config-source</a>
 * @see org.eclipse.microprofile.config.spi.ConfigSource
 */
@StaticInitSafe
public class DockerSwarmSecretsConfigSource implements ConfigSource {
    private static final Logger l = Logger.getLogger(DockerSwarmSecretsConfigSource.class);
    @Override
    public int getOrdinal() {
        return 275;
    }

    @Override
    public Set<String> getPropertyNames() {
        try(var secrets = Files.find(Paths.get("/run/secrets/"), 1, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())) {
            return secrets
                .filter(Files::isReadable)
                .map(path -> path.getFileName().toString())
                .filter(fileName -> !fileName.isEmpty())
                .collect(Collectors.toSet());
        } catch (IOException e) {
            l.debug("Error in reading secret files", e);
        }
        l.info("returning empty set");
        return Set.of();
    }

    @Override
    public String getValue(String propertyName) {
        try {
            return Files.readString(Paths.get("/run/secrets", propertyName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            l.debugv(e,"Error in reading secret {0}", propertyName);
        }
        l.info("returning null");
        return null;
    }

    @Override
    public String getName() {
        return DockerSwarmSecretsConfigSource.class.getSimpleName();
    }
}