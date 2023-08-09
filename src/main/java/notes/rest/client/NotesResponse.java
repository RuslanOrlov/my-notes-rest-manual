package notes.rest.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import notes.models.Note;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotesResponse {
	
	@JsonProperty("_embedded")
	private EmbeddedNotes embeddedNotes;
	
	public List<Note> getNotes() {
		return this.embeddedNotes.getNotes();
	}
	
	// Внутренний класс для десереализации объекта "_embedded.notes"
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EmbeddedNotes {
		private List<Note> notes;

		public List<Note> getNotes() {
			return this.notes;
		}

		public void setNotes(List<Note> notes) {
			this.notes = notes;
		}		
	}
	
}
