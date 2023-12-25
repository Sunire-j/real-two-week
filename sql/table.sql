create table items
(
    items_id  int auto_increment
        primary key,
    name      varchar(255)                       not null,
    price     int                                not null,
    delivery  int                                not null,
    img       varchar(255)                       not null,
    seller    varchar(255)                       not null,
    category  varchar(255)                       null,
    date      datetime default CURRENT_TIMESTAMP not null,
    userprice int                                null,
    detail    longtext                           null
);

create table member
(
    member_id bigint auto_increment
        primary key,
    name      varchar(255) not null,
    email     varchar(255) not null,
    provider  varchar(255) not null,
    nickname  varchar(255) null,
    isadmin   tinyint      not null,
    password  varchar(255) null,
    constraint nickname
        unique (nickname)
)
    charset = utf8mb3;

create table basket
(
    member_id bigint        not null,
    items_id  int           not null,
    amount    int default 1 not null,
    constraint fk_basket_items1
        foreign key (items_id) references items (items_id),
    constraint fk_table1_member
        foreign key (member_id) references member (member_id)
);

create index fk_basket_items1_idx
    on basket (items_id);

create index fk_table1_member_idx
    on basket (member_id);


