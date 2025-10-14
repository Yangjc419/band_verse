import React, { useState } from 'react';
import { Card, Form, Input, Select, Button, message, Typography, Tag } from 'antd';
import { useNavigate } from 'react-router-dom';
import { SaveOutlined, EyeOutlined } from '@ant-design/icons';
import './CreatePoem.css';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;
const { Option } = Select;

interface PoemForm {
  title: string;
  content: string;
  category: string;
  tags: string[];
  description?: string;
}

const CreatePoem: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [preview, setPreview] = useState(false);
  const [formData, setFormData] = useState<PoemForm>({
    title: '',
    content: '',
    category: '',
    tags: []
  });

  const categories = ['古诗', '现代诗', '词牌', '散文诗', '打油诗', '其他'];
  const commonTags = [
    '春天', '夏天', '秋天', '冬天',
    '思念', '爱情', '友情', '亲情',
    '山水', '花鸟', '月亮', '星空',
    '人生', '哲理', '励志', '感悟'
  ];

  const handleFormChange = (changedFields: any, allFields: any) => {
    const newFormData = allFields.reduce((acc: any, field: any) => {
      acc[field.name[0]] = field.value;
      return acc;
    }, {});
    setFormData(newFormData);
  };

  const handleSubmit = async (values: PoemForm) => {
    setLoading(true);
    
    try {
      // 模拟提交过程
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      message.success('诗歌发表成功！');
      navigate('/poems');
    } catch (error) {
      message.error('发表失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handlePreview = () => {
    setPreview(!preview);
  };

  const handleTagSelect = (tag: string) => {
    form.setFieldsValue({
      tags: [...(form.getFieldValue('tags') || []), tag]
    });
  };

  return (
    <div className="create-poem-page fade-in">
      <div className="page-header">
        <Title level={2}>创作诗歌</Title>
        <Paragraph>用文字书写你的心境，分享你的诗意人生</Paragraph>
      </div>

      <div className="create-content">
        {!preview ? (
          <Card title="编辑诗歌" className="edit-card">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              onFieldsChange={handleFormChange}
              initialValues={{ tags: [] }}
            >
              <Form.Item
                name="title"
                label="诗歌标题"
                rules={[
                  { required: true, message: '请输入诗歌标题' },
                  { max: 50, message: '标题不能超过50个字符' }
                ]}
              >
                <Input 
                  placeholder="为你的诗歌起一个美丽的标题"
                  size="large"
                />
              </Form.Item>

              <Form.Item
                name="category"
                label="诗歌分类"
                rules={[{ required: true, message: '请选择诗歌分类' }]}
              >
                <Select placeholder="选择诗歌分类" size="large">
                  {categories.map(category => (
                    <Option key={category} value={category}>
                      {category}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                name="content"
                label="诗歌内容"
                rules={[
                  { required: true, message: '请输入诗歌内容' },
                  { min: 10, message: '诗歌内容至少10个字符' }
                ]}
              >
                <TextArea
                  placeholder="在这里写下你的诗歌..."
                  rows={12}
                  style={{ fontSize: '1.1rem', lineHeight: '1.8' }}
                />
              </Form.Item>

              <Form.Item
                name="tags"
                label="标签"
              >
                <Select
                  mode="tags"
                  placeholder="选择或输入标签"
                  style={{ width: '100%' }}
                >
                  {commonTags.map(tag => (
                    <Option key={tag} value={tag}>
                      {tag}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <div className="common-tags">
                <Paragraph>常用标签：</Paragraph>
                {commonTags.map(tag => (
                  <Tag
                    key={tag}
                    className="clickable-tag"
                    onClick={() => handleTagSelect(tag)}
                  >
                    {tag}
                  </Tag>
                ))}
              </div>

              <Form.Item
                name="description"
                label="诗歌简介（可选）"
              >
                <TextArea
                  placeholder="简单介绍一下这首诗的创作背景或含义..."
                  rows={3}
                />
              </Form.Item>

              <Form.Item>
                <div className="form-actions">
                  <Button
                    type="default"
                    icon={<EyeOutlined />}
                    onClick={handlePreview}
                    style={{ marginRight: '1rem' }}
                  >
                    预览
                  </Button>
                  <Button
                    type="primary"
                    htmlType="submit"
                    icon={<SaveOutlined />}
                    loading={loading}
                    size="large"
                  >
                    发表诗歌
                  </Button>
                </div>
              </Form.Item>
            </Form>
          </Card>
        ) : (
          <Card title="预览诗歌" className="preview-card">
            <div className="poem-preview">
              <Title level={3} className="preview-title">
                {formData.title || '无标题'}
              </Title>
              
              <div className="preview-meta">
                <Tag color="blue">{formData.category || '未分类'}</Tag>
              </div>

              <div className="preview-content">
                <pre>{formData.content || '暂无内容'}</pre>
              </div>

              {formData.description && (
                <div className="preview-description">
                  <Paragraph>{formData.description}</Paragraph>
                </div>
              )}

              {formData.tags && formData.tags.length > 0 && (
                <div className="preview-tags">
                  {formData.tags.map(tag => (
                    <Tag key={tag} color="geekblue">{tag}</Tag>
                  ))}
                </div>
              )}
            </div>

            <div className="preview-actions">
              <Button
                type="default"
                onClick={handlePreview}
                style={{ marginRight: '1rem' }}
              >
                返回编辑
              </Button>
              <Button
                type="primary"
                icon={<SaveOutlined />}
                loading={loading}
                onClick={() => handleSubmit(formData)}
              >
                发表诗歌
              </Button>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};

export default CreatePoem;