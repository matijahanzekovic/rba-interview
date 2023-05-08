package hr.rba.interview.controller;

import hr.rba.interview.api.PersonApi;
import hr.rba.interview.model.PersonDto;
import hr.rba.interview.model.PersonForm;
import hr.rba.interview.model.PersonStatusForm;
import hr.rba.interview.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PersonController implements PersonApi {

  private final PersonService service;

  @Override
  public ResponseEntity<PersonDto> createPerson(final PersonForm form) {
    return ResponseEntity.ok(service.createPerson(form));
  }

  @Override
  public ResponseEntity<PersonDto> getPerson(final String oib) {
    return ResponseEntity.ok(service.getPerson(oib));
  }

  @Override
  public ResponseEntity<String> deletePerson(final String oib) {
    service.deletePerson(oib);
    return ResponseEntity.ok("Successfully deleted person.");
  }

  @Override
  public ResponseEntity<String> updatePersonStatus(final PersonStatusForm form) {
    service.updatePersonStatus(form);
    return ResponseEntity.ok("Successfully updated person status.");
  }

}
