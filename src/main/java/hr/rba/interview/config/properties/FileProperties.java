package hr.rba.interview.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("hr.rba.interview")
public class FileProperties {

  @NotBlank
  private final String activeDocumentLocationPath;

  @NotBlank
  private final String processedDocumentLocationPath;

}
