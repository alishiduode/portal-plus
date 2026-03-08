-- 初始化示例数据（只在数据库为空时插入）
-- 项目分类
INSERT INTO project_category (id, name, icon, sort_order, create_time, update_time)
SELECT 1, '效率工具', 'el-icon-tools', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project_category WHERE id = 1);

INSERT INTO project_category (id, name, icon, sort_order, create_time, update_time)
SELECT 2, '数据分析', 'el-icon-data-analysis', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project_category WHERE id = 2);

INSERT INTO project_category (id, name, icon, sort_order, create_time, update_time)
SELECT 3, 'API服务', 'el-icon-connection', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project_category WHERE id = 3);

INSERT INTO project_category (id, name, icon, sort_order, create_time, update_time)
SELECT 4, '娱乐应用', 'el-icon-sugar', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project_category WHERE id = 4);

-- 示例项目
INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 1, '任务管理系统', '一个简洁高效的任务管理工具，支持团队协作、任务分配和进度追踪，提升团队工作效率。', 'https://task.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=task', 1, 'Spring Boot, Vue 3, MySQL', 'ACTIVE', true, 1, 1280, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 1);

INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 2, '数据可视化平台', '基于ECharts的数据可视化平台，支持多种图表类型，数据实时更新，帮助业务决策。', 'https://chart.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=chart', 2, 'Vue 3, ECharts, Spring Boot', 'ACTIVE', true, 2, 956, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 2);

INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 3, '接口管理平台', '类似Postman的在线接口测试工具，支持团队共享接口文档，自动生成Mock数据。', 'https://api.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=api', 3, 'Spring Boot, Redis, Vue 3', 'ACTIVE', false, 3, 743, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 3);

INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 4, '在线音乐播放器', '基于网易云API的在线音乐播放器，界面简洁，支持歌词显示和播放列表管理。', 'https://music.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=music', 4, 'Vue 3, Web Audio API', 'ACTIVE', false, 4, 612, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 4);

INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 5, '个人记账本', '简单易用的个人财务记账工具，支持收支统计、预算设置和图表分析。', 'https://finance.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=finance', 1, 'Spring Boot, Vue 3, H2', 'ACTIVE', true, 5, 489, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 5);

INSERT INTO project (id, name, description, url, icon, category_id, tech_stack, status, is_featured, sort_order, visit_count, create_time, update_time)
SELECT 6, '简历生成器', '在线简历制作工具，提供多种精美模板，支持PDF导出，一键美化简历。', 'https://resume.example.com', 'https://api.dicebear.com/7.x/identicon/svg?seed=resume', 1, 'Vue 3, PDF.js, Spring Boot', 'MAINTENANCE', false, 6, 328, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM project WHERE id = 6);
