create schema if not exists product;

create table product.t_product
(
    product_id         serial primary key,
    c_title            varchar(200) not null check ( length(trim(c_title)) >= 3 ),
    c_catalogue_number varchar(50),
    c_program_number   int          not null check ( c_catalogue_number > 0 )
);

create table product.t_technic
(
    technic_id serial primary key,
    c_title    varchar(50) not null unique check ( length(trim(c_title)) >= 3 )
);

create table product.t_applicability
(
    applicability_id serial primary key,
    product_id       bigint not null,
    technic_id       bigint not null,
    unique (product_id, technic_id),
    constraint fk_applicability_product foreign key (product_id) references t_product (product_id) on delete cascade on update no action,
    constraint fk_applicability_technic foreign key (technic_id) references t_technic (technic_id) on delete cascade on update no action
);

create table product.t_product_balance
(
    balance_id serial primary key,
    product_id bigint not null,
    balance    bigint not null check ( balance > 0 ),
    price      decimal(12, 2) check ( price > 0 ),
    unique (product_id),
    constraint fk_product_balance_product foreign key (product_id) references t_product (product_id) on delete cascade on update no action
);