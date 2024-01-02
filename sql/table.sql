create table items
(
    items_id int auto_increment
        primary key,
    name     varchar(255)                       not null,
    price    int                                not null,
    delivery int                                not null,
    img      varchar(255)                       not null,
    seller   varchar(255)                       not null,
    category varchar(255)                       null,
    date     datetime default CURRENT_TIMESTAMP not null,
    detail   longtext                           null
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
    phone     varchar(255) null,
    zipno     int          null,
    address1  varchar(255) null,
    address2  varchar(255) null,
    address3  varchar(255) null,
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
        foreign key (items_id) references items (items_id)
            on update cascade on delete cascade,
    constraint fk_table1_member
        foreign key (member_id) references member (member_id)
            on update cascade on delete cascade
);

create index fk_basket_items1_idx
    on basket (items_id);

create index fk_table1_member_idx
    on basket (member_id);

create table method
(
    idmethod int          not null
        primary key,
    name     varchar(255) null
);

create table methoddetail
(
    idmethod int          not null,
    type     varchar(255) not null,
    detailid int auto_increment
        primary key,
    name     varchar(255) null,
    constraint fk_methoddetail_method1
        foreign key (idmethod) references method (idmethod)
);

create index fk_methoddetail_method1_idx
    on methoddetail (idmethod);

create table `order`
(
    idorder         int auto_increment
        primary key,
    member_id       bigint                             null,
    date            datetime default CURRENT_TIMESTAMP not null,
    status          int      default 0                 not null,
    ordername       varchar(255)                       not null,
    orderphonenum   varchar(255)                       not null,
    orderemail      varchar(255)                       not null,
    receivename     varchar(255)                       not null,
    receivephonenum varchar(255)                       not null,
    receiveaddress1 varchar(255)                       null,
    receiveaddress2 varchar(255)                       null,
    receiveaddress3 varchar(255)                       null,
    request         varchar(255)                       null,
    zipno           int                                not null,
    method          int                                not null,
    methodDetails   int                                null,
    orderNum        varchar(255)                       not null,
    totalprice      int                                null,
    delivery        int                                null,
    constraint fk_order_member1
        foreign key (member_id) references member (member_id)
);

create index fk_order_member1_idx
    on `order` (member_id);

create table orderdetail
(
    idorderdetail  int auto_increment
        primary key,
    order_idorder  int not null,
    items_items_id int not null,
    amount         int not null,
    constraint fk_orderdetail_order1
        foreign key (order_idorder) references `order` (idorder),
    constraint orderdetail_items_items_id_fk
        foreign key (items_items_id) references items (items_id)
            on update cascade on delete cascade
);

create index fk_orderdetail_items1_idx
    on orderdetail (items_items_id);

create index fk_orderdetail_order1_idx
    on orderdetail (order_idorder);


