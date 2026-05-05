package com.example.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户留言实体，对应 message_contents 表。
 *
 * 留言来源可以是微信小程序、网站前台等，管理员在后台统一查看和处理。
 */
@Data
public class MessageContent {
    private Long id;

    /** 留言人姓名。 */
    private String name;

    /** 留言人联系电话。 */
    private String phone;

    /** 留言内容。 */
    private String content;

    /** 来源：wechat_mini_program=微信小程序，website=网站。 */
    private String source;

    /** 状态：0未处理，1已处理。 */
    private Integer status;

    /** 管理员处理备注。 */
    private String adminRemark;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
