package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LittleHorseConfiguration {

    @Bean
    LHConfig littleHorseConfig() {
        return new LHConfig();
    }

    @Bean
    LittleHorseBlockingStub littleHorseClient(LHConfig config) {
        return config.getBlockingStub();
    }
}
