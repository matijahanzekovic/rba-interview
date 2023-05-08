package hr.rba.interview.converter.impl;

import hr.rba.interview.converter.Converter;
import hr.rba.interview.domain.Person;
import hr.rba.interview.domain.PersonStatus;
import hr.rba.interview.model.CodebookDto;
import hr.rba.interview.model.PersonDto;
import org.springframework.stereotype.Component;

@Component
public class PersonDtoConverter implements Converter<Person, PersonDto> {

  @Override
  public PersonDto convert(final Person person) {
    return new PersonDto()
        .id(person.getId())
        .oib(person.getOib())
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .status(mapStatus(person.getStatus()));
  }

  private CodebookDto mapStatus(final PersonStatus status) {
    return new CodebookDto()
        .key(status.getId())
        .value(status.getName());
  }

}
