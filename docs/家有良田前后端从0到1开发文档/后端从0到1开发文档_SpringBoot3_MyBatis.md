# 家有良田农产品网站后端从 0 到 1 开发文档

> 对应项目目录：`backend/`  
> 技术栈：JDK 21、Spring Boot 3.5.7、Spring MVC、Spring Validation、MyBatis 3、MySQL 8、Maven、Lombok  
> 目标：你可以照着本文档，从空目录一步步开发出和当前项目同结构、同接口、同业务逻辑的后端。

---

## 1. 后端项目定位

这个后端是一个前后端分离的农产品展示网站 API 服务。

业务对象虽然沿用了博客系统中的命名：

```text
Article = 商品 / 乡村故事
Category = 商品分类
```

但是当前业务含义已经变成：

```text
blog_article  = 农产品商品与乡村故事内容表
blog_category = 商品分类表
sys_user      = 后台用户表
```

---

## 2. 后端功能清单

### 2.1 前台接口

前台接口不需要登录：

| 功能 | 请求方式 | 地址 |
|---|---|---|
| 查询分类 | GET | `/api/site/categories` |
| 查询商品/故事分页 | GET | `/api/site/articles` |
| 查询详情并增加浏览量 | GET | `/api/site/articles/{id}` |

### 2.2 后台接口

后台接口除登录外都需要请求头：

```text
X-Token: 登录接口返回的 token
```

| 功能 | 请求方式 | 地址 |
|---|---|---|
| 登录 | POST | `/api/admin/auth/login` |
| 仪表盘统计 | GET | `/api/admin/dashboard` |
| 分类列表 | GET | `/api/admin/categories` |
| 新增/修改分类 | POST | `/api/admin/categories` |
| 删除分类 | DELETE | `/api/admin/categories/{id}` |
| 内容分页 | GET | `/api/admin/articles` |
| 内容详情 | GET | `/api/admin/articles/{id}` |
| 新增/修改内容 | POST | `/api/admin/articles` |
| 删除内容 | DELETE | `/api/admin/articles/{id}` |
| 查询图片 | GET | `/api/admin/images?type=cover` |
| 上传图片 | POST | `/api/admin/images/upload?type=cover` |
| 删除图片 | DELETE | `/api/admin/images?name=xxx.jpg&type=cover` |

---

## 3. 后端技术栈知识点总览

### 3.1 Spring Boot 3

本项目用到的 Spring Boot 核心知识点：

| 知识点 | 项目中的作用 |
|---|---|
| `@SpringBootApplication` | 启动类 |
| `@RestController` | 声明 REST 接口控制器 |
| `@RequestMapping` | 定义接口公共路径 |
| `@GetMapping` | GET 查询接口 |
| `@PostMapping` | POST 新增/修改接口 |
| `@DeleteMapping` | DELETE 删除接口 |
| `@RequestBody` | 接收 JSON 请求体 |
| `@RequestParam` | 接收 URL 查询参数 |
| `@PathVariable` | 接收路径参数 |
| `@Service` | 声明业务服务类 |
| `@Configuration` | 声明配置类 |
| `@Value` | 读取配置项 |
| `@ConfigurationProperties` | 绑定配置对象 |
| `@Transactional` | 开启事务 |
| `WebMvcConfigurer` | 配置跨域、拦截器、静态资源 |
| `HandlerInterceptor` | 后台登录拦截 |
| `@RestControllerAdvice` | 全局异常处理 |
| `MultipartFile` | 文件上传 |

### 3.2 MyBatis

本项目用到的 MyBatis 核心知识点：

| 知识点 | 项目中的作用 |
|---|---|
| `@Mapper` | 声明 Mapper 接口 |
| XML Mapper | 编写 SQL |
| `namespace` | XML 绑定 Java Mapper 接口 |
| `resultType` | 查询结果映射实体类 |
| `#{}` | 安全参数绑定，防 SQL 注入 |
| `<if>` | 动态 SQL 条件 |
| `<sql>` / `<include>` | 复用 SQL 片段 |
| `useGeneratedKeys` | 插入后回填自增主键 |
| `map-underscore-to-camel-case` | 下划线字段转驼峰属性 |

### 3.3 MySQL

本项目用到三张核心表：

```text
sys_user
blog_category
blog_article
```

特点：

1. 主键使用 `BIGINT AUTO_INCREMENT`。
2. 软删除字段使用 `deleted`。
3. 商品正文使用 `MEDIUMTEXT` 存 Markdown。
4. 金额使用 `DECIMAL(10,2)`。
5. 图片路径使用 `VARCHAR(500)`。
6. 使用索引提升分页查询性能。

### 3.4 Lombok

项目中大量使用：

```java
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
```

作用：减少 getter、setter、构造器等样板代码。

---

## 4. 从 0 创建 Spring Boot 后端项目

### 4.1 准备环境

建议：

```text
JDK 21
Maven 3.8+
MySQL 8.0+
IDEA 2024+
```

查看版本：

```bash
java -version
mvn -v
mysql --version
```

### 4.2 创建 Maven 项目

目录：

```text
backend
├─ pom.xml
└─ src
   └─ main
      ├─ java
      │  └─ com
      │     └─ example
      │        └─ blog
      └─ resources
```

### 4.3 编写 `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.7</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>practical-farm-product-fullstack</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
        <mybatis.spring.boot.version>3.0.4</mybatis.spring.boot.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.spring.boot.version}</version>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 5. 后端目录结构设计

照着当前项目创建：

```text
backend/src/main/java/com/example/blog
├─ BlogApplication.java
├─ common
│  ├─ ApiResponse.java
│  └─ PageResult.java
├─ config
│  ├─ AdminAuthInterceptor.java
│  ├─ AuthProperties.java
│  ├─ TokenStore.java
│  └─ WebConfig.java
├─ controller
│  ├─ AdminArticleController.java
│  ├─ AdminAuthController.java
│  ├─ AdminCategoryController.java
│  ├─ AdminDashboardController.java
│  ├─ AdminImageController.java
│  └─ SiteController.java
├─ dto
│  ├─ ArticleQueryRequest.java
│  ├─ ArticleSaveRequest.java
│  ├─ CategorySaveRequest.java
│  ├─ LoginRequest.java
│  └─ LoginResponse.java
├─ entity
│  ├─ Article.java
│  ├─ Category.java
│  └─ User.java
├─ exception
│  ├─ BusinessException.java
│  └─ GlobalExceptionHandler.java
├─ mapper
│  ├─ ArticleMapper.java
│  ├─ CategoryMapper.java
│  └─ UserMapper.java
├─ service
│  ├─ ArticleService.java
│  ├─ AuthService.java
│  ├─ CategoryService.java
│  └─ impl
│     ├─ ArticleServiceImpl.java
│     ├─ AuthServiceImpl.java
│     └─ CategoryServiceImpl.java
└─ util
   └─ PasswordUtil.java
```

资源目录：

```text
backend/src/main/resources
├─ application.yml
├─ application-dev.yml
├─ application-prod.yml
└─ mapper
   ├─ ArticleMapper.xml
   ├─ CategoryMapper.xml
   └─ UserMapper.xml
```

---

## 6. 第一步：初始化数据库

### 6.1 创建数据库

```sql
DROP DATABASE IF EXISTS practical_farm_product;
CREATE DATABASE practical_farm_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE practical_farm_product;
```

### 6.2 用户表 `sys_user`

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
  password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希值，本项目使用 SHA-256 演示',
  nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
  role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色：admin管理员，user普通管理用户',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='后台管理用户表';
```

### 6.3 分类表 `blog_category`

```sql
CREATE TABLE blog_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0正常，1删除',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_category_name (name)
) COMMENT='商品分类表';
```

### 6.4 内容表 `blog_article`

```sql
CREATE TABLE blog_article (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
  category_id BIGINT NOT NULL COMMENT '分类ID，关联 blog_category.id',
  title VARCHAR(200) NOT NULL COMMENT '标题。商品时为商品名称，故事时为文章标题',
  summary VARCHAR(500) DEFAULT NULL COMMENT '简介，前台列表展示',
  cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片相对路径',
  content MEDIUMTEXT NOT NULL COMMENT '详情内容，支持 Markdown，可以插入图片',
  article_type VARCHAR(20) NOT NULL DEFAULT 'product' COMMENT '内容类型：product商品，story乡村故事',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1发布',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0正常，1删除',
  published_at DATETIME DEFAULT NULL COMMENT '发布时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  price DECIMAL(10,2) DEFAULT NULL COMMENT '商品价格，故事文章为空',
  unit VARCHAR(20) DEFAULT NULL COMMENT '价格单位，例如 元/斤、元/瓶、元/箱',
  sales_text VARCHAR(50) DEFAULT NULL COMMENT '成交量或热度文案',
  product_tags VARCHAR(200) DEFAULT NULL COMMENT '标签，多个用英文逗号分隔',
  farmer_name VARCHAR(50) DEFAULT NULL COMMENT '农户名称或故事讲述人',
  farmer_phone VARCHAR(30) DEFAULT NULL COMMENT '农户联系电话',
  origin_place VARCHAR(100) DEFAULT NULL COMMENT '商品产地或故事地点',
  recommended TINYINT NOT NULL DEFAULT 0 COMMENT '是否时令推荐：0否，1是',
  KEY idx_article_category (category_id),
  KEY idx_article_type (article_type),
  KEY idx_article_status_deleted (status, deleted),
  KEY idx_article_published_at (published_at),
  KEY idx_article_recommended (recommended),
  CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES blog_category(id)
) COMMENT='农产品商品与乡村故事内容表';
```

### 6.5 初始化分类和账号

分类：

```sql
INSERT INTO blog_category(id, name, sort_order) VALUES
(1, '新鲜果蔬', 1),
(2, '禽蛋肉奶', 2),
(3, '粮油杂粮', 3),
(4, '特产农产品', 4),
(5, '其他商品', 5);
```

默认账号：

```text
admin / admin123
farmer01 / farmer123
editor01 / editor123
story01 / story123
```

完整初始化脚本使用项目中的：

```text
sql/init.sql
```

---

## 7. 第二步：编写配置文件

### 7.1 `application.yml`

```yaml
server:
  port: 8088

spring:
  profiles:
    active: dev

  application:
    name: practical-blog-backend

  datasource:
    url: jdbc:mysql://127.0.0.1:3306/practical_farm_product?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:/mapper/*.xml
  type-aliases-package: com.example.blog.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

app:
  auth:
    password-salt: blog-system-fixed-salt
    token-prefix: BLOG_TOKEN_
```

### 7.2 `application-dev.yml`

开发环境配置图片目录：

```yaml
app:
  upload:
    image-root-dir: E:/IDEA_Project/practical-farm-product-fullstack/frontend/public/asset/images
```

你需要改成自己电脑上的实际路径。例如：

```yaml
app:
  upload:
    image-root-dir: D:/project/practical-farm-product-fullstack/frontend/public/asset/images
```

### 7.3 `application-prod.yml`

生产环境示例：

```yaml
app:
  upload:
    image-root-dir: /data/farm-products/uploads/images
```

---

## 8. 第三步：创建启动类

### 8.1 `BlogApplication.java`

```java
package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```

`@SpringBootApplication` 等价于：

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

它会自动扫描 `com.example.blog` 包下面的 Controller、Service、Mapper、Config 等组件。

---

## 9. 第四步：统一响应和分页对象

### 9.1 `ApiResponse.java`

```java
package com.example.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

为什么要统一响应？

前端 Axios 可以统一判断：

```js
if (res.code !== 200) {
  ElMessage.error(res.message)
}
```

### 9.2 `PageResult.java`

```java
package com.example.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
}
```

---

## 10. 第五步：实体类设计

### 10.1 `Category.java`

```java
package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private Integer sortOrder;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 10.2 `User.java`

```java
package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 10.3 `Article.java`

```java
package com.example.blog.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String coverImage;
    private String content;
    private String articleType;
    private Integer status;
    private Integer viewCount;
    private Integer deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BigDecimal price;
    private String unit;
    private String salesText;
    private String productTags;
    private String farmerName;
    private String farmerPhone;
    private String originPlace;
    private Integer recommended;
}
```

`categoryName` 不是 `blog_article` 表字段，而是查询时通过：

```sql
LEFT JOIN blog_category c ON a.category_id = c.id
```

查出来的分类名称。

---

## 11. 第六步：DTO 请求对象

### 11.1 登录请求 `LoginRequest.java`

```java
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

### 11.2 登录响应 `LoginResponse.java`

```java
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nickname;
    private String role;
}
```

### 11.3 分类保存请求 `CategorySaveRequest.java`

```java
@Data
public class CategorySaveRequest {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sortOrder = 0;
}
```

### 11.4 内容查询请求 `ArticleQueryRequest.java`

```java
@Data
public class ArticleQueryRequest {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Long categoryId;
    private Integer status;
    private String keyword;
    private String articleType;

    public Integer getOffset() {
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePageNo - 1) * safePageSize;
    }
}
```

分页 SQL 使用：

```sql
LIMIT #{pageSize} OFFSET #{offset}
```

### 11.5 内容保存请求 `ArticleSaveRequest.java`

```java
@Data
public class ArticleSaveRequest {
    private Long id;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;
    private String coverImage;
    private String articleType = "product";

    @NotBlank(message = "详情内容不能为空")
    private String content;

    private Integer status = 0;
    private BigDecimal price;
    private String unit;
    private String salesText;
    private String productTags;
    private String farmerName;
    private String farmerPhone;
    private String originPlace;
    private Integer recommended = 0;
}
```

---

## 12. 第七步：全局异常处理

### 12.1 业务异常 `BusinessException.java`

```java
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
```

### 12.2 全局异常处理 `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.fail(500, "服务器内部错误：" + e.getMessage());
    }
}
```

好处：Controller 中不用每个方法都 try/catch。

---

## 13. 第八步：Mapper 接口和 XML

### 13.1 `CategoryMapper.java`

```java
@Mapper
public interface CategoryMapper {
    List<Category> selectAll();
    Category selectById(@Param("id") Long id);
    Category selectByName(@Param("name") String name);
    int insert(Category category);
    int update(Category category);
    int softDelete(@Param("id") Long id);
}
```

### 13.2 `CategoryMapper.xml`

```xml
<mapper namespace="com.example.blog.mapper.CategoryMapper">
    <select id="selectAll" resultType="Category">
        SELECT id, name, sort_order, deleted, created_at, updated_at
        FROM blog_category
        WHERE deleted = 0
        ORDER BY sort_order ASC, id ASC
    </select>

    <select id="selectById" resultType="Category">
        SELECT id, name, sort_order, deleted, created_at, updated_at
        FROM blog_category
        WHERE id = #{id} AND deleted = 0
    </select>

    <select id="selectByName" resultType="Category">
        SELECT id, name, sort_order, deleted, created_at, updated_at
        FROM blog_category
        WHERE name = #{name} AND deleted = 0
        LIMIT 1
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO blog_category(name, sort_order)
        VALUES (#{name}, #{sortOrder})
    </insert>

    <update id="update">
        UPDATE blog_category
        SET name = #{name}, sort_order = #{sortOrder}
        WHERE id = #{id} AND deleted = 0
    </update>

    <update id="softDelete">
        UPDATE blog_category SET deleted = 1 WHERE id = #{id}
    </update>
</mapper>
```

### 13.3 `ArticleMapper.java`

```java
@Mapper
public interface ArticleMapper {
    List<Article> selectPage(ArticleQueryRequest query);
    long countPage(ArticleQueryRequest query);
    Article selectById(@Param("id") Long id);
    int insert(Article article);
    int update(Article article);
    int softDelete(@Param("id") Long id);
    int increaseViewCount(@Param("id") Long id);
    long countAll();
    long countPublished();
}
```

### 13.4 `ArticleMapper.xml` 核心分页 SQL

```xml
<select id="selectPage" resultType="Article">
    SELECT <include refid="articleColumns"/>
    FROM blog_article a
    LEFT JOIN blog_category c ON a.category_id = c.id
    WHERE a.deleted = 0
    <if test="categoryId != null">AND a.category_id = #{categoryId}</if>
    <if test="articleType != null and articleType != ''">AND a.article_type = #{articleType}</if>
    <if test="status != null">AND a.status = #{status}</if>
    <if test="keyword != null and keyword != ''">
        AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.summary LIKE CONCAT('%', #{keyword}, '%'))
    </if>
    ORDER BY a.recommended DESC, a.published_at DESC, a.id DESC
    LIMIT #{pageSize} OFFSET #{offset}
</select>
```

这个 SQL 支持：

1. 按分类筛选。
2. 按内容类型筛选：商品或故事。
3. 按状态筛选：草稿或发布。
4. 按关键词模糊搜索。
5. 推荐内容优先排序。
6. 分页查询。

### 13.5 `UserMapper.java`

```java
@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);
}
```

### 13.6 `UserMapper.xml`

```xml
<mapper namespace="com.example.blog.mapper.UserMapper">
    <select id="selectByUsername" resultType="User">
        SELECT id, username, password_hash, nickname, role, status, created_at, updated_at
        FROM sys_user
        WHERE username = #{username}
        LIMIT 1
    </select>
</mapper>
```

---

## 14. 第九步：Service 业务层

### 14.1 为什么需要 Service？

不要把业务逻辑直接写在 Controller 中。

正确分层：

```text
Controller：接收请求、返回响应
Service：处理业务逻辑
Mapper：执行数据库 SQL
```

### 14.2 分类业务 `CategoryServiceImpl`

```java
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category save(CategorySaveRequest request) {
        Category sameName = categoryMapper.selectByName(request.getName());
        if (sameName != null && !sameName.getId().equals(request.getId())) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        category.setId(request.getId());
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (request.getId() == null) {
            categoryMapper.insert(category);
        } else {
            if (categoryMapper.selectById(request.getId()) == null) {
                throw new BusinessException("分类不存在");
            }
            categoryMapper.update(category);
        }
        return category;
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.softDelete(id);
    }
}
```

### 14.3 内容业务 `ArticleServiceImpl`

核心逻辑：

```java
@Override
public PageResult<Article> page(ArticleQueryRequest query) {
    long total = articleMapper.countPage(query);
    return new PageResult<>(articleMapper.selectPage(query), total, query.getPageNo(), query.getPageSize());
}
```

详情页增加浏览量：

```java
@Override
@Transactional
public Article detail(Long id, boolean increaseView) {
    Article article = articleMapper.selectById(id);
    if (article == null) {
        throw new BusinessException("商品不存在");
    }
    if (increaseView) {
        articleMapper.increaseViewCount(id);
        article.setViewCount(article.getViewCount() + 1);
    }
    return article;
}
```

保存内容：

```java
@Override
public Article save(ArticleSaveRequest request) {
    if (categoryMapper.selectById(request.getCategoryId()) == null) {
        throw new BusinessException("商品分类不存在");
    }

    Article article = new Article();
    article.setId(request.getId());
    article.setCategoryId(request.getCategoryId());
    article.setTitle(request.getTitle());
    article.setSummary(request.getSummary());
    article.setCoverImage(request.getCoverImage());
    article.setContent(request.getContent());
    article.setArticleType(request.getArticleType() == null || request.getArticleType().isBlank() ? "product" : request.getArticleType());
    article.setStatus(request.getStatus() == null ? 0 : request.getStatus());
    article.setPrice(request.getPrice());
    article.setUnit(request.getUnit());
    article.setSalesText(request.getSalesText());
    article.setProductTags(request.getProductTags());
    article.setFarmerName(request.getFarmerName());
    article.setFarmerPhone(request.getFarmerPhone());
    article.setOriginPlace(request.getOriginPlace());
    article.setRecommended(request.getRecommended() == null ? 0 : request.getRecommended());

    if (request.getId() == null) {
        articleMapper.insert(article);
    } else {
        if (articleMapper.selectById(request.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        articleMapper.update(article);
    }
    return article;
}
```

### 14.4 登录业务 `AuthServiceImpl`

登录流程：

```text
接收用户名和密码
  ↓
根据用户名查询 sys_user
  ↓
判断用户是否存在、是否启用
  ↓
明文密码 + salt 做 SHA-256
  ↓
和数据库 password_hash 比较
  ↓
生成 token
  ↓
保存到内存 TokenStore
  ↓
返回给前端
```

示例：

```java
String token = authProperties.getTokenPrefix() + UUID.randomUUID();
TokenStore.put(token, user.getId());
return new LoginResponse(token, user.getNickname(), user.getRole());
```

---

## 15. 第十步：Controller 接口层

### 15.1 前台接口 `SiteController`

```java
@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class SiteController {
    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.ok(categoryService.listAll());
    }

    @GetMapping("/articles")
    public ApiResponse<PageResult<Article>> articles(ArticleQueryRequest query) {
        query.setStatus(1);
        return ApiResponse.ok(articleService.page(query));
    }

    @GetMapping("/articles/{id}")
    public ApiResponse<Article> detail(@PathVariable Long id) {
        return ApiResponse.ok(articleService.detail(id, true));
    }
}
```

重点：前台列表强制：

```java
query.setStatus(1);
```

这样前台只展示已发布内容。

### 15.2 登录接口 `AdminAuthController`

```java
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
```

### 15.3 分类接口 `AdminCategoryController`

```java
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryService.listAll());
    }

    @PostMapping
    public ApiResponse<Category> save(@Valid @RequestBody CategorySaveRequest request) {
        return ApiResponse.ok(categoryService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.ok();
    }
}
```

### 15.4 内容接口 `AdminArticleController`

```java
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<PageResult<Article>> page(ArticleQueryRequest query) {
        return ApiResponse.ok(articleService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<Article> detail(@PathVariable Long id) {
        return ApiResponse.ok(articleService.detail(id, false));
    }

    @PostMapping
    public ApiResponse<Article> save(@Valid @RequestBody ArticleSaveRequest request) {
        return ApiResponse.ok(articleService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ApiResponse.ok();
    }
}
```

后台详情不增加浏览量：

```java
articleService.detail(id, false)
```

前台详情增加浏览量：

```java
articleService.detail(id, true)
```

---

## 16. 第十一步：登录拦截器

### 16.1 TokenStore

```java
public class TokenStore {
    private static final Map<String, Long> TOKEN_USER_MAP = new ConcurrentHashMap<>();

    public static void put(String token, Long userId) {
        TOKEN_USER_MAP.put(token, userId);
    }

    public static boolean isValid(String token) {
        return TOKEN_USER_MAP.containsKey(token);
    }

    public static void remove(String token) {
        TOKEN_USER_MAP.remove(token);
    }
}
```

这是学习项目的简单实现。缺点：

1. 后端重启后 token 失效。
2. 多台服务器之间不共享 token。
3. 没有过期时间。

生产项目建议改成 JWT 或 Redis。

### 16.2 `AdminAuthInterceptor`

```java
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("X-Token");
        if (token == null || token.isBlank() || !TokenStore.isValid(token)) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        return true;
    }
}
```

### 16.3 `WebConfig` 注册拦截器

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminAuthInterceptor)
            .addPathPatterns("/api/admin/**")
            .excludePathPatterns("/api/admin/auth/login");
}
```

含义：

```text
拦截 /api/admin/**
但是不拦截 /api/admin/auth/login
```

---

## 17. 第十二步：跨域和静态资源配置

### 17.1 跨域配置

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600);
}
```

前端开发环境虽然使用 Vite 代理，但保留跨域配置有利于直接调接口测试。

### 17.2 图片静态资源映射

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/asset/images/**")
            .addResourceLocations("file:" + resolveImageRootDir());
}
```

作用：

浏览器访问：

```text
/asset/images/covers/product-honey.jpg
```

后端映射到磁盘：

```text
frontend/public/asset/images/covers/product-honey.jpg
```

### 17.3 路径解析注意点

当前项目支持两种路径：

1. 绝对路径：

```yaml
app:
  upload:
    image-root-dir: E:/IDEA_Project/practical-farm-product-fullstack/frontend/public/asset/images
```

2. 相对路径：

```yaml
app:
  upload:
    image-root-dir: ../frontend/public/asset/images
```

后端会转换成标准绝对路径，并补 `/`。

---

## 18. 第十三步：图片管理接口

图片分两类：

| type | 目录 | URL 前缀 | 用途 |
|---|---|---|---|
| `cover` | `covers` | `/asset/images/covers/` | 商品/故事封面 |
| `content` | `content` | `/asset/images/content/` | Markdown 正文插图 |

### 18.1 查询图片

```java
@GetMapping
public ApiResponse<List<Map<String, Object>>> listImages(@RequestParam(defaultValue = "cover") String type) throws Exception
```

返回字段：

```json
{
  "name": "product-honey.jpg",
  "path": "/asset/images/covers/product-honey.jpg",
  "type": "cover",
  "size": 123456
}
```

### 18.2 上传图片

```java
@PostMapping("/upload")
public ApiResponse<Map<String, Object>> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam(defaultValue = "cover") String type
) throws Exception
```

后端校验：

1. 文件不能为空。
2. 文件名不能为空。
3. 后缀必须是：`jpg`、`jpeg`、`png`、`webp`、`gif`、`svg`。
4. 非 SVG 图片宽高不能超过 `1000 × 1000`。
5. 生成安全文件名。
6. 防止路径穿越。

生成文件名格式：

```text
cover-20260502123045-abcd1234.jpg
content-20260502123045-abcd1234.png
```

### 18.3 删除图片

```java
@DeleteMapping
public ApiResponse<Void> delete(
        @RequestParam("name") String name,
        @RequestParam(defaultValue = "cover") String type
) throws Exception
```

安全限制：

```java
if (name.contains("/") || name.contains("\\") || name.contains("..")) {
    return ApiResponse.fail(400, "非法文件名");
}
```

只允许传文件名，不允许传目录。

---

## 19. 第十四步：密码加密工具

当前项目使用 SHA-256 演示密码哈希。

逻辑：

```text
明文密码 + 固定盐 password-salt
  ↓
SHA-256
  ↓
得到 password_hash
```

注意：这适合学习项目。生产项目建议使用：

```text
BCryptPasswordEncoder
```

因为 BCrypt 自带随机盐和慢哈希，更适合密码存储。

---

## 20. 第十五步：接口测试

### 20.1 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：

```text
http://localhost:8088
```

### 20.2 测试登录

请求：

```http
POST http://localhost:8088/api/admin/auth/login
Content-Type: application/json
```

请求体：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "BLOG_TOKEN_xxx",
    "nickname": "系统管理员",
    "role": "admin"
  }
}
```

### 20.3 测试后台接口

请求：

```http
GET http://localhost:8088/api/admin/dashboard
X-Token: BLOG_TOKEN_xxx
```

### 20.4 测试前台商品列表

```http
GET http://localhost:8088/api/site/articles?pageNo=1&pageSize=10&articleType=product
```

### 20.5 测试乡村故事列表

```http
GET http://localhost:8088/api/site/articles?pageNo=1&pageSize=10&articleType=story
```

---

## 21. 启动和运行完整流程

### 21.1 初始化数据库

使用 Navicat 或命令行执行：

```text
sql/init.sql
```

命令行示例：

```bash
mysql -uroot -p < sql/init.sql
```

### 21.2 修改数据库密码

打开：

```text
backend/src/main/resources/application.yml
```

修改：

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码
```

### 21.3 修改图片路径

打开：

```text
backend/src/main/resources/application-dev.yml
```

修改成你本地项目实际路径：

```yaml
app:
  upload:
    image-root-dir: D:/your-path/practical-farm-product-fullstack/frontend/public/asset/images
```

### 21.4 启动后端

```bash
cd backend
mvn spring-boot:run
```

---

## 22. 后端调用链路

### 22.1 查询前台商品列表

```text
浏览器
  ↓
GET /api/site/articles?articleType=product
  ↓
SiteController.articles()
  ↓
ArticleService.page()
  ↓
ArticleMapper.countPage()
ArticleMapper.selectPage()
  ↓
ArticleMapper.xml 动态 SQL
  ↓
MySQL blog_article + blog_category
  ↓
返回 PageResult<Article>
  ↓
ApiResponse.ok(data)
```

### 22.2 后台新增商品

```text
浏览器后台表单
  ↓
POST /api/admin/articles
  ↓
AdminAuthInterceptor 校验 X-Token
  ↓
AdminArticleController.save()
  ↓
@Valid 校验 ArticleSaveRequest
  ↓
ArticleService.save()
  ↓
校验分类是否存在
  ↓
ArticleMapper.insert()
  ↓
MySQL 写入 blog_article
  ↓
返回保存后的 Article
```

### 22.3 登录流程

```text
POST /api/admin/auth/login
  ↓
AdminAuthController.login()
  ↓
AuthService.login()
  ↓
UserMapper.selectByUsername()
  ↓
PasswordUtil 计算哈希
  ↓
TokenStore 保存 token
  ↓
返回 LoginResponse
```

---

## 23. 常见问题排查

### 23.1 数据库连接失败

错误类似：

```text
Access denied for user 'root'@'localhost'
```

检查：

1. `application.yml` 中账号密码是否正确。
2. MySQL 是否启动。
3. 数据库名是否是 `practical_farm_product`。
4. 是否执行了 `sql/init.sql`。

### 23.2 Mapper XML 找不到

检查：

```yaml
mybatis:
  mapper-locations: classpath:/mapper/*.xml
```

XML 必须放在：

```text
src/main/resources/mapper
```

### 23.3 实体字段为空

检查是否开启：

```yaml
map-underscore-to-camel-case: true
```

例如数据库字段：

```text
cover_image
```

Java 字段：

```java
private String coverImage;
```

没有开启下划线转驼峰时，字段可能映射不上。

### 23.4 后台接口返回 401

原因：

1. 没有请求头 `X-Token`。
2. token 错误。
3. 后端重启后内存 token 清空。

解决：重新登录。

### 23.5 图片访问报错：No static resource

常见原因：

1. 数据库保存的图片路径和真实文件路径不一致。
2. `application-dev.yml` 的 `image-root-dir` 配错。
3. 文件在 `frontend/public/asset/images`，但数据库写成了 `/hero-farm-banner.png`。

正确路径示例：

```text
/asset/images/hero-farm-banner.png
/asset/images/covers/product-honey.jpg
```

对应真实文件：

```text
frontend/public/asset/images/hero-farm-banner.png
frontend/public/asset/images/covers/product-honey.jpg
```

### 23.6 删除分类失败

当前表有外键：

```sql
CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES blog_category(id)
```

虽然项目是软删除分类，但如果你改成物理删除，有商品关联时会失败。学习项目建议保留软删除。

---

## 24. 后端推荐学习顺序

1. 先看 `sql/init.sql`，理解表结构。
2. 看 `application.yml`，理解端口、数据库、MyBatis 配置。
3. 看 `BlogApplication.java`，理解启动入口。
4. 看 `ApiResponse` 和 `PageResult`，理解统一返回。
5. 看 `entity`，理解数据库字段和 Java 对象映射。
6. 看 `mapper/*.java` 和 `resources/mapper/*.xml`，理解 MyBatis。
7. 看 `service/impl`，理解业务逻辑。
8. 看 `controller`，理解接口设计。
9. 看 `AdminAuthInterceptor` 和 `WebConfig`，理解后台鉴权。
10. 看 `AdminImageController`，理解文件上传和静态资源映射。

---

## 25. 从零开发时的最小版本路线

建议不要一次写完整项目，按下面顺序迭代：

```text
第 1 步：创建 Spring Boot 项目，启动成功
第 2 步：连接 MySQL，执行 init.sql
第 3 步：写 ApiResponse、PageResult
第 4 步：写 Category 实体、Mapper、XML、Service、Controller
第 5 步：完成分类查询接口
第 6 步：写 Article 实体、Mapper、XML、Service、Controller
第 7 步：完成商品列表和详情接口
第 8 步：写 User、登录接口、密码校验
第 9 步：写 TokenStore 和拦截器
第 10 步：完成后台分类和商品 CRUD
第 11 步：写图片上传、查询、删除接口
第 12 步：补充全局异常处理和参数校验
第 13 步：用 Postman 测试全部接口
第 14 步：和前端联调
```

---

## 26. 可以继续优化的方向

当前项目适合学习，但如果要接近生产项目，可以继续优化：

1. 登录鉴权改为 JWT 或 Redis Token。
2. 密码加密改为 BCrypt。
3. 增加用户管理、角色权限管理。
4. 图片上传改为对象存储，例如 MinIO、阿里云 OSS。
5. 增加接口操作日志。
6. 增加分页参数最大值限制，避免一次查太多。
7. 增加商品上下架操作接口。
8. 增加分类删除前的商品数量检查。
9. 增加统一枚举：内容类型、状态、角色。
10. 增加 Swagger / Knife4j 接口文档。
11. 增加单元测试和集成测试。
12. 增加 Docker 部署脚本。
