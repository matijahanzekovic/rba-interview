package hr.rba.interview.controller;

import hr.rba.interview.api.DocumentApi;
import hr.rba.interview.model.DocumentStatusForm;
import hr.rba.interview.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DocumentController implements DocumentApi {

  private final DocumentService documentService;

  @Override
  public ResponseEntity<String> updateDocumentStatus(final DocumentStatusForm form) {
    documentService.updateDocumentStatus(form.getOib(), form.getStatusId());
    return ResponseEntity.ok("Successfully updated document status.");
  }

}
