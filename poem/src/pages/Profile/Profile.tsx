import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Avatar, 
  Typography, 
  Tabs, 
  List, 
  Tag, 
  Button, 
  Modal, 
  Form, 
  Input, 
  Upload,
  message,
  Statistic,
  Row,
  Col
} from 'antd';
import { 
  UserOutlined, 
  EditOutlined, 
  BookOutlined, 
  HeartOutlined,
  EyeOutlined,
  UploadOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { User, Poem } from '../../types';
import './Profile.css';

const { Title, Paragraph, Text } = Typography;
const { TabPane } = Tabs;
const { TextArea } = Input;

const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [userPoems, setUserPoems] = useState<Poem[]>([]);
  const [likedPoems, setLikedPoems] = useState<Poem[]>([]);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [form] = Form.useForm();

  // 模拟用户数据
  const mockUser: User = {
    id: 'user1',
    username: '诗歌爱好者',
    email: 'poetry@example.com',
    bio: '热爱诗歌的文学青年，喜欢用文字记录生活的美好。',
    joinDate: '2023-01-15',
    poemCount: 12,
    followersCount: 156,
    followingCount: 89
  };

  const mockUserPoems: Poem[] = [
    {
      id: '1',
      title: '春日偶成',
      content: '云淡风轻近午天，傍花随柳过前川。',
      author: '诗歌爱好者',
      createTime: '2024-03-15',
      tags: ['春天', '闲适'],
      likes: 128,
      views: 1520,
      category: '古诗'
    },
    {
      id: '2',
      title: '夜雨思君',
      content: '夜雨敲窗声不停，思君不见泪如倾。',
      author: '诗歌爱好者',
      createTime: '2024-03-14',
      tags: ['思念', '夜雨'],
      likes: 89,
      views: 756,
      category: '现代诗'
    }
  ];

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    // 模拟加载用户数据
    setTimeout(() => {
      setUser(mockUser);
      setUserPoems(mockUserPoems);
      setLikedPoems(mockUserPoems.slice(0, 1));
    }, 500);
  };

  const handleEditProfile = () => {
    if (user) {
      form.setFieldsValue({
        username: user.username,
        bio: user.bio
      });
      setEditModalVisible(true);
    }
  };

  const handleSaveProfile = async (values: any) => {
    try {
      // 模拟保存过程
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      if (user) {
        setUser({
          ...user,
          username: values.username,
          bio: values.bio
        });
      }
      
      setEditModalVisible(false);
      message.success('个人资料更新成功');
    } catch (error) {
      message.error('更新失败，请重试');
    }
  };

  const handlePoemClick = (poemId: string) => {
    navigate(`/poem/${poemId}`);
  };

  const renderPoemList = (poems: Poem[]) => (
    <List
      itemLayout="vertical"
      dataSource={poems}
      renderItem={(poem) => (
        <List.Item
          key={poem.id}
          className="poem-list-item"
          onClick={() => handlePoemClick(poem.id)}
          actions={[
            <span key="views">
              <EyeOutlined /> {poem.views}
            </span>,
            <span key="likes">
              <HeartOutlined /> {poem.likes}
            </span>
          ]}
        >
          <List.Item.Meta
            title={
              <div className="poem-item-title">
                <span>{poem.title}</span>
                <Tag color="blue">{poem.category}</Tag>
              </div>
            }
            description={
              <div>
                <Paragraph ellipsis={{ rows: 2 }}>
                  {poem.content}
                </Paragraph>
                <div className="poem-item-meta">
                  <Text type="secondary">{poem.createTime}</Text>
                  <div className="poem-item-tags">
                    {poem.tags.map(tag => (
                      <Tag key={tag} size="small">{tag}</Tag>
                    ))}
                  </div>
                </div>
              </div>
            }
          />
        </List.Item>
      )}
    />
  );

  if (!user) {
    return <div>加载中...</div>;
  }

  return (
    <div className="profile-page fade-in">
      {/* 用户信息卡片 */}
      <Card className="profile-card">
        <div className="profile-header">
          <Avatar 
            size={120} 
            icon={<UserOutlined />}
            src={user.avatar}
          />
          <div className="profile-info">
            <Title level={2}>{user.username}</Title>
            <Paragraph>{user.bio}</Paragraph>
            <Text type="secondary">加入时间：{user.joinDate}</Text>
            <div className="profile-actions">
              <Button 
                type="primary" 
                icon={<EditOutlined />}
                onClick={handleEditProfile}
              >
                编辑资料
              </Button>
            </div>
          </div>
        </div>

        {/* 统计信息 */}
        <div className="profile-stats">
          <Row gutter={32}>
            <Col span={8}>
              <Statistic
                title="作品数量"
                value={user.poemCount}
                prefix={<BookOutlined />}
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="粉丝数量"
                value={user.followersCount}
                prefix={<HeartOutlined />}
              />
            </Col>
            <Col span={8}>
              <Statistic
                title="关注数量"
                value={user.followingCount}
                prefix={<UserOutlined />}
              />
            </Col>
          </Row>
        </div>
      </Card>

      {/* 内容标签页 */}
      <Card className="content-tabs">
        <Tabs defaultActiveKey="poems">
          <TabPane tab="我的作品" key="poems">
            {userPoems.length === 0 ? (
              <div className="empty-state">
                <Paragraph>还没有发表作品</Paragraph>
                <Button 
                  type="primary"
                  onClick={() => navigate('/create')}
                >
                  创作第一首诗
                </Button>
              </div>
            ) : (
              renderPoemList(userPoems)
            )}
          </TabPane>
          <TabPane tab="喜欢的作品" key="liked">
            {likedPoems.length === 0 ? (
              <div className="empty-state">
                <Paragraph>还没有喜欢的作品</Paragraph>
                <Button onClick={() => navigate('/poems')}>
                  去发现好诗
                </Button>
              </div>
            ) : (
              renderPoemList(likedPoems)
            )}
          </TabPane>
        </Tabs>
      </Card>

      {/* 编辑资料弹窗 */}
      <Modal
        title="编辑个人资料"
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        footer={null}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSaveProfile}
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[
              { required: true, message: '请输入用户名' },
              { max: 20, message: '用户名不能超过20个字符' }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="bio"
            label="个人简介"
            rules={[
              { max: 200, message: '个人简介不能超过200个字符' }
            ]}
          >
            <TextArea rows={4} />
          </Form.Item>

          <Form.Item>
            <div style={{ textAlign: 'right' }}>
              <Button 
                style={{ marginRight: '1rem' }}
                onClick={() => setEditModalVisible(false)}
              >
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                保存
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Profile;