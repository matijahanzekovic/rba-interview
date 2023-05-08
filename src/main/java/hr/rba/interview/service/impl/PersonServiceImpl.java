package hr.rba.interview.service.impl;

import hr.rba.interview.converter.impl.PersonDtoConverter;
import hr.rba.interview.converter.impl.PersonFormConverter;
import hr.rba.interview.domain.Person;
import hr.rba.interview.enums.DocumentStatusValue;
import hr.rba.interview.enums.PersonStatusValue;
import hr.rba.interview.model.PersonDto;
import hr.rba.interview.model.PersonForm;
import hr.rba.interview.model.PersonStatusForm;
import hr.rba.interview.repository.PersonRepository;
import hr.rba.interview.repository.PersonStatusRepository;
import hr.rba.interview.service.DocumentService;
import hr.rba.interview.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

  private final PersonDtoConverter personDtoConverter;
  private final PersonFormConverter personFormConverter;
  private final PersonRepository personRepository;
  private final PersonStatusRepository personStatusRepository;
  private final DocumentService documentService;

  @Override
  @Transactional
  public PersonDto createPerson(final PersonForm form) {
    throwIfPersonAlreadyExists(form.getOib());

    final var person = personRepository.save(personFormConverter.convert(form));

    return personDtoConverter.convert(person);
  }

  @Override
  @Transactional
  public PersonDto getPerson(final String oib) {
    final var person = getPersonByOib(oib);

    if (Objects.isNull(person.getDocument())) {
      updatePersonStatus(person, PersonStatusValue.IN_PROCESS.getId());
      documentService.saveAndGenerateDocument(person);
    }

    return personDtoConverter.convert(person);
  }

  @Override
  @Transactional
  public void deletePerson(final String oib) {
    throwIfPersonDoesNotExist(oib);

    documentService.updateDocumentStatus(oib, DocumentStatusValue.INACTIVE.getId());
    documentService.deleteDocumentPerson(oib);

    personRepository.deleteByOib(oib);
  }

  @Override
  @Transactional
  public void updatePersonStatus(final PersonStatusForm form) {
    final var person = getPersonByOib(form.getOib());

    updatePersonStatus(person, form.getStatusId());
  }

  private Person getPersonByOib(final String oib) {
    return personRepository.findByOib(oib)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Person with OIB: %s not found.", oib)));
  }

  private void updatePersonStatus(final Person person, final Long statusId) {
    final var status = personStatusRepository.findById(statusId)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Person status with ID: %d not found.", statusId)));

    person.setStatus(status);

    personRepository.save(person);
  }

  private void throwIfPersonAlreadyExists(final String oib) {
    final var person = personRepository.findByOib(oib);

    if (person.isPresent()) {
      throw new IllegalArgumentException(String.format("Person with OIB: %s already exists.", oib));
    }
  }

  private void throwIfPersonDoesNotExist(final String oib) {
    final var person = personRepository.findByOib(oib);

    if (person.isEmpty()) {
      throw new EntityNotFoundException(String.format("Person with OIB: %s not found.", oib));
    }
  }

}
