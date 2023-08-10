package notes.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
import notes.models.Note;
import notes.props.PropsForNote;
import notes.rest.client.RestClientNotes;
import notes.tools.PDFGenerator;

//@Slf4j
@Controller
@RequestMapping("/notes-list")
@Data
public class NotesController {
	
	private RestClientNotes restClientNotes;
	
	private PropsForNote props;
	
	public NotesController(RestClientNotes restClientNotes) {
		this.restClientNotes = restClientNotes;
		this.props = new PropsForNote();
	}

	/*
	 * Ниже представлены методы управления постраничным просмотром данных
	 * 
	 * */
	
	@GetMapping("/paging")
	public String swithPaging() {
		if (this.props.getIsPaging())
			this.props.setIsPaging(false);
		else 
			this.props.setIsPaging(true);
		return "redirect:/notes-list";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.props.setCurPage(0);
		return "redirect:/notes-list";
	}
	
	@GetMapping("/prev")
	public String prevPage() {
		if (this.props.getCurPage() > 0) {
			this.props.setCurPage(this.props.getCurPage() - 1);
		}
		return "redirect:/notes-list";
	}
	
	@GetMapping("/next")
	public String nextPage() {
		Integer countNotes = this.restClientNotes.countAll(
										this.props.getIsFiltering(), 
										this.props.getFilteringValue());
		
		if (this.props.getCurPage() < this.props.getTotalPages(countNotes))
			this.props.setCurPage(this.props.getCurPage() + 1);
		
		return "redirect:/notes-list";
	}

	@GetMapping("/last")
	public String lastPage() {
		Integer countNotes = this.restClientNotes.countAll(
										this.props.getIsFiltering(), 
										this.props.getFilteringValue());
		
		this.props.setCurPage(this.props.getTotalPages(countNotes));
		
		return "redirect:/notes-list";
	}
	
	@PostMapping("/change-page-size")
	public String changePageSize(@ModelAttribute("props") PropsForNote props) {
		if (props.getPageSize() <= 0)
			props.setPageSize(1);
		this.props.setPageSize(props.getPageSize());
		return "redirect:/notes-list";
	}
	
	/*
	 * Ниже представлены методы управления фильтром просмотра данных
	 * 
	 * */
	
	@GetMapping("/filter")
	public String switchFilter() {
		this.props.setIsFiltering(!this.props.getIsFiltering());
		
		Integer countNotes = this.restClientNotes.countAll(
										this.props.getIsFiltering(), 
										this.props.getFilteringValue());
		Integer totalPages = this.props.getTotalPages(countNotes);
		if (this.props.getCurPage() > totalPages) 
			this.props.setCurPage(totalPages);
		
		return "redirect:/notes-list";
	}
	
	@GetMapping(value = "/query", params = "value")
	public String runFilteringQuery(@RequestParam("value") String value, Model model) {
		this.props.setFilteringValueUI(value);
		this.props.setFilteringValue("%" + value + "%");
		this.props.setCurPage(0);
		return "redirect:/notes-list";
	}
	
	/*
	 * Ниже представлен метод экспорта данных о заметках во внешний PDF файл
	 * 
	 * */
	
	@GetMapping("/export-to-pdf")
	public void exportToPDF(HttpServletResponse response) throws 
							IOException, DocumentException {
		response.setContentType("application/pdf");
		
		String headerName = "Content-Disposition";
		String headerValue = "attachment; filename=pdf_" + 
								LocalDateTime.now().toString() + ".pdf";
		response.setHeader(headerName, headerValue);
		
		PDFGenerator generator = new PDFGenerator();
		generator.generate(response, formNotesList());
	}
	
	/*
	 * Ниже представлены методы работы с данными (CRUD) посредством REST клиента
	 * 
	 * */
	
	@GetMapping
	public String getAllNotes(Model model) {
		model.addAttribute("props", this.props);
		model.addAttribute("notes", /*notes*/ formNotesList());
		return "notes-list";
	}
	
	List<Note> formNotesList() {
		List<Note> notes = new ArrayList<>();
		
		if (!this.props.getIsPaging() && !this.props.getIsFiltering()) {
			notes = this.restClientNotes.getAllNotes();
		}
		else if (this.props.getIsPaging() && !this.props.getIsFiltering()) {
			notes = this.restClientNotes.getAllNotes(
						this.props.getCurPage(), this.props.getPageSize());
		}
		else if (!this.props.getIsPaging() && this.props.getIsFiltering()) {
			notes = this.restClientNotes.getAllNotes(
						this.props.getFilteringValue());
		}
		else if (this.props.getIsPaging() && this.props.getIsFiltering()) {
			notes = this.restClientNotes.getAllNotes(
						this.props.getCurPage(), this.props.getPageSize(), 
						this.props.getFilteringValue());
		}
		return notes;
	}
	
	@GetMapping("/{id}")
	public String getNoteById(@PathVariable Long id, Model model) {
		Note note = this.restClientNotes.getNoteById(id);
		model.addAttribute("note", note);
		return "note-card";
	}
	
	@GetMapping("/new")
	public String openCreateNoteForm(Note note) {
		return "note-create";
	}
	
	@PostMapping
	public String postNote(@Valid Note note, BindingResult errors) {
		if (errors.hasErrors()) 
			return "note-create";
		
		this.restClientNotes.postNote(note);
		
		return "redirect:/notes-list";
	}
	
	@GetMapping("/{id}/edit")
	public String openPatchForm(@PathVariable Long id, Model model) {
		Note note = this.restClientNotes.getNoteById(id);
		
		model.addAttribute("oldName", note.getName());
		model.addAttribute("oldDescription", note.getDescription());
		model.addAttribute("note", new Note(note.getId(), null, null, 
							note.getIsDeleted(), note.getCreatedAt(), LocalDateTime.now()));
		
		return "note-edit";
	}
	
	// Эта первая версия метода patchNote сохраняет изменения в заметке (в объекте Note): 
	// - Для этого сначала создается промежуточный объект Note, полям которого 
	//   присваиваются изменения полей исходного объекта Note, пришедшие из формы 
	//   "note-edit". Также для сохранения даты и времени изменения в промежуточном 
	//   объекте запоминается значение для поля updatedAt. 
	// - Далее этот промежуточный объект с изменениями передается в Rest клиент с помощью 
	//   метода patchNote() Rest клиента. 
	/*
	@PatchMapping
	public String patchNote(@Valid Note note, BindingResult errors) {
		if (errors.hasErrors()) 
			return "note-edit";
		
		Note patch = new Note(null, null, null, null, null, null);
		
		if (note.getName() != null && note.getName().trim().length() > 0)
			patch.setName(note.getName().trim());
		if (note.getDescription() != null && note.getDescription().trim().length() > 0)
			patch.setDescription(note.getDescription().trim());
		patch.setUpdatedAt(note.getUpdatedAt());
		
		this.restClientNotes.patchNote(patch, note.getId());
		
		return "redirect:/notes-list";
	}*/
	
	// Эта вторая версия метода patchNote сохраняет изменения в заметке (в объекте Note): 
	// - Для этого сначала создается промежуточный ассоциативный массив Map, в который 
	//   добавляются пары значений с изменениями полей исходного объекта Note, пришедшие 
	//   из формы "note-edit". Также для сохранения даты и времени изменения в промежуточном 
	//   ассоциативном массиве Map запоминается значение для поля updatedAt в формате 
	//   текста (для корректной передачи значения LocalDateTime). 
	// - Далее этот промежуточный Map с изменениями передается в Rest клиент с помощью 
	//   метода patchNote() Rest клиента. 
	@PatchMapping
	public String patchNote(@Valid Note note, BindingResult errors) {
		if (errors.hasErrors()) 
			return "note-edit";
		
		Map<String, Object> patch = new HashMap<>();
		
		if (note.getName() != null && note.getName().trim().length() > 0)
			patch.put("name", note.getName().trim());
		if (note.getDescription() != null && note.getDescription().trim().length() > 0)
			patch.put("description", note.getDescription().trim());
		patch.put("updatedAt", note.getUpdatedAt().toString());
		
		this.restClientNotes.patchNote(patch, note.getId());
		
		return "redirect:/notes-list";
	}
	
	@GetMapping("/{id}/status")
	public String openStatusChangeForm(@PathVariable Long id, Model model) {
		Note note = this.restClientNotes.getNoteById(id);
		note.setUpdatedAt(LocalDateTime.now());
		model.addAttribute("note", note);
		return "note-status";
	}
	
	// Эта первая версия метода patchNoteStatus сохраняет изменения в статусе заметки 
	// (в статусе объекта Note): 
	// - Для этого сначала создается промежуточный объект Note, полю isDeleted 
	//   которого присваивается изменение, пришедшее из формы "note-status". Также 
	//   для сохранения даты и времени изменения в промежуточном объекте запоминается 
	//   значение для поля updatedAt. 
	// - Далее этот промежуточный объект с изменениями передается в Rest клиент с помощью 
	//   метода patchNote() Rest клиента. 
	/*
	@PatchMapping("/status")
	public String patchNoteStatus(Note note) {
		Note patch = new Note(null, null, null, null, null, null);
		
		patch.setIsDeleted(note.getIsDeleted());
		patch.setUpdatedAt(note.getUpdatedAt());
		
		this.restClientNotes.patchNote(patch, note.getId());
		
		return "redirect:/notes-list";
	}*/
	
	
	// Эта вторая версия метода patchNoteStatus сохраняет изменения в статусе заметки 
	// (в статусе объекта Note): 
	// - Для этого сначала создается промежуточный ассоциативный массив Map, в который 
	//   добавляется пара значений с изменением поля isDeleted, пришедшим из формы 
	//   "note-status". Также для сохранения даты и времени изменения в промежуточном 
	//   ассоциативном массиве Map запоминается значение для поля updatedAt в формате 
	//   текста (для корректной передачи значения LocalDateTime). 
	// - Далее этот промежуточный Map с изменениями передается в Rest клиент с помощью 
	//   метода patchNote() Rest клиента. 
	@PatchMapping("/status")
	public String patchNoteStatus(Note note) {
		Map<String, Object> patch = new HashMap<>();
		
		patch.put("isDeleted", note.getIsDeleted());
		patch.put("updatedAt", note.getUpdatedAt().toString());
		
		this.restClientNotes.patchNote(patch, note.getId());
		
		return "redirect:/notes-list";
	}
	
	@GetMapping("/{id}/delete")
	public String deleteNote(@PathVariable Long id) {
		Note note = this.restClientNotes.getNoteById(id);
		if (!note.getIsDeleted()) {
			return "warning";
		}
		this.restClientNotes.deleteNote(id);
		return "redirect:/notes-list";
	}
	
}
