package notes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import notes.models.Note;

public interface NotesRepository extends JpaRepository<Note, Long>{
	
	@Query(value = "select * from notes "
					+ "where cast(id as varchar) like :value "
							+ "or name like :value "
							+ "or description like :value "
							+ "or cast(is_deleted as varchar) like :value "
							+ "or cast(created_at as varchar) like :value "
							+ "or cast(updated_at as varchar) like :value "
					+ "order by id",
			nativeQuery = true)
	List<Note> readNotesWithQuery(String value);
	
	@Query(value = "select * from notes "
			+ "where cast(id as varchar) like :value "
					+ "or name like :value "
					+ "or description like :value "
					+ "or cast(is_deleted as varchar) like :value "
					+ "or cast(created_at as varchar) like :value "
					+ "or cast(updated_at as varchar) like :value "
			+ "order by id offset :offset limit :limit",
	nativeQuery = true)
	List<Note> readNotesWithPagingQuery(String value, Integer offset, Integer limit);
	
	@Query(value = "select count(*) from notes", nativeQuery = true)
	Integer countAll();
	
	@Query(value = "select count(*) from notes "
			+ "where cast(id as varchar) like :value "
					+ "or name like :value "
					+ "or description like :value "
					+ "or cast(is_deleted as varchar) like :value "
					+ "or cast(created_at as varchar) like :value "
					+ "or cast(updated_at as varchar) like :value ",
	nativeQuery = true)
	Integer countAllWithQuery(String value);
	
}
