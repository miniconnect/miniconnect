package hu.webarticum.miniconnect.rest;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

@MicronautTest
class RestTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        assertThat(application.isRunning()).isTrue();
    }

}
