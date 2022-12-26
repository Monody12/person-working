create database if not exists person_working_netdisk;

use person_working_netdisk;

--
-- 表的结构 `file`
--

CREATE TABLE `file` (
                        `id` bigint UNSIGNED NOT NULL COMMENT '文件标识(通过这个id可以在这个文件表中查找到一个用户的一个文件)',
                        `username` varchar(20) DEFAULT NULL COMMENT '文件所有者用户名',
                        `name` varchar(127) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件名',
                        `detail` varchar(100) DEFAULT NULL COMMENT '文件描述',
                        `size` bigint UNSIGNED DEFAULT NULL COMMENT '文件大小',
                        `type` varchar(20) DEFAULT 'other',
                        `path` varchar(200) DEFAULT NULL COMMENT '文件路径',
                        `update_time` datetime DEFAULT NULL COMMENT '修改日期',
                        `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                        `logic` tinyint UNSIGNED DEFAULT NULL COMMENT '是否逻辑有效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


--
-- 表的结构 `folder`
--

CREATE TABLE `folder` (
                          `id` bigint UNSIGNED NOT NULL COMMENT '文件夹id',
                          `username` varchar(20) DEFAULT NULL COMMENT '该文件夹的用户名',
                          `name` varchar(50) DEFAULT NULL COMMENT '文件夹名称',
                          `detail` varchar(100) DEFAULT NULL COMMENT '文件夹描述',
                          `type` varchar(20) DEFAULT 'folder',
                          `path` varchar(1000) DEFAULT NULL COMMENT '文件夹位置',
                          `update_time` datetime DEFAULT NULL COMMENT '修改日期',
                          `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                          `logic` tinyint UNSIGNED DEFAULT NULL COMMENT '是否逻辑有效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- 转储表的索引
--

--
-- 表的索引 `file`
--
ALTER TABLE `file`
    ADD PRIMARY KEY (`id`);

--
-- 表的索引 `folder`
--
ALTER TABLE `folder`
    ADD PRIMARY KEY (`id`);
COMMIT;