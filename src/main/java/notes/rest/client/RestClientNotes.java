package notes.rest.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//import lombok.extern.slf4j.Slf4j;
import notes.models.Note;

//@Slf4j
@Component
public class RestClientNotes {
	
	private RestTemplate restTemplate;
	private String urlWithoutId;
	private String urlWithoutIdSort1;
	private String urlWithoutIdSort2;
	private String urlWithId;
	private String urlCount;
	private String urlCountWithQuery;
	private String urlQuery;
	private String urlPagingQuery;
	
	public RestClientNotes() {
		
		/*
		 * Из официальной документации на RestTemplate: 
		 * Note that the standard JDK HTTP library does not support 
		 * the HTTP PATCH method.Configure the Apache HttpComponents 
		 * or OkHttp request factory to enable PATCH.
		 * 
		 * Так как стандартный RestTemplate не поддерживает метод 
		 * PATCH напрямую, то для обхода этой проблемы требуется 
		 * добавить поддержку метода PATCH через библиотеку Apache 
		 * HttpComponents. Для этого в pom.xml была добавлена зави-
		 * симость "httpclient5" библиотеки "org.apache.httpcomponents.client5", 
		 * а здесь в RestTemplate выполнена настройка его RequestFactory с 
		 * использованием HttpComponentsClientHttpRequestFactory, которому 
		 * передан подходящий HttpClient. 
		 * 
		 * */
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpComponentsClientHttpRequestFactory requestFactory = 
				new HttpComponentsClientHttpRequestFactory(httpClient);
		this.restTemplate 		= new RestTemplate(requestFactory);
		
		this.urlWithoutId 		= "http://localhost:8080/api";
		this.urlWithoutIdSort1 	= "http://localhost:8080/api?sort={field}";
		this.urlWithoutIdSort2 	= "http://localhost:8080/api?sort={field}"
														+ "&page={page}&size={size}";
		this.urlWithId 			= "http://localhost:8080/api/{id}";
		this.urlCount 			= "http://localhost:8080/api/count";
		this.urlCountWithQuery 	= "http://localhost:8080/api/count?value={value}";
		this.urlQuery 			= "http://localhost:8080/api?value={value}";
		this.urlPagingQuery 	= "http://localhost:8080/api?value={value}"
														+ "&offset={offset}&limit={limit}";
	}
	
	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<Note[]> responseEntity = 
				this.restTemplate.getForEntity(this.urlWithoutIdSort1, Note[].class, "id");
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			notes = Arrays.asList(responseEntity.getBody());
		}
		
		return notes;
	}
		
	public List<Note> getAllNotes(Integer curPage, Integer pageSize) {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<Note[]> responseEntity = 
				this.restTemplate.getForEntity(this.urlWithoutIdSort2, Note[].class, 
												"id", curPage, pageSize);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			notes = Arrays.asList(responseEntity.getBody());
		}
		
		return notes;
	}
	
	public List<Note> getAllNotes(String value) {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<Note[]> responseEntity = 
				this.restTemplate.getForEntity(this.urlQuery, Note[].class, value);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			notes = Arrays.asList(responseEntity.getBody());
		}
		
		return notes;
	}
	
	public List<Note> getAllNotes(Integer curPage, Integer pageSize, String value) {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<Note[]> responseEntity = 
				this.restTemplate.getForEntity(this.urlPagingQuery, Note[].class, value, 
												curPage*pageSize, pageSize);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			notes = Arrays.asList(responseEntity.getBody());
		}
		
		return notes;
	}
		
	public Integer countAll(Boolean isFiltering, String value) {
		ResponseEntity<Integer> responseEntity = 
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		
		if (isFiltering)
			responseEntity = this.restTemplate.getForEntity(
									this.urlCountWithQuery, Integer.class, value);
		else
			responseEntity = this.restTemplate.getForEntity(
									this.urlCount, Integer.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful())
			return responseEntity.getBody();
		return 0;
	}
	
	public Note getNoteById(Long id) {
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.getForEntity(this.urlWithId, Note.class, id);
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public Note postNote(Note note) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Note> requestEntity = new HttpEntity<Note>(note, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.postForEntity(this.urlWithoutId, requestEntity, Note.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	// Это первая версия метода patchNote, которая получает от контроллера MVC 
	// промежуточный объект Note с изменениями редактирования исходного объекта 
	// - И ДАЛЕЕ направляет PATCH запрос с этим промежуточным объектом в REST 
	//   контроллер по указанному URL для сохранения изменений в исходном объекте 
	//   Note в БД
	// - И ДАЛЕЕ получает от REST контроллера ответ с обновленным в БД исходным 
	//   объектом Note. 
	/*
	public Note patchNote(Note patch, Long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Note> requestEntity = 
				new HttpEntity<Note>(patch, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.exchange(this.urlWithId, HttpMethod.PATCH, 
						requestEntity, Note.class, id);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}*/

	// Это вторая версия метода patchNote, которая получает от контроллера MVC 
	// промежуточный ассоциативный массив Map с изменениями редактирования исходного 
	// объекта 
	// - И ДАЛЕЕ направляет PATCH запрос с этим промежуточным ассоциативным массивом 
	//   Map в REST контроллер по указанному URL для сохранения изменений в исходном 
	//   объекте Note в БД
	// - И ДАЛЕЕ получает от REST контроллера ответ с обновленным в БД исходным 
	//   объектом Note. 
	public Note patchNote(Map<String, Object> patch, Long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> requestEntity = 
				new HttpEntity<Map<String, Object>>(patch, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.exchange(this.urlWithId, HttpMethod.PATCH, 
						requestEntity, Note.class, id);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public void deleteNote(Long id) {
		this.restTemplate.delete(this.urlWithId, id);
	}	
	
}
