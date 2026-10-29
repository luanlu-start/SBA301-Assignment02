import { createContext, useContext, useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { Alert, Spinner } from 'react-bootstrap';
import api, { expiredEvent, sessionKey } from './LuanLHCE191099_api.js';
const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(() => Boolean(sessionStorage.getItem(sessionKey)));
  useEffect(() => {
    let active = true;
    const expire = () => setUser(null);
    window.addEventListener(expiredEvent, expire);
    if (sessionStorage.getItem(sessionKey)) {
      api
        .get('/auth/me')
        .then(({ data }) => {
          if (active) setUser(data);
        })
        .catch(() => {
          sessionStorage.removeItem(sessionKey);
        })
        .finally(() => {
          if (active) setLoading(false);
        });
    }
    return () => {
      active = false;
      window.removeEventListener(expiredEvent, expire);
    };
  }, []);
  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    sessionStorage.setItem(sessionKey, data.token);
    setUser(data.user);
    return data.user;
  }
  function logout() {
    sessionStorage.removeItem(sessionKey);
    setUser(null);
  }
  async function refresh() {
    const { data } = await api.get('/auth/me');
    setUser(data);
  }
  return (
    <AuthContext.Provider value={{ user, loading, login, logout, refresh }}>
      {children}
    </AuthContext.Provider>
  );
}
export function useAuth() {
  return useContext(AuthContext);
}
export function RequireRole({ roles }) {
  const { user, loading } = useAuth();
  if (loading)
    return (
      <Spinner animation="border" role="status">
        <span className="visually-hidden">Loading session</span>
      </Spinner>
    );
  if (!user) return <Navigate to="/login" replace />;
  if (!roles.includes(user.role))
    return <Alert variant="warning">You do not have permission to open this page.</Alert>;
  return <Outlet />;
}
