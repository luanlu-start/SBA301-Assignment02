import { Navigate, Outlet } from 'react-router-dom';
import { Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../context/LuanLHCE191099_AuthContext.jsx';

// This guard controls navigation. The backend independently authorizes every API request.
export default function ProtectedRoute({ roles }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <Spinner animation="border" role="status">
        <span className="visually-hidden">Loading session</span>
      </Spinner>
    );
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (!roles.includes(user.role)) {
    return <Alert variant="warning">You do not have permission to open this page.</Alert>;
  }
  return <Outlet />;
}
