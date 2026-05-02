-- =========================================================
-- 家有良田 - 乡村县城农产品展示网站 初始化脚本
-- 适配：MySQL 8.0.26+
-- 默认管理员：admin / admin123
-- 普通管理用户：farmer01 / farmer123，editor01 / editor123，story01 / story123
-- =========================================================

DROP DATABASE IF EXISTS practical_farm_product;
CREATE DATABASE practical_farm_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE practical_farm_product;

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

CREATE TABLE blog_category (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
                               name VARCHAR(50) NOT NULL COMMENT '分类名称',
                               sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
                               deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0正常，1删除',
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               UNIQUE KEY uk_category_name (name)
) COMMENT='商品分类表';

CREATE TABLE blog_article (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
                              category_id BIGINT NOT NULL COMMENT '分类ID，关联 blog_category.id',
                              title VARCHAR(200) NOT NULL COMMENT '标题。商品时为商品名称，故事时为文章标题',
                              summary VARCHAR(500) DEFAULT NULL COMMENT '简介，前台列表展示',
                              cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片相对路径，例如 /asset/images/covers/product-goji.svg',
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
                              sales_text VARCHAR(50) DEFAULT NULL COMMENT '成交量或热度文案，例如 成交17.8万元',
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

-- 管理员和普通管理用户
INSERT INTO sys_user(username, password_hash, nickname, role, status) VALUES
                                                                          ('admin', '06ed1be3ba0335dd36d285bd4fe12d0b5fbc1ba4faa5f5fba1b190874cdc7bc1', '系统管理员', 'admin', 1),
                                                                          ('farmer01', 'c47d3cb5b54c535b3078fa134a7391f2cdc0dc950fdd10afbe19497c6eb406f3', '农户运营员', 'user', 1),
                                                                          ('editor01', '81b2b0986b04035bc5e1b87e6e9e6e8ab8eca53ceab5a9af5c9f8a1e71f2cec5', '内容编辑员', 'user', 1),
                                                                          ('story01', '0355cc51123981a1746d29bcf0c454e57ceb250b1fcbcd12d6392ab293dcd01a', '乡村故事编辑', 'user', 1);

-- 源头好物二级分类，前台下拉菜单和分类筛选都会读取这里
INSERT INTO blog_category(id, name, sort_order) VALUES
                                                    (1, '新鲜果蔬', 1),
                                                    (2, '禽蛋肉奶', 2),
                                                    (3, '粮油杂粮', 3),
                                                    (4, '特产农产品', 4),
                                                    (5, '其他商品', 5);

INSERT INTO blog_article(category_id, title, summary, cover_image, content, article_type, status, view_count, published_at, price, unit, sales_text, product_tags, farmer_name, farmer_phone, origin_place, recommended)
VALUES
                                                                                                                                                                                                                             (1, '中宁枸杞干货 产地直发', '本地农户采摘晾晒，颗粒饱满，适合泡水煲汤。', '/asset/images/covers/product-goji.svg', '# 中宁枸杞干货\n\n![中宁枸杞](/asset/images/covers/product-goji.svg)\n\n本地农户种植，人工采摘，产地直发。\n\n## 商品特点\n\n- 颗粒饱满，色泽自然\n- 适合泡水、煲汤、送礼\n- 支持批量采购和农户直连', 'product', 1, 128, NOW(), 10.80, '元/斤', '成交17.8万元', '货版一致,源头直发,部分包邮', '王大叔', '13800000001', '中宁县枸杞种植基地', 1),
                                                                                                                                                                                                                             (1, '果园现摘无花果 新鲜水果', '果园现摘，成熟度高，适合家庭鲜食。', '/asset/images/covers/product-fig.svg', '# 果园现摘无花果\n\n![现摘无花果](/asset/images/covers/product-fig.svg)\n\n果园现摘，软糯清甜。\n\n- 现摘现发\n- 支持本地自提\n- 可按箱咨询', 'product', 1, 86, NOW(), 12.80, '元/斤', '成交25.1万元', '现摘现发,部分包邮', '李姐果园', '13800000002', '本地果园', 1),
                                                                                                                                                                                                                             (1, '当季黄瓜 新鲜采摘', '大棚当季黄瓜，清脆爽口，适合凉拌和炒菜。', '/asset/images/covers/product-cucumber.svg', '# 当季黄瓜\n\n![当季黄瓜](/asset/images/covers/product-cucumber.svg)\n\n当天采摘，清脆爽口。', 'product', 1, 53, NOW(), 2.60, '元/斤', '成交3.2万元', '新鲜采摘,本地蔬菜,支持团购', '赵师傅', '13800000003', '城郊蔬菜大棚', 0),
                                                                                                                                                                                                                             (2, '农家散养土鸡蛋', '农户散养土鸡蛋，蛋黄饱满，适合家庭日常食用。', '/asset/images/covers/product-egg.svg', '# 农家散养土鸡蛋\n\n![农家散养土鸡蛋](/asset/images/covers/product-egg.svg)\n\n本地农户散养土鸡产蛋，数量有限，新鲜供应。', 'product', 1, 212, NOW(), 18.80, '元/斤', '成交6.8万元', '农户直供,新鲜配送,散养鸡蛋', '赵大哥', '13800000004', '本地散养农户', 1),
                                                                                                                                                                                                                             (2, '农家土鸡 散养走地鸡', '本地农户散养土鸡，肉质紧实，适合炖汤。', '/asset/images/covers/product-chicken.svg', '# 农家土鸡\n\n![农家土鸡](/asset/images/covers/product-chicken.svg)\n\n本地农户散养走地鸡，活动空间大，肉质紧实。', 'product', 1, 96, NOW(), 35.00, '元/斤', '成交9.3万元', '散养土鸡,肉质紧实,提前预约', '马师傅', '13800000005', '山脚散养基地', 1),
                                                                                                                                                                                                                             (2, '本地羊奶 新鲜配送', '本地养殖户供应羊奶，适合提前预订。', '/asset/images/covers/product-milk.svg', '# 本地羊奶\n\n![本地羊奶](/asset/images/covers/product-milk.svg)\n\n本地养殖户每日供应，新鲜配送。', 'product', 1, 41, NOW(), 12.00, '元/瓶', '成交1.9万元', '本地养殖,每日供应,提前预订', '周大姐', '13800000006', '本地养殖场', 0),
                                                                                                                                                                                                                             (3, '生态大米 农户自产', '本地生态大米，颗粒饱满，适合家庭和单位采购。', '/asset/images/covers/product-rice.svg', '# 生态大米\n\n![生态大米](/asset/images/covers/product-rice.svg)\n\n本地农户自产生态大米，米粒饱满，口感自然。', 'product', 1, 176, NOW(), 4.80, '元/斤', '成交12.6万元', '生态种植,粮油杂粮,农户自产', '刘师傅', '13800000007', '本地稻田', 1),
                                                                                                                                                                                                                             (3, '农家菜籽油 物理压榨', '本地油菜籽压榨，香味浓，适合家庭炒菜。', '/asset/images/covers/product-oil.svg', '# 农家菜籽油\n\n![农家菜籽油](/asset/images/covers/product-oil.svg)\n\n本地油菜籽压榨，香味浓郁，适合家庭日常炒菜。', 'product', 1, 88, NOW(), 68.00, '元/桶', '成交5.7万元', '物理压榨,农家菜籽油,桶装供应', '陈师傅', '13800000008', '本地榨油坊', 1),
                                                                                                                                                                                                                             (3, '农家小米 五谷杂粮', '本地农户种植小米，适合煮粥。', '/asset/images/covers/product-millet.svg', '# 农家小米\n\n![农家小米](/asset/images/covers/product-millet.svg)\n\n本地农户种植小米，适合煮粥、养胃餐。', 'product', 1, 62, NOW(), 6.50, '元/斤', '成交2.8万元', '五谷杂粮,农户自产,适合煮粥', '何大叔', '13800000009', '本地旱地种植区', 0),
                                                                                                                                                                                                                             (4, '手工红薯粉 农家特产', '传统工艺制作，口感筋道，适合火锅、炖菜、凉拌。', '/asset/images/covers/product-noodle.svg', '# 手工红薯粉\n\n![手工红薯粉](/asset/images/covers/product-noodle.svg)\n\n农家手工制作红薯粉，口感筋道。', 'product', 1, 134, NOW(), 9.50, '元/斤', '成交10.4万元', '一件代发,特产农品,农家手作', '张阿姨', '13800000010', '本地乡村作坊', 1),
                                                                                                                                                                                                                             (4, '农家手工辣酱', '本地辣椒手工熬制，拌饭、拌面都合适。', '/asset/images/covers/product-sauce.png', '# 农家手工辣酱\n\n![农家手工辣酱](/asset/images/covers/product-sauce.png)\n\n本地辣椒手工熬制，香辣下饭。', 'product', 1, 73, NOW(), 16.80, '元/瓶', '成交4.5万元', '手工熬制,本地辣椒,下饭酱', '孙阿姨', '13800000011', '本地农家厨房', 1),
                                                                                                                                                                                                                             (4, '农家腌菜 老坛风味', '本地农家腌制，酸香开胃，适合配粥下饭。', '/asset/images/covers/product-pickle.svg', '# 农家腌菜\n\n![农家腌菜](/asset/images/covers/product-pickle.svg)\n\n农家老坛腌制，酸香开胃。', 'product', 1, 45, NOW(), 8.00, '元/袋', '成交1.6万元', '老坛风味,农家腌制,开胃下饭', '吴大姐', '13800000012', '本地乡村', 0),
                                                                                                                                                                                                                             (5, '农户手编竹篮', '农户手工编织竹篮，可用于收纳、采摘、装饰。', '/asset/images/covers/product-basket.svg', '# 农户手编竹篮\n\n![农户手编竹篮](/asset/images/covers/product-basket.svg)\n\n本地农户手工编织竹篮，实用又有乡土特色。', 'product', 1, 31, NOW(), 28.00, '元/个', '成交0.9万元', '农户手作,竹编,乡村好物', '黄师傅', '13800000013', '本地竹编农户', 0),
                                                                                                                                                                                                                     (5, '农家蜂蜜 土蜂蜜', '本地蜂农采收，口感清甜，适合冲水、烘焙。', '/asset/images/covers/product-honey.jpg', '# 农家蜂蜜\n\n![农家蜂蜜](/asset/images/covers/product-honey.jpg)\n\n本地蜂农采收土蜂蜜，口感清甜。', 'product', 1, 102, NOW(), 58.00, '元/瓶', '成交7.2万元', '蜂农直供,土蜂蜜,自然清甜', '牛师傅', '13800000014', '本地蜂场', 1),
-- 乡村故事文章：不会出现在“源头好物”商品列表，只会在“乡村故事”里展示
                                                                                                                                                                                                                             (5, '清晨五点的枸杞采摘', '王大叔一家每天清晨进地采摘，只为让客户拿到更新鲜的枸杞。', '/asset/images/covers/story-harvest.svg', '# 清晨五点的枸杞采摘\n\n![清晨采摘](/asset/images/covers/story-harvest.svg)\n\n天还没亮，王大叔就和家人一起到枸杞地里准备采摘。\n\n## 为什么要赶早？\n\n清晨温度低，果实状态更稳定，也更适合采摘和分拣。\n\n这类乡村故事文章不会展示在源头好物商品列表中，只在乡村故事栏目展示。', 'story', 1, 35, NOW(), NULL, NULL, NULL, '农户故事,采摘记录,产地直连', '王大叔', '13800000001', '中宁县枸杞种植基地', 0),
                                                                                                                                                                                                                             (5, '一块菜地的四季管理', '从翻土、育苗到采摘，赵师傅记录了一块菜地一整年的变化。', '/asset/images/covers/story-field.svg', '# 一块菜地的四季管理\n\n![田间管理](/asset/images/covers/story-field.svg)\n\n春天翻土，夏天除草，秋天采摘，冬天养地。\n\n## 种菜不是一天的事情\n\n每一次浇水、施肥、除草，都会影响最后端上餐桌的味道。', 'story', 1, 42, NOW(), NULL, NULL, NULL, '种植过程,乡村生活,田间管理', '赵师傅', '13800000003', '城郊蔬菜大棚', 0),
                                                                                                                                                                                                                             (5, '老街赶集与农产品交易', '乡镇赶集不仅是买卖，也是乡村生活的一部分。', '/asset/images/covers/story-market.svg', '# 老街赶集与农产品交易\n\n![赶集记忆](/asset/images/covers/story-market.svg)\n\n每到集市日，农户会把自家的鸡蛋、蔬菜、杂粮拿到街上售卖。\n\n## 线上展示，线下信任\n\n网站把这些好物展示出来，但真正的信任来自产地、农户和长期口碑。', 'story', 1, 28, NOW(), NULL, NULL, NULL, '乡村故事,赶集记忆,农户生活', '内容编辑员', '13800000000', '本地老街', 0);


-- =========================================================
-- 合并自 patch_fix_home_and_cover_images.sql
-- 说明：当前 init.sql 中的数据本身已经是新版图片路径，
--      这里继续保留补丁 UPDATE，保证旧路径或误写路径也会被修正。
-- =========================================================

/*
  修复已有数据库中的图片路径。

  适用场景：
  你之前已经执行过旧版 init.sql，数据库里的 cover_image 仍然是：
  /asset/images/product-xxx.svg

  新版项目封面图统一放在：
  /asset/images/covers/

  执行本脚本后，旧路径会批量修正为新版路径。
*/
-- 1. 批量把旧封面路径迁移到 covers 目录。
UPDATE blog_article
SET cover_image = REPLACE(cover_image, '/asset/images/', '/asset/images/covers/')
WHERE cover_image IS NOT NULL
  AND cover_image <> ''
  AND cover_image NOT LIKE '/asset/images/covers/%'
  AND cover_image NOT LIKE '/asset/images/content/%';

-- 2. 修复两张真实图片文件名：辣酱使用 png，蜂蜜使用 jpg。
UPDATE blog_article
SET cover_image = '/asset/images/covers/product-sauce.png'
WHERE title LIKE '%辣酱%';

UPDATE blog_article
SET cover_image = '/asset/images/covers/product-honey.jpg'
WHERE title LIKE '%蜂蜜%';

-- 3. 如果正文 Markdown 里还是旧图片路径，也一并迁移到 covers 目录，避免详情页裂图。
UPDATE blog_article
SET content = REPLACE(content, '/asset/images/product-', '/asset/images/covers/product-')
WHERE content LIKE '%/asset/images/product-%';

UPDATE blog_article
SET content = REPLACE(content, '/asset/images/story-', '/asset/images/covers/story-')
WHERE content LIKE '%/asset/images/story-%';

UPDATE blog_article
SET content = REPLACE(content, 'product-sauce.svg', 'product-sauce.png')
WHERE content LIKE '%product-sauce.svg%';

UPDATE blog_article
SET content = REPLACE(content, 'product-honey.svg', 'product-honey.jpg')
WHERE content LIKE '%product-honey.svg%';

SELECT id, title, cover_image
FROM blog_article
ORDER BY id;
