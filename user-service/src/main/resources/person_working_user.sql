create database if not exists person_working_user;

use person_working_user;

--
-- 表的结构 `user_base`
--
CREATE TABLE `user_base`
(
    `username` varchar(20) NOT NULL COMMENT '用户名',
    `nickname` varchar(30)      DEFAULT NULL COMMENT '昵称',
    `password` varchar(20)      DEFAULT NULL COMMENT '密码',
    `level`    tinyint UNSIGNED DEFAULT NULL COMMENT '等级'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

--
-- 表的索引 `user_base`
--
ALTER TABLE `user_base`
    ADD PRIMARY KEY (`username`);
COMMIT;

--
-- 表的结构 `user_extra`
--

CREATE TABLE `user_extra`
(
    `username`    varchar(20) NOT NULL COMMENT '用户名',
    `email`       varchar(100) DEFAULT NULL COMMENT '电子邮件',
    `image`       varchar(200) DEFAULT NULL COMMENT '用户头像链接',
    `message`     varchar(100) DEFAULT NULL COMMENT '用户留言',
    `create_time` datetime     DEFAULT NULL COMMENT '用户创建时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

--
-- 表的索引 `user_extra`
--
ALTER TABLE `user_extra`
    ADD PRIMARY KEY (`username`),
    ADD UNIQUE KEY `idx_user_extra_email` (`email`);

--
-- 限制表 `user_extra`
--
ALTER TABLE `user_extra`
    ADD CONSTRAINT `fk_user_base_user_extra` FOREIGN KEY (`username`) REFERENCES `user_base` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;