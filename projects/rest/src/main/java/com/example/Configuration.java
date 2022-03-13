package com.example;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class Configuration {

    @Singleton
    public TestBean testBean() {
        return new TestBean("I am the default test bean...");
    }
    
}
