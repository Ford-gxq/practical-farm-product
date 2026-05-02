package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 后台用户实体，对应 sys_user 表。 */
@Data
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;

    /** 用户角色：admin=管理员，user=普通管理用户。 */
    private String role;

    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
