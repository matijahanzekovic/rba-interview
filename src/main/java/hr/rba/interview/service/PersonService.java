package hr.rba.interview.service;

import hr.rba.interview.model.PersonDto;
import hr.rba.interview.model.PersonForm;
import hr.rba.interview.model.PersonStatusForm;

public interface PersonService {

  PersonDto createPerson(PersonForm form);
  PersonDto getPerson(String oib);
  void deletePerson(String oib);
  void updatePersonStatus(PersonStatusForm form);

}
