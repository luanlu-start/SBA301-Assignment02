import { useState } from 'react';
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap';
import api, { errorText } from './LuanLHCE191099_api.js';
import useApiList from './LuanLHCE191099_useApiList.js';
import FormFields from './LuanLHCE191099_FormFields.jsx';
export const activeOptions = [
  { value: 1, label: 'Active' },
  { value: 0, label: 'Inactive' },
];
export const statusBadge = (value) => (
  <Badge bg={value === 1 ? 'success' : 'secondary'}>{value === 1 ? 'Active' : 'Inactive'}</Badge>
);
export default function CrudPage({
  title,
  endpoint,
  fields = [],
  columns,
  defaults = {},
  prepare = (value) => value,
  toForm,
  readOnly = false,
  deleteHint = '',
  onChanged,
}) {
  const [search, setSearch] = useState('');
  const list = useApiList(`${endpoint}?q=${encodeURIComponent(search)}`);
  const [form, setForm] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [busy, setBusy] = useState(false);
  const [formError, setFormError] = useState('');
  const [notice, setNotice] = useState('');
  function open(row = null) {
    const values = Object.fromEntries(
      fields.map((field) => [
        field.name,
        row ? (row[field.name] ?? '') : (defaults[field.name] ?? ''),
      ]),
    );
    setForm(toForm && row ? toForm(values, row) : values);
    setEditingId(row?.id ?? null);
    setFormError('');
  }
  async function save(event) {
    event.preventDefault();
    setBusy(true);
    setFormError('');
    try {
      const values = { ...form };
      fields
        .filter((field) => field.numeric)
        .forEach((field) => {
          values[field.name] = values[field.name] === '' ? null : Number(values[field.name]);
        });
      const body = prepare(values);
      if (editingId === null) await api.post(endpoint, body);
      else await api.put(`${endpoint}/${editingId}`, body);
      setForm(null);
      setNotice(editingId === null ? 'Record created.' : 'Changes saved.');
      list.reload();
      onChanged?.();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  async function remove() {
    setBusy(true);
    setFormError('');
    try {
      const result = await api.delete(`${endpoint}/${deleting.id}`);
      setNotice(result.data?.message || 'Record deleted.');
      setDeleting(null);
      list.reload();
      onChanged?.();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>{title}</h1>
          <p className="text-secondary mb-0">{list.data.length} record(s)</p>
        </div>
        {!readOnly && <Button onClick={() => open()}>Add new</Button>}
      </div>
      {notice && (
        <Alert variant="success" dismissible onClose={() => setNotice('')}>
          {notice}
        </Alert>
      )}
      <Form.Label htmlFor="record-search" className="visually-hidden">
        Search records
      </Form.Label>
      <Form.Control
        id="record-search"
        className="search-box mb-4"
        placeholder="Search..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
      />
      {list.error && (
        <Alert variant="danger">
          {list.error}{' '}
          <Button size="sm" variant="outline-danger" onClick={list.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {list.loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading</span>
        </Spinner>
      ) : (
        <div className="table-shell">
          <Table responsive hover className="mb-0 align-middle">
            <thead>
              <tr>
                {columns.map((column) => (
                  <th key={column.key}>{column.label}</th>
                ))}
                {!readOnly && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {list.data.length ? (
                list.data.map((row) => (
                  <tr key={row.id}>
                    {columns.map((column) => (
                      <td key={column.key}>
                        {column.render
                          ? column.render(row[column.key], row)
                          : String(row[column.key] ?? '—')}
                      </td>
                    ))}
                    {!readOnly && (
                      <td className="text-nowrap">
                        <Button
                          size="sm"
                          variant="outline-primary"
                          className="me-2"
                          onClick={() => open(row)}
                        >
                          Edit
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-danger"
                          onClick={() => {
                            setDeleting(row);
                            setFormError('');
                          }}
                        >
                          Delete
                        </Button>
                      </td>
                    )}
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan={columns.length + (readOnly ? 0 : 1)}
                    className="text-center py-4 text-secondary"
                  >
                    No records found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </div>
      )}
      <Modal show={form !== null} onHide={() => !busy && setForm(null)} size="lg" backdrop="static">
        <Form onSubmit={save}>
          <Modal.Header closeButton={!busy}>
            <Modal.Title>
              {editingId === null ? 'Create' : 'Edit'} — {title}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            {form && (
              <FormFields
                fields={fields}
                value={form}
                onChange={setForm}
                editing={editingId !== null}
              />
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" disabled={busy} onClick={() => setForm(null)}>
              Cancel
            </Button>
            <Button type="submit" disabled={busy}>
              {busy ? 'Saving...' : 'Save'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
      <Modal show={deleting !== null} onHide={() => !busy && setDeleting(null)}>
        <Modal.Header closeButton={!busy}>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {formError && <Alert variant="danger">{formError}</Alert>}Delete record #{deleting?.id}?
          {deleteHint && <p className="mt-2 text-secondary mb-0">{deleteHint}</p>}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={busy} onClick={() => setDeleting(null)}>
            Cancel
          </Button>
          <Button variant="danger" disabled={busy} onClick={remove}>
            {busy ? 'Deleting...' : 'Delete'}
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}
