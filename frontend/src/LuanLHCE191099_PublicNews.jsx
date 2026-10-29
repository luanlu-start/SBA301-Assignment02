import { useState } from 'react';
import { Alert, Badge, Button, Card, Col, Form, Modal, Row, Spinner } from 'react-bootstrap';
import useApiList from './LuanLHCE191099_useApiList.js';
export default function PublicNews() {
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState(null);
  const news = useApiList(`/public/news?q=${encodeURIComponent(search)}`);
  return (
    <>
      <section className="public-intro">
        <p className="text-uppercase small fw-bold">University newsroom</p>
        <h1>Stories and announcements from campus.</h1>
        <p className="mt-3 mb-0">Read the latest published news from our university community.</p>
      </section>
      <Form.Label htmlFor="news-search">Find a story</Form.Label>
      <Form.Control
        id="news-search"
        className="search-box mb-4"
        placeholder="Search title or content..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
      />
      {news.error && (
        <Alert variant="danger">
          {news.error}{' '}
          <Button size="sm" onClick={news.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {news.loading ? (
        <Spinner animation="border" />
      ) : (
        <Row className="g-4">
          {news.data.length ? (
            news.data.map((item) => (
              <Col md={6} lg={4} key={item.id}>
                <Card className="news-card">
                  <Card.Body className="d-flex flex-column">
                    <div>
                      <Badge bg="light" text="dark" className="mb-3">
                        {item.categoryName}
                      </Badge>
                    </div>
                    <Card.Title as="h2">{item.title}</Card.Title>
                    <Card.Text className="text-secondary">
                      {item.headline || item.content.slice(0, 130)}
                    </Card.Text>
                    <small className="text-secondary mb-3">
                      By {item.createdByName} · {new Date(item.createdDate).toLocaleDateString()}
                    </small>
                    <Button
                      variant="outline-primary"
                      className="mt-auto align-self-start"
                      onClick={() => setSelected(item)}
                    >
                      Read article
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            ))
          ) : (
            <Col>
              <Alert variant="info">No published news found.</Alert>
            </Col>
          )}
        </Row>
      )}
      <Modal show={selected !== null} onHide={() => setSelected(null)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>{selected?.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p className="text-secondary">{selected?.headline}</p>
          <p className="content-text">{selected?.content}</p>
          <p className="small text-secondary">
            Source: {selected?.source || 'University newsroom'} · By {selected?.createdByName}
          </p>
          {selected?.tags.map((tag) => (
            <Badge key={tag.id} bg="secondary" className="me-1" title={tag.note}>
              {tag.name}
            </Badge>
          ))}
        </Modal.Body>
      </Modal>
    </>
  );
}
