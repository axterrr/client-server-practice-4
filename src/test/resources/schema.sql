create table if not exists goods (
    id        serial  primary key,
    name      varchar(255)      not null,
    category  varchar(255)      not null,
    amount    integer           not null,
    price     double precision  not null
);
