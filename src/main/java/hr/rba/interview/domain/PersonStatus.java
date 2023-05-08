package hr.rba.interview.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "person_status")
public class PersonStatus {

  @Id
  @SequenceGenerator(name = "person_status_id_seq", sequenceName = "person_status_id_seq", allocationSize = 1)
  @GeneratedValue(generator = "person_status_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

}
