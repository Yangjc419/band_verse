import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Layout from './components/Layout/Layout';
import HomePage from './pages/HomePage/HomePage';
import PoemList from './pages/PoemList/PoemList';
import PoemDetail from './pages/PoemDetail/PoemDetail';
import CreatePoem from './pages/CreatePoem/CreatePoem';
import Profile from './pages/Profile/Profile';
import './App.css';

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/poems" element={<PoemList />} />
            <Route path="/poem/:id" element={<PoemDetail />} />
            <Route path="/create" element={<CreatePoem />} />
            <Route path="/profile" element={<Profile />} />
          </Routes>
        </Layout>
      </Router>
    </ConfigProvider>
  );
};

export default App;