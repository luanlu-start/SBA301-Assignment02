import { useEffect, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import api, { errorText } from './LuanLHCE191099_api.js';
import { useAuth } from './LuanLHCE191099_Auth.jsx';
import FormFields from './LuanLHCE191099_FormFields.jsx';
export default function ProfilePage({ endpoint, fields }) {
  const { refresh } = useAuth();
  const [form, setForm] = useState(null);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(false);
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    let active = true;
    api
      .get(endpoint)
      .then(({ data }) => {
        if (active)
          setForm(
            Object.fromEntries(
              fields.map((field) => [
                field.name,
                field.name === 'password' ? '' : (data[field.name] ?? ''),
              ]),
            ),
          );
      })
      .catch((error) => {
        if (active) setError(errorText(error));
      });
    return () => {
      active = false;
    };
  }, [endpoint, fields]);
  async function save(event) {
    event.preventDefault();
    setBusy(true);
    setSaved(false);
    setError('');
    try {
      await api.put(endpoint, form);
      await refresh();
      setForm({ ...form, password: '' });
      setSaved(true);
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  return (
    <section className="profile-shell">
      <h1>My profile</h1>
      <p className="text-secondary">Update your own contact information.</p>
      {error && <Alert variant="danger">{error}</Alert>}
      {saved && <Alert variant="success">Profile updated.</Alert>}
      {form ? (
        <Form onSubmit={save}>
          <FormFields fields={fields} value={form} onChange={setForm} editing />
          <Button type="submit" disabled={busy}>
            {busy ? 'Saving...' : 'Save profile'}
          </Button>
        </Form>
      ) : (
        !error && <Spinner animation="border" />
      )}
    </section>
  );
}
