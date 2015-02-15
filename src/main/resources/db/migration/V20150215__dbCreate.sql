create table Companies (
    id serial primary key,
    _name VARCHAR(255),
    _admin VARCHAR(255),
    _password VARCHAR(255),
    _domainName VARCHAR(255)
);

create table Users (
    id serial primary key,
    _name VARCHAR(255),
    _password VARCHAR(255)
);