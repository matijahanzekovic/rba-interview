package hr.rba.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hr.rba.interview.converter.impl.PersonDtoConverter;
import hr.rba.interview.converter.impl.PersonFormConverter;
import hr.rba.interview.domain.Document;
import hr.rba.interview.domain.DocumentStatus;
import hr.rba.interview.domain.Person;
import hr.rba.interview.domain.PersonStatus;
import hr.rba.interview.enums.DocumentStatusValue;
import hr.rba.interview.enums.PersonStatusValue;
import hr.rba.interview.model.CodebookDto;
import hr.rba.interview.model.PersonDto;
import hr.rba.interview.model.PersonForm;
import hr.rba.interview.model.PersonStatusForm;
import hr.rba.interview.repository.PersonRepository;
import hr.rba.interview.repository.PersonStatusRepository;
import hr.rba.interview.service.impl.PersonServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

  private static final String ACTIVE_DOCUMENT_PATH = "documents/active";

  private PersonService personService;

  @Mock
  private PersonDtoConverter personDtoConverter;

  @Mock
  private PersonFormConverter personFormConverter;

  @Mock
  private PersonRepository personRepository;

  @Mock
  private PersonStatusRepository personStatusRepository;

  @Mock
  private DocumentService documentService;

  @BeforeEach
  void setUp() {
    personService = new PersonServiceImpl(
        personDtoConverter,
        personFormConverter,
        personRepository,
        personStatusRepository,
        documentService
    );
  }

  @Test
  void createPersonShouldThrowIllegalArgumentExceptionIfPersonAlreadyExists() {
    final var form = buildPersonForm();
    final var person = buildPerson();

    when(personRepository.findByOib(person.getOib())).thenReturn(Optional.of(person));

    final var exception = assertThrows(
        IllegalArgumentException.class,
        () -> personService.createPerson(form)
    );

    assertEquals(exception.getMessage(), String.format("Person with OIB: %s already exists.", person.getOib()));

    verify(personRepository, times(1)).findByOib(anyString());
    verify(personFormConverter, times(0)).convert(any());
    verify(personRepository, times(0)).save(any());
    verify(personDtoConverter, times(0)).convert(any());
  }

  @Test
  void createPersonShould() {
    final var form = buildPersonForm();
    final var person = buildPerson();
    final var dto = buildPersonDto();

    when(personRepository.findByOib(person.getOib())).thenReturn(Optional.empty());
    when(personFormConverter.convert(form)).thenReturn(person);
    when(personRepository.save(person)).thenReturn(person);
    when(personDtoConverter.convert(person)).thenReturn(dto);

    final var result = personService.createPerson(form);

    assertThat(result).usingRecursiveComparison().isEqualTo(dto);

    verify(personRepository, times(1)).findByOib(anyString());
    verify(personFormConverter, times(1)).convert(any());
    verify(personRepository, times(1)).save(any());
    verify(personDtoConverter, times(1)).convert(any());
  }

  @Test
  void getPersonShouldThrowEntityNotFoundExceptionIfPersonDoesNotExist() {
    final var oib = "00000000000";

    when(personRepository.findByOib(oib)).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> personService.getPerson(oib)
    );

    assertEquals(exception.getMessage(), String.format("Person with OIB: %s not found.", oib));

    verify(personRepository, times(1)).findByOib(any());
    verify(personStatusRepository, times(0)).findById(anyLong());
    verify(personRepository, times(0)).save(any());
    verify(documentService, times(0)).saveAndGenerateDocument(any());
    verify(personDtoConverter, times(0)).convert(any());
  }

  @Test
  void getPersonShouldFetchPerson() {
    final var oib = "12345678901";
    final var document = buildDocument();
    final var person = buildPerson();
    person.setDocument(document);
    final var dto = buildPersonDto();

    when(personRepository.findByOib(oib)).thenReturn(Optional.of(person));
    when(personDtoConverter.convert(person)).thenReturn(dto);

    final var result = personService.getPerson(oib);

    assertThat(result).usingRecursiveComparison().isEqualTo(dto);

    verify(personRepository, times(1)).findByOib(any());
    verify(personStatusRepository, times(0)).findById(anyLong());
    verify(personRepository, times(0)).save(any());
    verify(documentService, times(0)).saveAndGenerateDocument(any());
    verify(personDtoConverter, times(1)).convert(any());
  }

  @Test
  void getPersonShouldFetchPersonAndUpdatePersonStatusAndGenerateDocument() {
    final var oib = "12345678901";
    final var person = buildPerson();
    final var personStatus = PersonStatus.builder().id(2L).name(PersonStatusValue.IN_PROCESS.getName()).build();
    final var dto = buildPersonDto();

    when(personRepository.findByOib(oib)).thenReturn(Optional.of(person));
    when(personStatusRepository.findById(2L)).thenReturn(Optional.of(personStatus));
    doNothing().when(documentService).saveAndGenerateDocument(person);
    when(personDtoConverter.convert(person)).thenReturn(dto);

    final var result = personService.getPerson(oib);

    assertThat(result).usingRecursiveComparison().isEqualTo(dto);

    verify(personRepository, times(1)).findByOib(any());
    verify(personStatusRepository, times(1)).findById(anyLong());
    verify(personRepository, times(1)).save(any());
    verify(documentService, times(1)).saveAndGenerateDocument(any());
    verify(personDtoConverter, times(1)).convert(any());
  }

  @Test
  void deletePersonShouldThrowEntityNotFoundExceptionIfPersonDoesNotExist() {
    final var oib = "00000000000";

    when(personRepository.findByOib(oib)).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> personService.deletePerson(oib)
    );

    assertEquals(exception.getMessage(), String.format("Person with OIB: %s not found.", oib));

    verify(personRepository, times(1)).findByOib(anyString());
    verify(documentService, times(0)).updateDocumentStatus(anyString(), anyLong());
    verify(documentService, times(0)).deleteDocumentPerson(anyString());
    verify(personRepository, times(0)).deleteByOib(any());
  }

  @Test
  void deletePersonShould() {
    final var oib = "12345678901";
    final var person = buildPerson();

    when(personRepository.findByOib(oib)).thenReturn(Optional.of(person));
    doNothing().when(documentService).updateDocumentStatus(oib, DocumentStatusValue.INACTIVE.getId());
    doNothing().when(documentService).deleteDocumentPerson(oib);
    doNothing().when(personRepository).deleteByOib(oib);

    personService.deletePerson(oib);

    verify(personRepository, times(1)).findByOib(anyString());
    verify(documentService, times(1)).updateDocumentStatus(anyString(), anyLong());
    verify(documentService, times(1)).deleteDocumentPerson(anyString());
    verify(personRepository, times(1)).deleteByOib(any());
  }

  @Test
  void updatePersonStatusShouldThrowEntityNotFoundExceptionIfPersonIsNotFound() {
    final var form = new PersonStatusForm().oib("00000000000").statusId(3L);

    when(personRepository.findByOib(form.getOib())).thenReturn(Optional.empty());

    final var exception = assertThrows(
        EntityNotFoundException.class,
        () -> personService.updatePersonStatus(form)
    );

    assertEquals(exception.getMessage(), String.format("Person with OIB: %s not found.", form.getOib()));

    verify(personRepository, times(1)).findByOib(anyString());
    verify(personStatusRepository, times(0)).findById(anyLong());
    verify(personRepository, times(0)).save(any());
  }

  @Test
  void updatePersonStatus() {
    final var form = new PersonStatusForm().oib("12345678901").statusId(3L);
    final var person = buildPerson();
    final var personStatus = PersonStatus.builder().id(3L).name(PersonStatusValue.DONE.getName()).build();

    when(personRepository.findByOib(form.getOib())).thenReturn(Optional.of(person));
    when(personStatusRepository.findById(3L)).thenReturn(Optional.of(personStatus));
    when(personRepository.save(person)).thenReturn(person);

    personService.updatePersonStatus(form);

    verify(personRepository, times(1)).findByOib(anyString());
    verify(personStatusRepository, times(1)).findById(anyLong());
    verify(personRepository, times(1)).save(any());
  }

  private PersonForm buildPersonForm() {
    return new PersonForm()
        .oib("12345678901")
        .firstName("junit")
        .lastName("test");
  }

  private PersonDto buildPersonDto() {
    return new PersonDto()
        .id(1L)
        .oib("12345678901")
        .firstName("junit")
        .lastName("test")
        .status(new CodebookDto().key(1L).value(PersonStatusValue.INITIAL.getName()));
  }

  private Person buildPerson() {
    return Person.builder()
        .id(1L)
        .oib("12345678901")
        .firstName("junit")
        .lastName("test")
        .status(PersonStatus.builder().id(1L).name(PersonStatusValue.INITIAL.getName()).build())
        .build();
  }

  private Document buildDocument() {
    final var fileName = "12345678901_2023-05-07_21-48";

    return Document.builder()
        .id(1L)
        .name(fileName)
        .location(String.format("%s/%s/%s", System.getProperty("user.dir"),
            ACTIVE_DOCUMENT_PATH, fileName))
        .status(DocumentStatus.builder().id(1L).name(DocumentStatusValue.CREATED.getName()).build())
        .person(buildPerson())
        .build();
  }

}
