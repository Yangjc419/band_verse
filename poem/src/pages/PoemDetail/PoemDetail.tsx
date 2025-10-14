import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Card, 
  Typography, 
  Tag, 
  Button, 
  Divider, 
  Comment, 
  Avatar, 
  Input, 
  message,
  Spin,
  Space
} from 'antd';
import { 
  HeartOutlined, 
  HeartFilled, 
  EyeOutlined, 
  ShareAltOutlined,
  ArrowLeftOutlined,
  CalendarOutlined,
  UserOutlined
} from '@ant-design/icons';
import type { Poem, Comment as CommentType } from '../../types';
import './PoemDetail.css';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;

const PoemDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [poem, setPoem] = useState<Poem | null>(null);
  const [comments, setComments] = useState<CommentType[]>([]);
  const [loading, setLoading] = useState(true);
  const [liked, setLiked] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);

  // 模拟数据
  const mockPoem: Poem = {
    id: '1',
    title: '春日偶成',
    content: `云淡风轻近午天，
傍花随柳过前川。
时人不识余心乐，
将谓偷闲学少年。`,
    author: '程颢',
    createTime: '2024-03-15',
    tags: ['春天', '闲适', '宋诗'],
    likes: 128,
    views: 1520,
    category: '古诗',
    description: '这是一首描写春日美好时光的诗作，表达了诗人内心的愉悦和对自然的热爱。诗中通过对春日景象的描绘，展现了诗人超脱世俗的心境。'
  };

  const mockComments: CommentType[] = [
    {
      id: '1',
      poemId: '1',
      userId: 'user1',
      username: '诗歌爱好者',
      content: '这首诗写得真好，春日的美好跃然纸上！',
      createTime: '2024-03-16 10:30',
      likes: 5
    },
    {
      id: '2',
      poemId: '1',
      userId: 'user2',
      username: '文学青年',
      content: '程颢的诗总是这样清新自然，让人心旷神怡。',
      createTime: '2024-03-16 14:20',
      likes: 3
    }
  ];

  useEffect(() => {
    if (id) {
      loadPoemDetail();
    }
  }, [id]);

  const loadPoemDetail = async () => {
    setLoading(true);
    // 模拟 API 调用
    setTimeout(() => {
      setPoem(mockPoem);
      setComments(mockComments);
      setLoading(false);
    }, 500);
  };

  const handleLike = async () => {
    if (!poem) return;
    
    setLiked(!liked);
    setPoem({
      ...poem,
      likes: liked ? poem.likes - 1 : poem.likes + 1
    });
    
    message.success(liked ? '取消点赞' : '点赞成功');
  };

  const handleShare = () => {
    navigator.clipboard.writeText(window.location.href);
    message.success('链接已复制到剪贴板');
  };

  const handleSubmitComment = async () => {
    if (!newComment.trim()) {
      message.warning('请输入评论内容');
      return;
    }

    setSubmittingComment(true);
    
    // 模拟提交评论
    setTimeout(() => {
      const comment: CommentType = {
        id: Date.now().toString(),
        poemId: id!,
        userId: 'currentUser',
        username: '当前用户',
        content: newComment,
        createTime: new Date().toLocaleString(),
        likes: 0
      };
      
      setComments([comment, ...comments]);
      setNewComment('');
      setSubmittingComment(false);
      message.success('评论发表成功');
    }, 500);
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!poem) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem' }}>
        <Title level={3}>诗歌不存在</Title>
        <Button onClick={() => navigate('/poems')}>返回列表</Button>
      </div>
    );
  }

  return (
    <div className="poem-detail-page fade-in">
      {/* 返回按钮 */}
      <div className="back-button">
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/poems')}
        >
          返回列表
        </Button>
      </div>

      {/* 诗歌内容 */}
      <Card className="poem-content-card">
        <div className="poem-header">
          <Title level={1} className="poem-title">{poem.title}</Title>
          <div className="poem-meta">
            <Space>
              <Tag color="blue">{poem.category}</Tag>
              <Text type="secondary">
                <UserOutlined /> {poem.author}
              </Text>
              <Text type="secondary">
                <CalendarOutlined /> {poem.createTime}
              </Text>
              <Text type="secondary">
                <EyeOutlined /> {poem.views} 次浏览
              </Text>
            </Space>
          </div>
        </div>

        <div className="poem-content">
          <pre>{poem.content}</pre>
        </div>

        {poem.description && (
          <div className="poem-description">
            <Paragraph>{poem.description}</Paragraph>
          </div>
        )}

        <div className="poem-tags">
          {poem.tags.map(tag => (
            <Tag key={tag} color="geekblue">{tag}</Tag>
          ))}
        </div>

        <Divider />

        {/* 操作按钮 */}
        <div className="poem-actions">
          <Space size="large">
            <Button
              type={liked ? "primary" : "default"}
              icon={liked ? <HeartFilled /> : <HeartOutlined />}
              onClick={handleLike}
            >
              {poem.likes} 点赞
            </Button>
            <Button
              icon={<ShareAltOutlined />}
              onClick={handleShare}
            >
              分享
            </Button>
          </Space>
        </div>
      </Card>

      {/* 评论区域 */}
      <Card title="评论区" className="comment-section">
        {/* 发表评论 */}
        <div className="comment-form">
          <TextArea
            rows={4}
            placeholder="写下你的评论..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
          />
          <div style={{ marginTop: '1rem', textAlign: 'right' }}>
            <Button
              type="primary"
              loading={submittingComment}
              onClick={handleSubmitComment}
            >
              发表评论
            </Button>
          </div>
        </div>

        <Divider />

        {/* 评论列表 */}
        <div className="comment-list">
          {comments.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#999' }}>
              暂无评论，快来发表第一条评论吧！
            </div>
          ) : (
            comments.map(comment => (
              <Comment
                key={comment.id}
                author={comment.username}
                avatar={<Avatar icon={<UserOutlined />} />}
                content={comment.content}
                datetime={comment.createTime}
                actions={[
                  <span key="like">
                    <HeartOutlined /> {comment.likes}
                  </span>
                ]}
              />
            ))
          )}
        </div>
      </Card>
    </div>
  );
};

export default PoemDetail;