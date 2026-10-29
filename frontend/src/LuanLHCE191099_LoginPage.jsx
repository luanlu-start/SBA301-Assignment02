import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Alert, Button, Card, Form } from 'react-bootstrap';
import api, { errorText } from './LuanLHCE191099_api.js';
import { useAuth } from './LuanLHCE191099_Auth.jsx';
import FormFields from './LuanLHCE191099_FormFields.jsx';
export default function LoginPage({ registerFields = [], destinations }) {
  const auth = useAuth();
  const navigate = useNavigate();
  const [registering, setRegistering] = useState(false);
  const [form, setForm] = useState({
    email: '',
    password: '',
    fullName: '',
    telephone: '',
    birthday: '',
  });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  async function submit(event) {
    event.preventDefault();
    setError('');
    setBusy(true);
    try {
      if (registering)
        await api.post(
          '/auth/register',
          Object.fromEntries(registerFields.map((field) => [field.name, form[field.name]])),
        );
      const user = await auth.login(form.email, form.password);
      navigate(destinations[user.role] || '/', { replace: true });
    } catch (error) {
      setError(errorText(error));
    } finally {
      setBusy(false);
    }
  }
  const loginFields = [
    { name: 'email', label: 'Email', type: 'email', required: true, maxLength: 150 },
    { name: 'password', label: 'Password', type: 'password', required: true, maxLength: 100 },
  ];
  return (
    <Card className="login-shell">
      <Card.Body className="p-4">
        <h1 className="h3">{registering ? 'Create customer account' : 'Sign in'}</h1>
        <p className="text-secondary">
          {registering
            ? 'Register to book rooms and manage your stays.'
            : 'Enter your email and password.'}
        </p>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={submit}>
          <FormFields
            fields={registering ? registerFields : loginFields}
            value={form}
            onChange={setForm}
          />
          <Button type="submit" className="w-100" disabled={busy}>
            {busy ? 'Please wait...' : registering ? 'Register and sign in' : 'Sign in'}
          </Button>
        </Form>
        {registerFields.length > 0 && (
          <Button
            variant="link"
            className="w-100 mt-2"
            disabled={busy}
            onClick={() => {
              setRegistering((value) => !value);
              setError('');
            }}
          >
            {registering ? 'Already have an account? Sign in' : 'New customer? Create an account'}
          </Button>
        )}
      </Card.Body>
    </Card>
  );
}
