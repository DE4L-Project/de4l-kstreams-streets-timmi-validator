package io.de4l.kstreams.timmi.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaAppStarter {

    public static void main(String[] args) {
        SpringApplication.run(KafkaAppStarter.class, args);
    }

}
