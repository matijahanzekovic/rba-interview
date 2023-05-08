package hr.rba.interview.service;

import hr.rba.interview.domain.Person;

public interface DocumentService {

  void saveAndGenerateDocument(Person person);
  void updateDocumentStatus(String oib, Long statusId);
  void deleteDocumentPerson(String oib);
  void deleteProcessedDocuments();

}
