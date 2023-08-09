package notes.rest.server;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//import lombok.extern.slf4j.Slf4j;
import notes.models.Note;
import notes.repositories.NotesRepository;

//@Slf4j
@RestController
@RequestMapping(path = "/api", produces = "application/json")
public class NotesRestController {
	
	private NotesRepository notesRepository;

	public NotesRestController(NotesRepository notesRepository) {
		this.notesRepository = notesRepository;
	}
	
	@GetMapping(params = "sort")
	public List<Note> getAllNotes(@RequestParam String sort) {
		return this.notesRepository.findAll(Sort.by(sort));
	}
	
	@GetMapping(params = {"sort", "page", "size"})
	public List<Note> getAllNotes(@RequestParam("sort") String sort, 
									@RequestParam("page") int page, 
									@RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
		return this.notesRepository.findAll(pageable).getContent();
	}
	
	@GetMapping(params = "value")
	public List<Note> getAllNotesWithQuery(@RequestParam String value) {
		return this.notesRepository.readNotesWithQuery(value);
	}
	
	@GetMapping(params = {"value", "offset", "limit"})
	public List<Note> getAllNotesWithQuery(@RequestParam String value, 
									@RequestParam int offset, 
									@RequestParam int limit) {
		return this.notesRepository.readNotesWithPagingQuery(value, offset, limit);
	}
	
	@GetMapping("/count")
	public Integer countAll() {
		return this.notesRepository.countAll();
	}
	
	@GetMapping(path = "/count", params = "value")
	public Integer countAll(@RequestParam String value) {
		return this.notesRepository.countAllWithQuery(value);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
		Note note = this.notesRepository.findById(id).orElse(null);
		if (note != null) {
			return ResponseEntity.ok(note);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<Note> postNote(@RequestBody Note note) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.notesRepository.save(note));
	}
	
	@PatchMapping(path = "/{id}", consumes = "application/json")
	public ResponseEntity<Note> patchNote(@PathVariable Long id, 
										@RequestBody Note patch) {
		Note note = this.notesRepository.findById(id).orElse(null);
		
		if (note == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		
		if (patch.getIsDeleted() != null) {
			note.setIsDeleted(patch.getIsDeleted());
		}
		if (patch.getName() != null && patch.getName().trim().length() > 0) {
			note.setName(patch.getName().trim());
		}
		if (patch.getDescription() != null && patch.getDescription().trim().length() > 0) {
			note.setDescription(patch.getDescription().trim());
		}
		note.setUpdatedAt(patch.getUpdatedAt());
		
		return ResponseEntity.ok(this.notesRepository.save(note));
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteNote(@PathVariable Long id) {
		this.notesRepository.deleteById(id);
	}
	
}
