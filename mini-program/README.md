# 家有良田 - 原生微信小程序前端

这是给现有 Spring Boot 农产品信息发布网站新增的 **原生微信小程序前端**。后端代码不需要修改，小程序直接调用现有前台接口：

- `GET /api/site/categories`
- `GET /api/site/articles`
- `GET /api/site/articles/{id}`

## 1. 本地运行步骤

### 第一步：启动后端

进入项目根目录的 `backend`：

```bash
cd backend
mvn spring-boot:run
```

默认后端地址：

```text
http://127.0.0.1:8088
```

### 第二步：导入小程序

打开微信开发者工具：

1. 选择“导入项目”。
2. 项目目录选择：`mini-program`。
3. AppID 可以选择“测试号”或继续使用 `touristappid`。
4. 在开发者工具右上角“详情 / 本地设置”中，勾选：
   - 不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书。

### 第三步：确认接口地址

小程序本地接口配置文件：

```text
mini-program/utils/config.js
```

默认：

```js
const BASE_URL = 'http://127.0.0.1:8088/api'
const IMAGE_BASE_URL = 'http://127.0.0.1:8088'
```

如果是真机调试，不能继续用 `127.0.0.1`，要改成电脑局域网 IP，例如：

```js
const BASE_URL = 'http://192.168.1.8:8088/api'
const IMAGE_BASE_URL = 'http://192.168.1.8:8088'
```

## 2. 页面说明

- `pages/home`：首页，展示品牌头图、快捷入口、精选分类、时令推荐、乡村故事。
- `pages/products`：农产品列表，支持分类筛选、关键词搜索、上拉加载。
- `pages/detail`：商品/故事详情，展示封面、价格、标签、产地、农户电话、正文。
- `pages/stories`：乡村故事列表。
- `pages/contact`：联系站长说明和留言复制功能。

## 3. 注意事项

1. 正式上线小程序时，微信要求后端接口必须使用 HTTPS，并且域名要配置到微信公众平台“小程序后台”的 request 合法域名中。
2. 当前后端没有留言提交接口，所以“联系站长”页先做成本地整理留言并复制到剪贴板，不修改后端。
3. 图片如果后端返回相对路径，小程序会通过 `IMAGE_BASE_URL` 自动拼接。
4. 如果页面没有数据，请先确认 MySQL 已导入 `sql/init.sql`，并且后端连接数据库成功。

## 2026-05-04 自定义底部导航说明

本版本已把微信原生 `tabBar` 改为页面内自定义底部导航，原因是原生 `tabBar` 不能通过 WXSS 调整文字字号和加粗。

主要修改：

1. 删除 `app.json` 中的 `tabBar` 配置。
2. 在 `pages/home/home.wxml`、`pages/products/products.wxml`、`pages/stories/stories.wxml`、`pages/contact/contact.wxml` 底部增加 `.custom-tabbar`。
3. 在 `app.wxss` 中增加 `.custom-tabbar` 和 `.custom-tabbar-item` 样式。
4. 页面跳转从 `wx.switchTab` 改为 `wx.redirectTo`。

如果要继续调大底部文字，修改 `app.wxss`：

```css
.custom-tabbar-item {
  font-size: 30rpx;
  font-weight: 700;
}

.custom-tabbar-item.active {
  font-size: 32rpx;
  font-weight: 900;
}
```
