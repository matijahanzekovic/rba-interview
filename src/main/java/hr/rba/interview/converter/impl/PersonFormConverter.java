package hr.rba.interview.converter.impl;

import hr.rba.interview.converter.Converter;
import hr.rba.interview.domain.Person;
import hr.rba.interview.domain.PersonStatus;
import hr.rba.interview.enums.PersonStatusValue;
import hr.rba.interview.model.PersonForm;
import org.springframework.stereotype.Component;

@Component
public class PersonFormConverter implements Converter<PersonForm, Person> {

  @Override
  public Person convert(final PersonForm form) {
    return Person.builder()
        .oib(form.getOib())
        .firstName(form.getFirstName())
        .lastName(form.getLastName())
        .status(mapStatus())
        .build();
  }

  private PersonStatus mapStatus() {
    final var status = PersonStatusValue.findByName(PersonStatusValue.INITIAL.getName());

    return PersonStatus.builder()
        .id(status.getId())
        .name(status.getName())
        .build();
  }

}
