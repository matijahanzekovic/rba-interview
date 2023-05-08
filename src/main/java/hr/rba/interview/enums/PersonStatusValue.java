package hr.rba.interview.enums;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PersonStatusValue {

  INITIAL(1L, "Initial"),
  IN_PROCESS(2L, "In process"),
  DONE(3L, "Done");

  private final Long id;
  private final String name;

  public static PersonStatusValue findById(final Long id) {
    return Arrays.stream(PersonStatusValue.values())
        .filter(status -> status.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Person status with ID: %d not found.", id)));
  }

  public static PersonStatusValue findByName(final String name) {
    return Arrays.stream(PersonStatusValue.values())
        .filter(status -> status.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Person status with name: %s not found.", name)));
  }

}
