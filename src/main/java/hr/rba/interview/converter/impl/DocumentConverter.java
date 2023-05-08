package hr.rba.interview.converter.impl;

import hr.rba.interview.config.properties.FileProperties;
import hr.rba.interview.constant.FileConstant;
import hr.rba.interview.converter.Converter;
import hr.rba.interview.domain.Document;
import hr.rba.interview.domain.DocumentStatus;
import hr.rba.interview.domain.Person;
import hr.rba.interview.enums.DocumentStatusValue;
import hr.rba.interview.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentConverter implements Converter<Person, Document> {

  private final FileProperties properties;
  private final PersonRepository personRepository;

  @Override
  public Document convert(final Person person) {
    final var documentName = mapDocumentName(person.getOib());

    return Document.builder()
        .name(documentName)
        .location(String.format("%s/%s/%s", FileConstant.WORKING_DIR,
            properties.getActiveDocumentLocationPath(), documentName))
        .status(mapStatus())
        .person(mapPerson(person.getId()))
        .build();
  }

  private String mapDocumentName(final String oib) {
    final var dateTime = LocalDateTime.now();
    final var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    final var formattedDateTime = dateTime.format(formatter);

    return String.format("%s_%s.txt", oib, formattedDateTime);
  }

  private DocumentStatus mapStatus() {
    final var status = DocumentStatusValue.findByName(DocumentStatusValue.CREATED.getName());

    return DocumentStatus.builder()
        .id(status.getId())
        .name(status.getName())
        .build();
  }

  private Person mapPerson(final Long id) {
    return personRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Person with ID: %d not found.", id)));
  }

}
