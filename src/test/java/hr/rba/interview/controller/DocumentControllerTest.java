package hr.rba.interview.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.rba.interview.model.DocumentStatusForm;
import hr.rba.interview.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

  @MockBean
  private DocumentService documentService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void updateDocumentStatusShould() throws Exception {
    final var form = buildDocumentStatusForm();

    doNothing().when(documentService).updateDocumentStatus(form.getOib(), form.getStatusId());

    mockMvc.perform(post("/api/document/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully updated document status."));
  }

  @Test
  void updateDocumentStatusShouldThrowExceptionIfDocumentIsNotFound() throws Exception {
    final var form = buildDocumentStatusForm();

    doThrow(EntityNotFoundException.class).when(documentService).updateDocumentStatus(form.getOib(), form.getStatusId());

    mockMvc.perform(post("/api/document/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isNotFound());
  }

  private DocumentStatusForm buildDocumentStatusForm() {
    return new DocumentStatusForm()
        .oib("12345678901")
        .statusId(3L);
  }

}
