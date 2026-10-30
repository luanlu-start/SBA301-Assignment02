import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Alert, Button, Card, Form } from 'react-bootstrap';
import { errorText } from '../services/LuanLHCE191099_api.js';
import { useAuth } from '../context/LuanLHCE191099_AuthContext.jsx';
import FormFields from '../components/Common/LuanLHCE191099_FormFields.jsx';

const loginFields = [
  { name: 'email', label: 'Email', type: 'email', required: true, maxLength: 150 },
  { name: 'password', label: 'Password', type: 'password', required: true, maxLength: 100 },
];

// This page handles only A2 login. Admin creates accounts from Account management.
export default function LoginPage({ destinations }) {
  const auth = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleLogin(event) {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    try {
      const user = await auth.login(formData.email, formData.password);
      const destination = destinations[user.role] || '/';
      navigate(destination, { replace: true });
    } catch (error) {
      setError(errorText(error));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Card className="login-shell">
      <Card.Body className="p-4">
        <h1 className="h3">Sign in</h1>
        <p className="text-secondary">Enter your email and password.</p>
        {error && <Alert variant="danger">{error}</Alert>}

        <Form onSubmit={handleLogin}>
          <FormFields fields={loginFields} value={formData} onChange={setFormData} />
          <Button type="submit" className="w-100" disabled={isSubmitting}>
            {isSubmitting ? 'Please wait...' : 'Sign in'}
          </Button>
        </Form>
      </Card.Body>
    </Card>
  );
}
