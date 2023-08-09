package notes.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedNativeQueries({
	@NamedNativeQuery(
			name = "Note_FindByAllField",
			query = "select * from notes "
					+ "where cast(id as varchar) like :value "
							+ "or name like :value "
							+ "or description like :value "
							+ "or cast(isDeleted as varchar) like :value "
							+ "or cast(createdAt as varchar) like :value "
							+ "or cast(updatedAt as varchar) like :value "
					+ "order by id", 
			resultClass = Note.class)
})
public class Note {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Size(max = 255, message = "Длина наименования не может превышать 255 символов!")
	private String name;
	
	@Size(max = 255, message = "Длина описания не может превышать 255 символов!")
	private String description;
	
	private Boolean isDeleted = false;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	
	private LocalDateTime updatedAt;
	
}
