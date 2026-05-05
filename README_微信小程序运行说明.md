# 家有良田：原生微信小程序 + 留言管理模块运行说明

本压缩包包含：

- `backend/`：Spring Boot 后端，已新增留言接口和后台留言管理接口。
- `frontend/`：Vue3 Web 前端，已新增后台“留言管理”页面。
- `mini-program/`：原生微信小程序前端，已支持在“联系站长”页面提交留言。
- `sql/`：数据库初始化脚本，已新增 `message_contents` 留言表。

## 一、初始化数据库

使用 Navicat、DBeaver、MySQL Workbench 或 MySQL 命令行执行：

```text
sql/init.sql
```

默认数据库：

```text
practical_farm_product
```

新增留言表：

```text
message_contents
```

## 二、启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认端口来自 `backend/src/main/resources/application.yml`：

```text
http://127.0.0.1:8088
```

## 三、启动网站管理后台

```bash
cd frontend
npm install
npm run dev
```

浏览器访问：

```text
http://127.0.0.1:5173/admin/login
```

默认管理员账号：

```text
admin / admin123
```

登录后点击左侧菜单：

```text
留言管理
```

即可看到微信小程序提交的留言信息。

## 四、打开微信小程序

使用微信开发者工具导入：

```text
mini-program
```

本地调试需要在微信开发者工具中勾选：

```text
详情 -> 本地设置 -> 不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书
```

## 五、小程序接口地址

配置文件：

```text
mini-program/utils/config.js
```

默认本地地址：

```js
const BASE_URL = 'http://127.0.0.1:8088/api'
const IMAGE_BASE_URL = 'http://127.0.0.1:8088'
```

如果真机调试，把 `127.0.0.1` 改成电脑局域网 IP，例如：

```js
const BASE_URL = 'http://192.168.1.8:8088/api'
const IMAGE_BASE_URL = 'http://192.168.1.8:8088'
```

## 六、留言功能完整流程

```text
用户在微信小程序“联系站长”填写留言
        ↓
小程序 POST /api/site/messages
        ↓
后端保存到 MySQL 的 message_contents 表
        ↓
管理员登录 Vue 管理后台
        ↓
进入“留言管理”查看、标记已处理、填写处理备注
```

相关接口：

```text
POST   /api/site/messages              小程序提交留言，不需要登录
GET    /api/admin/messages             后台分页查询留言，需要 X-Token
GET    /api/admin/messages/{id}        后台查看留言详情，需要 X-Token
PUT    /api/admin/messages/{id}/status 后台修改处理状态，需要 X-Token
DELETE /api/admin/messages/{id}        后台删除留言，需要 X-Token
```

正式发布微信小程序时，后端必须部署 HTTPS，并在微信公众平台配置 request 合法域名。
