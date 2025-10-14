import React from 'react';
import { Layout as AntLayout, Menu, Avatar, Dropdown } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  HomeOutlined, 
  BookOutlined, 
  EditOutlined, 
  UserOutlined,
  LogoutOutlined,
  SettingOutlined
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import './Layout.css';

const { Header, Content, Footer } = AntLayout;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/poems',
      icon: <BookOutlined />,
      label: '诗歌列表',
    },
    {
      key: '/create',
      icon: <EditOutlined />,
      label: '创作诗歌',
    },
  ];

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  return (
    <AntLayout className="layout">
      <Header className="header">
        <div className="logo">
          <span className="logo-text">诗韵</span>
        </div>
        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
        />
        <div className="user-section">
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Avatar 
              size="default" 
              icon={<UserOutlined />} 
              className="user-avatar"
            />
          </Dropdown>
        </div>
      </Header>
      <Content className="content">
        <div className="content-wrapper">
          {children}
        </div>
      </Content>
      <Footer className="footer">
        诗韵平台 ©2024 Created by Poetry Lovers
      </Footer>
    </AntLayout>
  );
};

export default Layout;