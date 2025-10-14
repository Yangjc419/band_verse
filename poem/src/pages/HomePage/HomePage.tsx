import React from 'react';
import { Card, Row, Col, Typography, Button, Carousel } from 'antd';
import { useNavigate } from 'react-router-dom';
import { BookOutlined, EditOutlined, HeartOutlined } from '@ant-design/icons';
import './HomePage.css';

const { Title, Paragraph } = Typography;

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  const featuredPoems = [
    {
      id: '1',
      title: '春晓',
      content: '春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。',
      author: '孟浩然',
    },
    {
      id: '2',
      title: '静夜思',
      content: '床前明月光，疑是地上霜。举头望明月，低头思故乡。',
      author: '李白',
    },
    {
      id: '3',
      title: '登鹳雀楼',
      content: '白日依山尽，黄河入海流。欲穷千里目，更上一层楼。',
      author: '王之涣',
    },
  ];

  const features = [
    {
      icon: <BookOutlined style={{ fontSize: '2rem', color: '#1890ff' }} />,
      title: '诗歌阅读',
      description: '浏览和阅读精选的诗歌作品，感受文字的魅力',
    },
    {
      icon: <EditOutlined style={{ fontSize: '2rem', color: '#52c41a' }} />,
      title: '创作分享',
      description: '创作属于你的诗歌作品，与他人分享你的灵感',
    },
    {
      icon: <HeartOutlined style={{ fontSize: '2rem', color: '#f5222d' }} />,
      title: '互动交流',
      description: '评论、点赞、收藏，与诗友们互动交流',
    },
  ];

  return (
    <div className="home-page fade-in">
      {/* 轮播图区域 */}
      <section className="hero-section">
        <Carousel autoplay>
          {featuredPoems.map((poem) => (
            <div key={poem.id} className="carousel-item">
              <div className="poem-showcase">
                <Title level={2} className="gradient-text">{poem.title}</Title>
                <Paragraph className="poem-content">{poem.content}</Paragraph>
                <Paragraph className="poem-author">—— {poem.author}</Paragraph>
              </div>
            </div>
          ))}
        </Carousel>
      </section>

      {/* 功能介绍区域 */}
      <section className="features-section">
        <Title level={2} style={{ textAlign: 'center', marginBottom: '3rem' }}>
          发现诗歌之美
        </Title>
        <Row gutter={[32, 32]}>
          {features.map((feature, index) => (
            <Col xs={24} md={8} key={index}>
              <Card 
                className="feature-card"
                hoverable
                style={{ textAlign: 'center', height: '100%' }}
              >
                <div className="feature-icon">{feature.icon}</div>
                <Title level={4}>{feature.title}</Title>
                <Paragraph>{feature.description}</Paragraph>
              </Card>
            </Col>
          ))}
        </Row>
      </section>

      {/* 行动号召区域 */}
      <section className="cta-section">
        <Card className="cta-card">
          <Title level={3} style={{ textAlign: 'center' }}>
            开始你的诗歌之旅
          </Title>
          <Paragraph style={{ textAlign: 'center', fontSize: '1.1rem' }}>
            加入我们的诗歌社区，探索无限的创作可能
          </Paragraph>
          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <Button 
              type="primary" 
              size="large" 
              style={{ marginRight: '1rem' }}
              onClick={() => navigate('/poems')}
            >
              浏览诗歌
            </Button>
            <Button 
              size="large"
              onClick={() => navigate('/create')}
            >
              开始创作
            </Button>
          </div>
        </Card>
      </section>
    </div>
  );
};

export default HomePage;