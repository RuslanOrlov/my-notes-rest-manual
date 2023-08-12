create table if not exists notes (
	id 					serial primary key,
	name 				varchar(255),
	description 		varchar(255),
	is_deleted 			boolean,
	created_at 			timestamp,
	updated_at 			timestamp
);