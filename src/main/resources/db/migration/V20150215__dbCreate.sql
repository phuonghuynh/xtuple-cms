create table Companies (
    id serial primary key,
    _installName VARCHAR(255),
    _adminPassword VARCHAR(255),
    _publicDomain VARCHAR(255),
    _sysReport VARCHAR(5000)
);

create table Users (
    id serial primary key,
    _username VARCHAR(255),
    _password VARCHAR(255)
);

INSERT INTO Users (_username, _password) VALUES ('sadmin', 'jdJHsa7/1D2lapAWh0Wz2/DSXuden+5x');
INSERT INTO Companies (_installName, _adminPassword, _publicDomain, _sysReport) VALUES ('sadmin', 'jdJHsa7/1D2lapAWh0Wz2/DSXuden+5x', 'domain 1', '');