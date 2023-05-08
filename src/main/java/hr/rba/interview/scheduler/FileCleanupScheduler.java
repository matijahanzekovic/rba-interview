package hr.rba.interview.scheduler;

import hr.rba.interview.config.properties.FileProperties;
import hr.rba.interview.service.DocumentService;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileCleanupScheduler {

  private final FileProperties properties;
  private final DocumentService documentService;

  @Scheduled(cron = "0 0 3 * * ?")
  public void deleteFiles() {
    final var directory = new File(properties.getProcessedDocumentLocationPath());
    final var files = directory.listFiles();

    if (Objects.nonNull(files)) {
      Arrays.stream(files).forEach(file -> {
        if (!file.isDirectory() && file.getName().endsWith(".txt")) {
          file.delete();
        }
      });
    }

    documentService.deleteProcessedDocuments();
  }

}
