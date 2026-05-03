# 县城乡村农产品展示网站：Spring Boot 3 + Vue 3 前后端分离实战项目

这是一个从博客系统改造而来的 **乡村县城农产品展示网站**，适合 8~12 小时快速实战 Spring Boot 3、MyBatis、MySQL 8、Vue 3、Vite、Element Plus、Pinia、Axios 和 Markdown 编辑器。

## 一、项目定位

前台是面向客户的农产品展示网站，后台是管理员发布商品、管理分类的管理系统。

前台导航：

```text
首页 | 源头好物 | 时令推荐 | 乡村故事 | 联系农户
```

“源头好物”二级分类：

```text
新鲜果蔬 | 禽蛋肉奶 | 粮油杂粮 | 特产农产品 | 其他商品
```
## 项目文档说明
项目目录下的docs目录下有完整的前后端的项目开发文档


## 二、主要功能

前台功能：

- 农产品首页展示
- 源头好物分类筛选
- 时令推荐筛选
- 商品卡片展示：图片、价格、单位、成交文案、标签、产地、浏览量
- 商品详情页：价格、分类、产地、农户、电话、Markdown 商品详情
- 乡村故事和联系农户模块

后台功能：

- 管理员登录
- 仪表盘统计
- 商品分类管理
- 商品管理：新增、修改、删除、搜索、分页
- 商品详情支持 Markdown 编辑器
- 登录页粒子背景效果

## 三、技术栈

后端：

- JDK 21
- Spring Boot 3.5.7
- MyBatis 3
- MySQL 8.0.26+
- Maven
- Lombok

前端：

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- Element Plus
- md-editor-v3
- markdown-it

## 四、运行步骤

### 1. 初始化数据库

使用 Navicat、DBeaver、MySQL Workbench 或 MySQL 命令行执行：

```text
sql/init.sql
```

默认数据库：

```text
practical_blog
```

默认后台账号：

```text
用户名：admin
密码：admin123
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认端口：

```text
http://localhost:8088
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

<img width="2532" height="1359" alt="d1d8f4f09b27e81b8ea247fee17c84e6" src="https://github.com/user-attachments/assets/f9f7567f-7493-404a-82fd-33cd7ec02fbc" />



后台登录地址：

```text
http://localhost:5173/admin/login
```

## 五、重要说明

为了降低改造成本，后端表名和部分类名仍沿用原来的：

```text
blog_article / Article
blog_category / Category
```

但是业务含义已经改成：

```text
Article = 商品 / 图文详情
Category = 商品分类
```

这样做的好处是改动小、风险低，适合你快速学习前后端分离项目实战。

## 六、建议学习顺序

1. 先执行 `sql/init.sql`，看表结构和测试数据。
2. 启动后端，理解 Controller、Service、Mapper、XML 的调用链路。
3. 启动前端，理解 Axios 如何请求 `/api/site/articles`。
4. 查看 `SiteHome.vue`，学习商品卡片布局和二级菜单。
5. 查看 `AdminArticles.vue`，学习 Element Plus 表格、弹窗、表单和 Markdown 编辑器。
6. 查看 `ArticleDetail.vue`，学习 markdown-it 如何渲染 Markdown 商品详情。

## 本次农产品网站增强版说明

品牌名称已调整为“家有良田”。前台“乡村故事”现在独立展示故事文章，不再混入源头好物商品；“源头好物”菜单支持 5 个二级分类；商品和故事图片统一存放在 `frontend/public/asset/images`，数据库保存 `/asset/images/xxx.svg` 相对路径。

默认账号：

- 管理员：admin / admin123
- 普通管理用户：farmer01 / farmer123，editor01 / editor123，story01 / story123
