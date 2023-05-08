package hr.rba.interview.service.impl;

import hr.rba.interview.config.properties.FileProperties;
import hr.rba.interview.constant.FileConstant;
import hr.rba.interview.converter.impl.DocumentConverter;
import hr.rba.interview.domain.Document;
import hr.rba.interview.domain.Person;
import hr.rba.interview.enums.DocumentStatusValue;
import hr.rba.interview.exception.FileException;
import hr.rba.interview.repository.DocumentRepository;
import hr.rba.interview.repository.DocumentStatusRepository;
import hr.rba.interview.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

  private final DocumentConverter documentConverter;
  private final DocumentRepository documentRepository;
  private final DocumentStatusRepository documentStatusRepository;
  private final FileProperties properties;

  @Override
  @Transactional
  public void saveAndGenerateDocument(final Person person) {
    final var document = documentRepository.save(documentConverter.convert(person));

    generateFile(person, document);
  }

  @Override
  @Transactional
  public void updateDocumentStatus(final String oib, final Long statusId) {
    final var document = getDocumentByPersonOib(oib);

    final var status = documentStatusRepository.findById(statusId)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Document status with ID: %d not found.", statusId)));

    document.setStatus(status);

    if (isDocumentStatusDoneOrInactive(statusId)) {
      document.setLocation(String.format("%s/%s/%s", FileConstant.WORKING_DIR, properties.getProcessedDocumentLocationPath(),
          document.getName()));
      moveFileToDirectoryForRemoval(document.getName());
    }

    documentRepository.save(document);
  }

  @Override
  @Transactional
  public void deleteDocumentPerson(final String oib) {
    final var document = getDocumentByPersonOib(oib);

    document.setPerson(null);

    documentRepository.save(document);
  }

  @Override
  @Transactional
  public void deleteProcessedDocuments() {
    documentRepository.deleteProcessedDocuments(properties.getProcessedDocumentLocationPath());
  }

  private void generateFile(final Person person, final Document document) {
    final var fileName = document.getName();
    final var filePath = Paths.get(String.format("%s/%s/%s", FileConstant.WORKING_DIR,
        properties.getActiveDocumentLocationPath(), fileName));
    final var file = filePath.toFile();

    try (final var writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(String.format("%s, %s, %s, %s", person.getFirstName(), person.getLastName(),
          person.getOib(), person.getStatus().getName()));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  private Document getDocumentByPersonOib(final String oib) {
    return documentRepository.findByPersonOib(oib)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Document for person with OIB: %s not found.", oib)));
  }

  private boolean isDocumentStatusDoneOrInactive(final Long statusId) {
    return DocumentStatusValue.DONE.getId().equals(statusId) ||
        DocumentStatusValue.INACTIVE.getId().equals(statusId);
  }

  private void moveFileToDirectoryForRemoval(final String fileName) {
    final var source = Paths.get(String.format("%s/%s/%s", FileConstant.WORKING_DIR,
        properties.getActiveDocumentLocationPath(), fileName));
    final var target = Paths.get(String.format("%s/%s/%s", FileConstant.WORKING_DIR,
        properties.getProcessedDocumentLocationPath(), fileName));

    try {
      Files.move(source, target);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

}
