package notes.tools;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
import notes.models.Note;

//@Slf4j
public class PDFGenerator {
	
	public void generate(HttpServletResponse response, List<Note> notes) 
							throws IOException, DocumentException {
		
		// Создать объект документа форматом А4 
		Document document = new Document(PageSize.A4);
		
		// Получить объект PdfWriter, который должен записывать 
		// в указанный документ и через указанный выходной поток 
		// объекта HttpServletResponse 
		PdfWriter.getInstance(document, response.getOutputStream());
		
		// Открыть документ 
		document.open();
		
		// Создать шрифт с поддержкой русского языка 
		/*
		 * Скачать файлы шрифтов с поддержкой русского языка можно с ресурса: 
		 * - https://fonts-online.ru/languages/russian
		 * */
		BaseFont bFont = BaseFont.createFont(
					"src/main/resources/static/fonts/timesnrcyrmt.ttf", 
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font myFont = new Font(bFont, 14);
		
		// Создать параграф с целью использования в качестве заголовка отчета 
		Paragraph paragraph = new Paragraph("СПИСОК ЗАМЕТОК", myFont);
		
		// Выровнять параграф по центру 
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		
		// Добавить созданный параграф (в роли заголовка) в документ 
		document.add(paragraph);
		
		// Создать таблицу для заполнения отчета из 6 столбцов 
		// (что равно количеству полей сущности Note (Заметка))
		PdfPTable table = new PdfPTable(6);
		
		// Установить свойства таблицы: 
		// - ширинна таблицы в процентах от пространства страницы 
		// - ширина столбцов относительно друг друга (целочисленный массив) 
		// - отступ перед таблицей сверху (отделяет от элементов сверху) 
		// - отступ после таблицей снизу (отделяет от элементов снизу) 
		table.setWidthPercentage(100);
		table.setWidths(new int[] {2, 6, 9, 4, 6, 6});
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);
		
		// Создать ячейку для заполнения шапки (заголовка) таблицы
		PdfPCell cell = new PdfPCell(); 
		
		// Установить свойства ячейки: 
		// - отступ текста ячейки от границ ячейки 
		// - горизонтальное выравнивание текста в ячейке 
		// - цвет фона ячейки 
		cell.setPadding(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.MAGENTA);
		
		//Сформировать шапку (заголовок) таблицы
		cell.setPhrase(new Phrase("id", myFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Название", myFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Описание", myFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Статус", myFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Дата/время создания", myFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Дата/время обновления", myFont));
		table.addCell(cell);
		
		// Создать  форматтер для форматирования даты и времени в нужный формат 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy hh:ss");
		
		// Пройти по коллекции заметок и построчно добавить их в таблицу
		for (Note note : notes) {
			table.addCell(new Phrase(note.getId().toString(), myFont));
			table.addCell(new Phrase(note.getName(), myFont));
			table.addCell(new Phrase(note.getDescription(), myFont));
			table.addCell(new Phrase(note.getIsDeleted()==true? 
							"удалено" : "активно", myFont));
			table.addCell(new Phrase(note.getCreatedAt()==null? 
							"---" : note.getCreatedAt().format(formatter), myFont));
			table.addCell(new Phrase(note.getUpdatedAt()==null? 
							"---" : note.getUpdatedAt().format(formatter), myFont));
		}
		// Добавить созданную талицу в документ
		document.add(table);
		
		// Создать параграф как подпись документа 
		paragraph = new Paragraph("Дата и время выгрузки: " + 
									LocalDateTime.now().format(formatter), myFont);
		// Добавить параграф (в роли подписи) в документ 
		document.add(paragraph);
		
		// Закрыть документ 
		document.close();
	}
	
}
