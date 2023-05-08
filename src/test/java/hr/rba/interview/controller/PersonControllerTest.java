package hr.rba.interview.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.rba.interview.domain.Person;
import hr.rba.interview.domain.PersonStatus;
import hr.rba.interview.enums.PersonStatusValue;
import hr.rba.interview.exception.FileException;
import hr.rba.interview.model.CodebookDto;
import hr.rba.interview.model.PersonDto;
import hr.rba.interview.model.PersonForm;
import hr.rba.interview.model.PersonStatusForm;
import hr.rba.interview.service.PersonService;
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
class PersonControllerTest {

  @MockBean
  private PersonService personService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createPersonShould() throws Exception {
    final var form = buildPersonForm();
    final var dto = buildPersonDto();

    when(personService.createPerson(form)).thenReturn(dto);

    mockMvc.perform(post("/api/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(dto)));
  }

  @Test
  void createPersonShouldThrowExceptionIfPersonAlreadyExists() throws Exception {
    final var form = buildPersonForm();

    when(personService.createPerson(form)).thenThrow(IllegalArgumentException.class);

    mockMvc.perform(post("/api/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getPersonShould() throws Exception {
    final var oib = "12345678901";
    final var dto = buildPersonDto();

    when(personService.getPerson(oib)).thenReturn(dto);

    mockMvc.perform(get("/api/person/{oib}", oib)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(dto)));
  }

  @Test
  void getPersonShouldThrowExceptionIfPersonDoesNotExist() throws Exception {
    final var oib = "12345678901";

    when(personService.getPerson(oib)).thenThrow(EntityNotFoundException.class);

    mockMvc.perform(get("/api/person/{oib}", oib)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getPersonShouldThrowExceptionIfErrorHappensDuringGeneratingFile() throws Exception {
    final var oib = "12345678901";

    when(personService.getPerson(oib)).thenThrow(FileException.class);

    mockMvc.perform(get("/api/person/{oib}", oib)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deletePersonShould() throws Exception {
    final var oib = "12345678901";

    doNothing().when(personService).deletePerson(oib);

    mockMvc.perform(delete("/api/person/{oib}", oib)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully deleted person."));
  }

  @Test
  void deletePersonShouldThrowExceptionIfPersonDoesNotExist() throws Exception {
    final var oib = "12345678901";

    doThrow(EntityNotFoundException.class).when(personService).deletePerson(oib);

    mockMvc.perform(delete("/api/person/{oib}", oib)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void updatePersonStatusShould() throws Exception {
    final var form = buildPersonStatusForm();

    doNothing().when(personService).updatePersonStatus(form);

    mockMvc.perform(post("/api/person/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully updated person status."));
  }

  @Test
  void updatePersonStatusShouldThrowExceptionIfPersonDoesNotExist() throws Exception {
    final var form = buildPersonStatusForm();

    doThrow(EntityNotFoundException.class).when(personService).updatePersonStatus(form);

    mockMvc.perform(post("/api/person/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(form)))
        .andExpect(status().isNotFound());
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

  private PersonStatusForm buildPersonStatusForm() {
    return new PersonStatusForm()
        .oib("12345678901")
        .statusId(3L);
  }

}
