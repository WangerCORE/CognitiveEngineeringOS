-- 创建数据库
CREATE DATABASE IF NOT EXISTS ce_os DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ce_os;

-- 创建格式化器表
CREATE TABLE IF NOT EXISTS formatters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '格式化器名称',
    description VARCHAR(500) NOT NULL COMMENT '格式化器描述',
    prompt TEXT NOT NULL COMMENT '提示词',
    example TEXT NOT NULL COMMENT '示例内容',
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED-启用，DISABLED-禁用',
    last_used_time DATETIME COMMENT '最后使用时间',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='格式化器表';

-- 插入示例数据
INSERT INTO formatters (name, description, prompt, example, status) VALUES
('Markdown格式化', '将文本格式化为Markdown格式', '请将以下文本格式化为Markdown格式，包括标题、列表、引用等。', '# 标题\n\n- 列表项1\n- 列表项2\n\n> 引用文本', 'ENABLED'),
('JSON格式化', '将文本格式化为JSON格式', '请将以下文本格式化为规范的JSON格式，注意缩进和换行。', '{\n  "name": "示例",\n  "value": 123\n}', 'ENABLED'),
('SQL格式化', '将SQL语句格式化为标准格式', '请将以下SQL语句格式化为标准格式，包括关键字大写、适当的缩进等。', 'SELECT id, name FROM users WHERE age > 18 ORDER BY created_at DESC', 'ENABLED'); 