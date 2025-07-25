package cv.igrp.platform.process_manager_studio;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cv.igrp.platform.process_manager_studio.shared.config.ApplicationAuditorAware;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware", dateTimeProviderRef = "auditDateTimeProvider")
public class IgrpPlatformProcessManagerStudioApplication {
  @Bean
  public AuditorAware<String> auditAware() {
    return new ApplicationAuditorAware();
  }

  @Bean
  public DateTimeProvider auditDateTimeProvider() {
    return () -> Optional.of(LocalDateTime.now());
  }

  public static void main(String[] args) {
    SpringApplication.run(IgrpPlatformProcessManagerStudioApplication.class, args);
  }
}
