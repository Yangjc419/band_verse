import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Input, Select, Pagination, Tag, Typography, Empty, Spin } from 'antd';
import { useNavigate } from 'react-router-dom';
import { EyeOutlined, HeartOutlined, CalendarOutlined } from '@ant-design/icons';
import type { Poem, SearchParams } from '../../types';
import './PoemList.css';

const { Search } = Input;
const { Option } = Select;
const { Title, Paragraph } = Typography;

const PoemList: React.FC = () => {
  const navigate = useNavigate();
  const [poems, setPoems] = useState<Poem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [searchParams, setSearchParams] = useState<SearchParams & { page: number; pageSize: number }>({
    page: 1,
    pageSize: 12,
    sortBy: 'createTime',
    sortOrder: 'desc',
  });

  // 模拟数据
  const mockPoems: Poem[] = [
    {
      id: '1',
      title: '春日偶成',
      content: '云淡风轻近午天，傍花随柳过前川。时人不识余心乐，将谓偷闲学少年。',
      author: '程颢',
      createTime: '2024-03-15',
      tags: ['春天', '闲适'],
      likes: 128,
      views: 1520,
      category: '古诗',
      description: '描写春日美好时光的诗作'
    },
    {
      id: '2',
      title: '夜雨思君',
      content: '夜雨敲窗声不停，思君不见泪如倾。何时共赏月圆夜，不负相思不负卿。',
      author: '现代诗人',
      createTime: '2024-03-14',
      tags: ['思念', '夜雨'],
      likes: 89,
      views: 756,
      category: '现代诗',
      description: '表达思念之情的现代诗'
    },
    {
      id: '3',
      title: '山居秋暝',
      content: '空山新雨后，天气晚来秋。明月松间照，清泉石上流。',
      author: '王维',
      createTime: '2024-03-13',
      tags: ['秋天', '山水'],
      likes: 245,
      views: 2134,
      category: '古诗',
      description: '王维的经典山水诗'
    },
    // 更多模拟数据...
  ];

  useEffect(() => {
    loadPoems();
  }, [searchParams]);

  const loadPoems = async () => {
    setLoading(true);
    // 模拟 API 调用
    setTimeout(() => {
      setPoems(mockPoems);
      setTotal(mockPoems.length);
      setLoading(false);
    }, 500);
  };

  const handleSearch = (keyword: string) => {
    setSearchParams(prev => ({ ...prev, keyword, page: 1 }));
  };

  const handleCategoryChange = (category: string) => {
    setSearchParams(prev => ({ ...prev, category: category === 'all' ? undefined : category, page: 1 }));
  };

  const handleSortChange = (value: string) => {
    const [sortBy, sortOrder] = value.split('-');
    setSearchParams(prev => ({ 
      ...prev, 
      sortBy: sortBy as any, 
      sortOrder: sortOrder as any, 
      page: 1 
    }));
  };

  const handlePageChange = (page: number, pageSize?: number) => {
    setSearchParams(prev => ({ ...prev, page, pageSize: pageSize || prev.pageSize }));
  };

  const handlePoemClick = (poemId: string) => {
    navigate(`/poem/${poemId}`);
  };

  const truncateContent = (content: string, maxLength: number = 100) => {
    return content.length > maxLength ? content.substring(0, maxLength) + '...' : content;
  };

  return (
    <div className="poem-list-page fade-in">
      <div className="page-header">
        <Title level={2}>诗歌列表</Title>
        <Paragraph>探索优美的诗歌作品</Paragraph>
      </div>

      {/* 搜索和筛选区域 */}
      <div className="search-section">
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8}>
            <Search
              placeholder="搜索诗歌标题、作者..."
              allowClear
              onSearch={handleSearch}
              style={{ width: '100%' }}
            />
          </Col>
          <Col xs={12} sm={6} md={4}>
            <Select
              placeholder="选择分类"
              allowClear
              style={{ width: '100%' }}
              onChange={handleCategoryChange}
            >
              <Option value="all">全部分类</Option>
              <Option value="古诗">古诗</Option>
              <Option value="现代诗">现代诗</Option>
              <Option value="词牌">词牌</Option>
              <Option value="散文诗">散文诗</Option>
            </Select>
          </Col>
          <Col xs={12} sm={6} md={4}>
            <Select
              defaultValue="createTime-desc"
              style={{ width: '100%' }}
              onChange={handleSortChange}
            >
              <Option value="createTime-desc">最新发布</Option>
              <Option value="likes-desc">最多点赞</Option>
              <Option value="views-desc">最多浏览</Option>
            </Select>
          </Col>
        </Row>
      </div>

      {/* 诗歌列表 */}
      <Spin spinning={loading}>
        {poems.length === 0 ? (
          <Empty description="暂无诗歌作品" />
        ) : (
          <>
            <Row gutter={[24, 24]}>
              {poems.map((poem) => (
                <Col xs={24} sm={12} lg={8} key={poem.id}>
                  <Card
                    className="poem-card"
                    hoverable
                    onClick={() => handlePoemClick(poem.id)}
                    actions={[
                      <span key="views">
                        <EyeOutlined /> {poem.views}
                      </span>,
                      <span key="likes">
                        <HeartOutlined /> {poem.likes}
                      </span>,
                      <span key="date">
                        <CalendarOutlined /> {poem.createTime}
                      </span>,
                    ]}
                  >
                    <Card.Meta
                      title={
                        <div className="poem-title">
                          <span>{poem.title}</span>
                          <Tag color="blue" style={{ marginLeft: 8 }}>
                            {poem.category}
                          </Tag>
                        </div>
                      }
                      description={
                        <div className="poem-meta">
                          <div className="poem-author">作者：{poem.author}</div>
                          <div className="poem-content">
                            {truncateContent(poem.content)}
                          </div>
                          <div className="poem-tags">
                            {poem.tags.map(tag => (
                              <Tag key={tag} size="small">{tag}</Tag>
                            ))}
                          </div>
                        </div>
                      }
                    />
                  </Card>
                </Col>
              ))}
            </Row>

            {/* 分页 */}
            <div className="pagination-section">
              <Pagination
                current={searchParams.page}
                pageSize={searchParams.pageSize}
                total={total}
                showSizeChanger
                showQuickJumper
                showTotal={(total, range) => 
                  `第 ${range[0]}-${range[1]} 项，共 ${total} 项`
                }
                onChange={handlePageChange}
              />
            </div>
          </>
        )}
      </Spin>
    </div>
  );
};

export default PoemList;