import { useEffect, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import api, { errorText } from '../services/LuanLHCE191099_api.js';
import { useAuth } from '../context/LuanLHCE191099_AuthContext.jsx';
import FormFields from '../components/Common/LuanLHCE191099_FormFields.jsx';

const profileFields = [
  { name: 'name', label: 'Name', required: true, maxLength: 100 },
  { name: 'email', label: 'Email', type: 'email', required: true, maxLength: 150 },
  {
    name: 'password',
    label: 'New password',
    type: 'password',
    maxLength: 100,
    help: 'Leave blank to keep the current password.',
  },
];

export default function ProfilePage() {
  const endpoint = '/profile';
  const fields = profileFields;
  const { refresh } = useAuth();
  const [formData, setFormData] = useState(null);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    let active = true;

    async function loadProfile() {
      try {
        const response = await api.get(endpoint);
        const profile = response.data;
        const draft = {};

        // Response DTOs never include a password; a blank edit keeps the existing hash.
        for (const field of fields) {
          if (field.name === 'password') {
            draft.password = '';
          } else {
            draft[field.name] = profile[field.name] ?? '';
          }
        }
        if (active) {
          setFormData(draft);
        }
      } catch (error) {
        if (active) {
          setError(errorText(error));
        }
      }
    }

    loadProfile();
    return () => {
      active = false;
    };
  }, [endpoint, fields]);

  async function handleSaveProfile(event) {
    event.preventDefault();
    setIsSaving(true);
    setSaved(false);
    setError('');

    try {
      await api.put(endpoint, formData);
      await refresh();
      setFormData({ ...formData, password: '' });
      setSaved(true);
    } catch (error) {
      setError(errorText(error));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <section className="profile-shell">
      <h1>My profile</h1>
      <p className="text-secondary">Update your own contact information.</p>
      {error && <Alert variant="danger">{error}</Alert>}
      {saved && <Alert variant="success">Profile updated.</Alert>}
      {formData ? (
        <Form onSubmit={handleSaveProfile}>
          <FormFields fields={fields} value={formData} onChange={setFormData} editing />
          <Button type="submit" disabled={isSaving}>
            {isSaving ? 'Saving...' : 'Save profile'}
          </Button>
        </Form>
      ) : (
        !error && <Spinner animation="border" />
      )}
    </section>
  );
}
