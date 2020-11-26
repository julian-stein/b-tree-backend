package com.dhbw.btreebackend;

import com.dhbw.btreebackend.btreeimplementation.BTree;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.management.MXBean;

@SpringBootApplication
public class BTreeBackendApplication {

    /**
     * This Bean provides the BTree and injects it to the other classes.
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
