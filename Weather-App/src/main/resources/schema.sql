create table country (
  ID int not null AUTO_INCREMENT,
  country_name varchar(100) not null,
  country_code varchar(2) not null,
  PRIMARY KEY ( ID )
)
AS
SELECT *
FROM CSVREAD('classpath:countrycode.csv');

create table api_key (
  api_key varchar(50) not null,
  timestamp long not null,
  number_of_time_used int not null,
  PRIMARY KEY ( api_key )
);

create table weather_report_details (
    ID int not null AUTO_INCREMENT,
    city varchar(50) not null,
    country varchar(50) not null,
    description varchar(100) not null,
    timestamp long not null,
    PRIMARY KEY ( ID )
)
