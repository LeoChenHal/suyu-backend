-- auto-generated definition
create table user
(
    id            int auto_increment comment '主键id'
        primary key,
    username      varchar(255)                       null comment '用户名',
    user_account  varchar(255)                       null comment '登录账号',
    user_password varchar(255)                       null comment '用户登录密码',
    gender        varchar(255)                       null comment '性别',
    phone         varchar(15)                        null comment '手机号',
    email         varchar(255)                       null comment '邮箱',
    avatar_url    varchar(255)                       null comment '头像',
    user_status   int      default 1                 null comment '0-无效，1-有效',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    is_delete     int      default 0                 not null comment '0代表没被删除，1代表删除',
    is_admin      int      default 0                 not null comment '0代表不是，1代表是管理员',
    tags          varchar(255)                       null comment '标签'
)
    collate = utf8mb4_general_ci;

-- auto-generated definition
create table team
(
    id          int auto_increment comment '主键id'
        primary key,
    team_name   varchar(255)                       null comment '队伍名称',
    team_desc   varchar(255)                       null comment '队伍描述',
    max_num     int                                null comment '最大人数',
    expire_time datetime                           null comment '过期时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    status      int                                null comment '队伍状态 0-公开，1私有，2加密',
    user_id     int                                null comment '队长',
    team_pwd    varchar(255)                       null comment '队伍密码'
)
    collate = utf8mb4_general_ci;



-- auto-generated definition
create table user_team
(
    user_id     int                                null comment '用户id',
    id          int auto_increment
        primary key,
    team_id     int                                null comment '队伍id',
    join_time   datetime                           null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_delete   int      default 0                 not null comment '是否删除'
)
    comment '队伍用户关系表' collate = utf8mb4_general_ci;


