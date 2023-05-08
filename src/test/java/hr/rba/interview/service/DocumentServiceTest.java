package hr.rba.interview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hr.rba.interview.config.properties.FileProperties;
import hr.rba.interview.converter.impl.DocumentConverter;
import hr.rba.interview.domain.Document;
import hr.rba.interview.domain.DocumentStatus;
import hr.rba.interview.domain.Person;
import hr.rba.interview.domain.PersonStatus;
import hr.rba.interview.enums.DocumentStatusValue;
import hr.rba.interview.enums.PersonStatusValue;
import hr.rba.interview.model.DocumentStatusForm;
import hr.rba.interview.repository.DocumentRepository;
import hr.rba.interview.repository.DocumentStatusRepository;
import hr.rba.interview.service.impl.DocumentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  private DocumentService documentService;

  @Mock
  private DocumentConverter documentConverter;

  @Mock
  private DocumentRepository documentRepository;

  @Mock
  private DocumentStatusRepository documentStatusRepository;

  private final FileProperties properties = buildProperties();

  @BeforeEach
  void setUp() {
    documentService = new DocumentServiceImpl(
        documentConverter,
        documentRepository,
        documentStatusRepository,
        properties
    );
  }

  @Test
  void saveAndGenerateDocumentShould() {
    final var person = buildPerson();
    final var document = buildDocument();

    when(documentConverter.convert(person)).thenReturn(document);
    when(documentRepository.save(document)).thenReturn(document);

    documentService.saveAndGenerateDocument(person);

    final var generatedDocument = new File(document.getLocation());

    assertTrue(generatedDocument.exists());

    final var result = fetchAndReadFile(generatedDocument);

    assertEquals("junit, test, 12345678901, In process", result);

    generatedDocument.delete();

    verify(documentConverter, times(1)).convert(any());
    verify(documentRepository, times(1)).save(any());
  }

  @Test
  void updateDocumentStatusShouldThrowEntityNotFoundExceptionIfDocumentIsNotFound() {
    final var form = new DocumentStatusForm().oib("00000000000").statusId(2L);

    when(documentRepository.findByPersonOib(form.getOib())).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> documentService.updateDocumentStatus(form.getOib(), form.getStatusId())
    );

    assertEquals(exception.getMessage(), String.format("Document for person with OIB: %s not found.", form.getOib()));

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentStatusRepository, times(0)).findById(anyLong());
    verify(documentRepository, times(0)).save(any());
  }

  @Test
  void updateDocumentStatusShouldThrowEntityNotFoundExceptionIfStatusIsNotFound() {
    final var form = new DocumentStatusForm().oib("12345678901").statusId(999999L);
    final var document = buildDocument();

    when(documentRepository.findByPersonOib(form.getOib())).thenReturn(Optional.of(document));
    when(documentStatusRepository.findById(form.getStatusId())).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> documentService.updateDocumentStatus(form.getOib(), form.getStatusId())
    );

    assertEquals(exception.getMessage(), String.format("Document status with ID: %d not found.", form.getStatusId()));

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentStatusRepository, times(1)).findById(anyLong());
    verify(documentRepository, times(0)).save(any());
  }

  @Test
  void updateDocumentStatusShouldUpdateStatus() {
    final var form = new DocumentStatusForm().oib("12345678901").statusId(2L);
    final var document = buildDocument();
    final var status = DocumentStatus.builder().id(2L).name(DocumentStatusValue.IN_PROCESS.getName()).build();

    when(documentRepository.findByPersonOib(form.getOib())).thenReturn(Optional.of(document));
    when(documentStatusRepository.findById(form.getStatusId())).thenReturn(Optional.of(status));

    documentService.updateDocumentStatus(form.getOib(), form.getStatusId());

    assertEquals(document.getStatus(), status);
    assertEquals(document.getLocation(), String.format("%s/%s/%s", System.getProperty("user.dir"),
        properties.getActiveDocumentLocationPath(), document.getName()));

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentStatusRepository, times(1)).findById(anyLong());
    verify(documentRepository, times(1)).save(any());
  }

  @Test
  void updateDocumentStatusShouldUpdateStatusAndMoveFileToProcessedDirectory() {
    // create initial document
    final var person = buildPerson();
    final var document = buildDocument();
    when(documentConverter.convert(person)).thenReturn(document);
    when(documentRepository.save(document)).thenReturn(document);
    documentService.saveAndGenerateDocument(person);

    final var form = new DocumentStatusForm().oib("12345678901").statusId(3L);
    final var status = DocumentStatus.builder().id(2L).name(DocumentStatusValue.DONE.getName()).build();

    when(documentRepository.findByPersonOib(form.getOib())).thenReturn(Optional.of(document));
    when(documentStatusRepository.findById(form.getStatusId())).thenReturn(Optional.of(status));

    documentService.updateDocumentStatus(form.getOib(), form.getStatusId());

    assertEquals(document.getStatus(), status);
    assertEquals(document.getLocation(), String.format("%s/%s/%s", System.getProperty("user.dir"),
        properties.getProcessedDocumentLocationPath(), document.getName()));

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentStatusRepository, times(1)).findById(anyLong());
    verify(documentRepository, times(2)).save(any());

    final var generatedDocument = new File(document.getLocation());
    assertTrue(generatedDocument.exists());
    final var result = fetchAndReadFile(generatedDocument);
    assertEquals("junit, test, 12345678901, In process", result);
    generatedDocument.delete();
  }

  @Test
  void deleteDocumentPersonShouldThrowEntityNotFoundExceptionIfDocumentIsNotFound() {
    final var oib = "00000000000";

    when(documentRepository.findByPersonOib(oib)).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> documentService.deleteDocumentPerson(oib)
    );

    assertEquals(exception.getMessage(), String.format("Document for person with OIB: %s not found.", oib));

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentRepository, times(0)).save(any());
  }

  @Test
  void deleteDocumentPersonShould() {
    final var oib = "12345678901";
    final var document = buildDocument();

    when(documentRepository.findByPersonOib(oib)).thenReturn(Optional.of(document));

    documentService.deleteDocumentPerson(oib);

    assertNull(document.getPerson());

    verify(documentRepository, times(1)).findByPersonOib(anyString());
    verify(documentRepository, times(1)).save(any());
  }

  private Document buildDocument() {
    final var fileName = "12345678901_2023-05-07_21-48";

    return Document.builder()
        .id(1L)
        .name(fileName)
        .location(String.format("%s/%s/%s", System.getProperty("user.dir"),
            properties.getActiveDocumentLocationPath(), fileName))
        .status(DocumentStatus.builder().id(1L).name(DocumentStatusValue.CREATED.getName()).build())
        .person(buildPerson())
        .build();
  }

  private Person buildPerson() {
    return Person.builder()
        .id(1L)
        .oib("12345678901")
        .firstName("junit")
        .lastName("test")
        .status(PersonStatus.builder().id(2L).name(PersonStatusValue.IN_PROCESS.getName()).build())
        .build();
  }

  private String fetchAndReadFile(final File generatedDocument) {
    var result = new StringBuilder();

    try (final BufferedReader br = new BufferedReader(new FileReader(generatedDocument))) {
      String line;
      while ((line = br.readLine()) != null) {
        result.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result.toString();
  }

  private FileProperties buildProperties() {
    final var activeDocumentLocationPath = "/documents/active";
    final var processedDocumentLocationPath = "/documents/processed";

    return new FileProperties(activeDocumentLocationPath, processedDocumentLocationPath);
  }

}
