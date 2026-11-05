
create table bs_user(
    id bigint auto_increment comment '用户id',
    username varchar(20) not null comment '用户名',
    password_hash varchar(100) not null comment '密码哈希',
    nickname varchar(20) not null comment '昵称',
    phone varchar(20) comment '手机号',
    email varchar(100) not null comment '邮箱',
    status enum('ENABLED','DISABLED') not null default 'ENABLED' comment '账号状态',
    role enum('USER','ADMIN') not null default 'USER' comment '角色权限',
    created_at datetime not null default current_timestamp comment'创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment'更新时间',
    primary key(id),
    unique key uk_bs_user_username(username),
    unique key uk_bs_user_email(email)
)engine=InnoDB
 default charset=utf8mb4
 collate=utf8mb4_0900_ai_ci
comment'用户表';