package hr.rba.interview.enums;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DocumentStatusValue {

  CREATED (1L, "Created"),
  IN_PROCESS (2L, "In process"),
  DONE (3L, "Done"),
  INACTIVE (4L, "Inactive");

  private final Long id;
  private final String name;

  public static DocumentStatusValue findById(final Long id) {
    return Arrays.stream(DocumentStatusValue.values())
        .filter(status -> status.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Document status with ID: %d not found.", id)));
  }

  public static DocumentStatusValue findByName(final String name) {
    return Arrays.stream(DocumentStatusValue.values())
        .filter(status -> status.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Document status with name: %s not found.", name)));
  }

}
