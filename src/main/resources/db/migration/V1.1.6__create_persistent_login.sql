create table persistent_login (
	username varchar(255) not null,
	series varchar(255) primary key,
	token varchar(255) not null,
	last_used timestamp not null
)