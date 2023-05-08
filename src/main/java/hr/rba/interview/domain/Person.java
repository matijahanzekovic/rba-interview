package hr.rba.interview.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "person")
public class Person {

  @Id
  @SequenceGenerator(name = "person_id_seq", sequenceName = "person_id_seq", allocationSize = 1)
  @GeneratedValue(generator = "person_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;

  @Column(name = "oib", nullable = false)
  private String oib;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @ManyToOne
  @JoinColumn(name = "status_id")
  private PersonStatus status;

  @OneToOne(mappedBy = "person")
  private Document document;

}
