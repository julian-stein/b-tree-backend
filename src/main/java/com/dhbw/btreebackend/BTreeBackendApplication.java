package com.dhbw.btreebackend;

import com.dhbw.btreebackend.btreeimplementation.BTree;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The basic SpringBootApplication class to start the application and create the BTree object used by other classes.
 *
 * @author Elias Müller
 * @version 1.0
 */
@SpringBootApplication
public class BTreeBackendApplication {

    /**
     * This Bean provides the BTree with a default order of 5 and injects it to the other classes.
     * @return BTree: The BTree itself.
     */
    @Bean
    public BTree bTree () {
        return new BTree(5);
    }

    public static void main(String[] args) {
        SpringApplication.run(BTreeBackendApplication.class, args);
    }

}
