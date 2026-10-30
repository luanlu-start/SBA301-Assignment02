import { useState } from 'react';
import { Badge, Button, Modal } from 'react-bootstrap';
import CrudPage from '../components/Common/LuanLHCE191099_CrudPage.jsx';

// Required public reading: a plain table and an article detail dialog.
export default function PublicNewsPage() {
  const [selectedArticle, setSelectedArticle] = useState(null);
  const columns = [
    { key: 'id', label: 'ID' },
    {
      key: 'title',
      label: 'Title',
      render: (title, article) => (
        <Button
          variant="link"
          className="p-0 text-start"
          onClick={() => setSelectedArticle(article)}
        >
          {title}
        </Button>
      ),
    },
    { key: 'categoryName', label: 'Category' },
    { key: 'createdByName', label: 'Author' },
  ];

  return (
    <>
      <CrudPage title="Published news" endpoint="/public/news" columns={columns} readOnly />
      <Modal show={selectedArticle !== null} onHide={() => setSelectedArticle(null)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>{selectedArticle?.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p className="text-secondary">{selectedArticle?.headline}</p>
          <p className="content-text">{selectedArticle?.content}</p>
          <p className="small text-secondary">
            Source: {selectedArticle?.source || 'University newsroom'} · By{' '}
            {selectedArticle?.createdByName}
          </p>
          {selectedArticle?.tags.map((tag) => (
            <Badge key={tag.id} bg="secondary" className="me-1" title={tag.note}>
              {tag.name}
            </Badge>
          ))}
        </Modal.Body>
      </Modal>
    </>
  );
}
