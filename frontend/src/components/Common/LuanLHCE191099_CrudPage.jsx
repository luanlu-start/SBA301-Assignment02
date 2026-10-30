import { useState } from 'react';
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap';
import api, { errorText } from '../../services/LuanLHCE191099_api.js';
import useApiList from '../../hooks/LuanLHCE191099_useApiList.js';
import FormFields from './LuanLHCE191099_FormFields.jsx';

export const activeOptions = [
  { value: 1, label: 'Active' },
  { value: 0, label: 'Inactive' },
];

export function statusBadge(status) {
  const isActive = status === 1;
  return <Badge bg={isActive ? 'success' : 'secondary'}>{isActive ? 'Active' : 'Inactive'}</Badge>;
}

function keepValues(values) {
  return values;
}

// Page components describe fields/columns; this component owns the shared CRUD workflow.
// Follow the named handlers in order: open a dialog -> edit the draft -> submit -> reload.
export default function CrudPage({
  title,
  endpoint,
  fields = [],
  columns,
  defaults = {},
  prepareRequest = keepValues,
  mapRecordToForm,
  readOnly = false,
  deleteHint = '',
  onChanged,
}) {
  const [search, setSearch] = useState('');
  const records = useApiList(`${endpoint}?q=${encodeURIComponent(search)}`);
  const [formData, setFormData] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [recordToDelete, setRecordToDelete] = useState(null);
  const [isBusy, setIsBusy] = useState(false);
  const [formError, setFormError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // Select only editable fields. Never send the entire response DTO back to the API.
  function buildFormData(record = null) {
    const values = {};
    for (const field of fields) {
      if (record !== null) {
        values[field.name] = record[field.name] ?? '';
      } else {
        values[field.name] = defaults[field.name] ?? '';
      }
    }

    if (record !== null && mapRecordToForm) {
      return mapRecordToForm(values, record);
    }
    return values;
  }

  function openCreateModal() {
    setEditingId(null);
    setFormData(buildFormData());
    setFormError('');
  }

  function openEditModal(record) {
    setEditingId(record.id);
    setFormData(buildFormData(record));
    setFormError('');
  }

  function closeEditor() {
    if (!isBusy) {
      setFormData(null);
    }
  }

  // Inputs provide strings; convert fields marked numeric before creating the request body.
  function buildRequestBody() {
    const values = { ...formData };
    for (const field of fields) {
      if (field.numeric) {
        values[field.name] = values[field.name] === '' ? null : Number(values[field.name]);
      }
    }
    return prepareRequest(values);
  }

  function reloadAfterChange() {
    records.reload();
    if (onChanged) {
      onChanged();
    }
  }

  async function handleSave(event) {
    event.preventDefault();
    setIsBusy(true);
    setFormError('');

    try {
      const requestBody = buildRequestBody();
      if (editingId === null) {
        await api.post(endpoint, requestBody);
        setSuccessMessage('Record created.');
      } else {
        await api.put(`${endpoint}/${editingId}`, requestBody);
        setSuccessMessage('Changes saved.');
      }
      setFormData(null);
      reloadAfterChange();
    } catch (error) {
      setFormError(errorText(error));
    } finally {
      setIsBusy(false);
    }
  }

  function openDeleteModal(record) {
    setRecordToDelete(record);
    setFormError('');
  }

  function closeDeleteModal() {
    if (!isBusy) {
      setRecordToDelete(null);
    }
  }

  async function handleDelete() {
    setIsBusy(true);
    setFormError('');

    try {
      const response = await api.delete(`${endpoint}/${recordToDelete.id}`);
      setSuccessMessage(response.data?.message || 'Record deleted.');
      setRecordToDelete(null);
      reloadAfterChange();
    } catch (error) {
      // A 409 stays visible in the dialog when related data prevents deletion.
      setFormError(errorText(error));
    } finally {
      setIsBusy(false);
    }
  }

  function renderCell(column, record) {
    const value = record[column.key];
    if (column.render) {
      return column.render(value, record);
    }
    return String(value ?? '—');
  }

  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>{title}</h1>
          <p className="text-secondary mb-0">{records.data.length} record(s)</p>
        </div>
        {!readOnly && <Button onClick={openCreateModal}>Add new</Button>}
      </div>
      {successMessage && (
        <Alert variant="success" dismissible onClose={() => setSuccessMessage('')}>
          {successMessage}
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
      {records.error && (
        <Alert variant="danger">
          {records.error}{' '}
          <Button size="sm" variant="outline-danger" onClick={records.reload}>
            Retry
          </Button>
        </Alert>
      )}
      {records.loading ? (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading</span>
        </Spinner>
      ) : (
        <div className="table-shell">
          {/* columns.map builds the configured cells; row.id is a stable React key for each record. */}
          {/* A column render function can turn raw values into badges or a readable tag records. */}
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
              {records.data.length ? (
                records.data.map((row) => (
                  <tr key={row.id}>
                    {columns.map((column) => (
                      <td key={column.key}>{renderCell(column, row)}</td>
                    ))}
                    {!readOnly && (
                      <td className="text-nowrap">
                        <Button
                          size="sm"
                          variant="outline-primary"
                          className="me-2"
                          onClick={() => openEditModal(row)}
                        >
                          Edit
                        </Button>
                        <Button
                          size="sm"
                          variant="outline-danger"
                          onClick={() => openDeleteModal(row)}
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
      {/* One modal serves both Create and Edit. A non-null draft opens it. */}
      {/* FormFields receives the draft and its setter, so input changes are reflected in parent state. */}
      <Modal show={formData !== null} onHide={closeEditor} size="lg" backdrop="static">
        <Form onSubmit={handleSave}>
          <Modal.Header closeButton={!isBusy}>
            <Modal.Title>
              {editingId === null ? 'Create' : 'Edit'} — {title}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            {formData && (
              <FormFields
                fields={fields}
                value={formData}
                onChange={setFormData}
                editing={editingId !== null}
              />
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" disabled={isBusy} onClick={() => setFormData(null)}>
              Cancel
            </Button>
            <Button type="submit" disabled={isBusy}>
              {isBusy ? 'Saving...' : 'Save'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
      {/* Selecting Delete opens this confirmation; only its final Delete button invokes handleDelete(). */}
      <Modal show={recordToDelete !== null} onHide={closeDeleteModal}>
        <Modal.Header closeButton={!isBusy}>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {formError && <Alert variant="danger">{formError}</Alert>}Delete record #
          {recordToDelete?.id}?
          {deleteHint && <p className="mt-2 text-secondary mb-0">{deleteHint}</p>}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={isBusy} onClick={() => setRecordToDelete(null)}>
            Cancel
          </Button>
          <Button variant="danger" disabled={isBusy} onClick={handleDelete}>
            {isBusy ? 'Deleting...' : 'Delete'}
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}
