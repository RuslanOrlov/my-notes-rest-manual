# my-notes-rest-manual
EN: A Java and Spring Boot project for taking notes using a hand-crafted the REST API (server and client are implemented in the same application). 

The project implements a REST API that is hand-crafted as a Rest controller and provides endpoints for the Note entity. Wherein:
1. REST endpoints provide a complete list of data operations (CRUD);
2. Implemented a logical deletion operation, available via the "Status" link in the list of notes, which changes the value of the isDeleted field of the note object;
3. physical deletion, as one of the CRUD operations, is available only for note objects with a true value of the isDeleted field;
4. in the list of notes, the functions of page-by-page viewing of records and data filtering are implemented, which work in concert;
5. The function of uploading the list of notes in the form of a report to an external PDF file has also been implemented (this function uploads to the report data only about those notes that are available in accordance with the filtering and paging criteria at the time the report was uploaded).
P.S.: This version of the application simultaneously implements the functions of a server and a client.

/----------------------------------------------------------------------------------------------/

RU: Проект на языке Java и Spring Boot по учету заметок с использованием вручную разработанного REST API (сервер и клиент реализованы в одном приложении).

Проект реализует REST API, который разработан вручную в виде Rest контроллера и предоставляет конечные точки для сущности Note (Заметка). При этом:
1. конечные точки REST предоставляют полный перечень операций с данными (CRUD);
2. реализована операция логического удаления, доступная по ссылке "Статус" в списке заметок, которая изменяет значение поля isDeleted объекта заметки;
3. физическое удаление, как одна из операций CRUD, доступна только для объектов заметок с истинным значением поля isDeleted;
4. в списке заметок реализованы функции пострачниного просмотра записей и фильтрации данных, которые работают согласованно;
5. также реализована функция выгрузки списка заметок в виде отчета во веншний файл формата PDF (данная функция выгружает в отчет данные только о тех заметках, которые доступны в соответствии с критериями фильтрации и постраничного просмотра на момент выгрузки отчета).
P.S.: Данная версия приложения реализует одновременно функции сервера и клиента.
