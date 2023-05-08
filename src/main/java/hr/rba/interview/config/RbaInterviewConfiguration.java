package hr.rba.interview.config;

import hr.rba.interview.config.properties.FileProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({FileProperties.class})
@EnableScheduling
public class RbaInterviewConfiguration {

  private final FileProperties properties;

}
