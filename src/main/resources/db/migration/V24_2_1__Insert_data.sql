insert into product.t_product (c_title, c_catalogue_number, c_program_number)
values ('Накладка тормозная (МАЗ Супер) с заклепками (к-т)  В-160', '5336-3501105р', 6526),
       ('Муфта выключения сцепления в сборе (МАЗ)', '236-1601180-Б2', 6010);

insert into product.t_technic (c_title)
values ('МАЗ'),
       ('КамАЗ'),
       ('МТЗ');

insert into product.t_applicability (product_id, technic_id)
values (1, 1),
       (1, 2),
       (2, 1);

insert into product.t_product_balance (product_id, balance, price)
values (1, 5, 2450.00),
       (2, 7, 1400.00);