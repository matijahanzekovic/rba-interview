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
@Table(name = "document")
public class Document {

  @Id
  @SequenceGenerator(name = "document_id_seq", sequenceName = "document_id_seq", allocationSize = 1)
  @GeneratedValue(generator = "document_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "location", nullable = false)
  private String location;

  @OneToOne
  @JoinColumn(name = "person_id")
  private Person person;

  @ManyToOne
  @JoinColumn(name = "status_id")
  private DocumentStatus status;

}
