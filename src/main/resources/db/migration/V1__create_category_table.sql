create table bs_category(
      id bigint auto_increment comment'分类id' ,
      name varchar(50)  not null comment'分类名称',
      parent_id bigint comment'父分类id',
      sort_order int not null default 0 comment'排序值',
      created_at datetime not null default current_timestamp comment'创建时间',
      updated_at datetime not null default current_timestamp on update current_timestamp comment'更新时间',
      primary key(id),
      unique key uk_bs_category_name (name)
)engine=InnoDB
 default charset=utf8mb4
 collate=utf8mb4_0900_ai_ci
comment'书籍分类表';