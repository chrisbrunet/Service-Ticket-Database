create database SERVICE_TICKETS;
use SERVICE_TICKETS;

CREATE TABLE EventActivity (
	ID int NOT NULL AUTO_INCREMENT, 
	ActivityName varChar(20), 
	PRIMARY KEY (ID)
);

CREATE TABLE EventOrigin (
	ID int NOT NULL AUTO_INCREMENT,
	OriginName varChar(20),
	PRIMARY KEY (ID)
);
		
CREATE TABLE EventStatus (
	ID int NOT NULL AUTO_INCREMENT,
	Status varChar(20),
	PRIMARY KEY (ID)
);

CREATE TABLE EventClass (
	ID int NOT NULL AUTO_INCREMENT,
	Class varChar(20),
	PRIMARY KEY (ID)
);

CREATE TABLE EventLog (
	ID int NOT NULL AUTO_INCREMENT, 
	CaseID varChar(20), 
    Activity varChar(20),
    Urgency varChar(1),
    Impact varChar(1),
    Priority varChar(1),
    StartDate date,
    EndDate date,
    TicketStatus varChar(20),
    UpdateDateTime datetime,
    Duration int, 
    Origin varChar(20),
    Class varChar(20),
	PRIMARY KEY (ID)
);

insert into EventActivity (ActivityName) Values
('Design'), ('Construction'), ('Test'), ('Password Reset');

insert into EventOrigin (OriginName) Values
('Joe S.'), ('Bill B.'), ('George E.'), ('Ahmed M.'), ('Rona E.');

insert into EventStatus (Status) Values
('Open'), ('On Hold'), ('In Process'), ('Deployed'), ('Deployed Failed');

insert into EventClass (Class) Values
('Change'), ('Incident'), ('Problem'), ('Service Request');

select * from EventLog LIMIT 0,10000;