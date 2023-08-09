package notes.props;

import lombok.Data;

@Data
public class PropsForNote {
	// Свойства управления постраничным выводом
	private Boolean isPaging = false;
	private Integer curPage = 0;
	private Integer pageSize = 10;
	
	// Свойства управления фильтрацией
	private Boolean isFiltering = false;
	private String filteringValue = "%%";
	private String filteringValueUI = "";
	
	public int getTotalPages(Integer count) {
		Integer totalPages = 0;
		
		if (count == 0) 
			totalPages = count; 
		else if (count % this.pageSize == 0)
			totalPages = count / this.pageSize - 1;
		else 
			totalPages = count / this.pageSize; 
		
		if (this.curPage > totalPages)
			this.curPage = totalPages;
		
		return totalPages;
	}
}
