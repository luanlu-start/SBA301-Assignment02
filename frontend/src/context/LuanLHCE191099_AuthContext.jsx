import { createContext, useContext, useEffect, useState } from 'react';
import api, { expiredEvent } from '../services/LuanLHCE191099_api.js';

const AuthContext = createContext(null);

// Share login information with the navigation, route guards and pages.
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    function handleExpiredLogin() {
      setUser(null);
    }

    // After F5, the browser sends its session cookie and the backend returns the current user.
    async function restoreSession() {
      try {
        const response = await api.get('/auth/me');
        if (active) {
          setUser(response.data);
        }
      } catch {
        if (active) {
          setUser(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    window.addEventListener(expiredEvent, handleExpiredLogin);
    restoreSession();

    return () => {
      active = false;
      window.removeEventListener(expiredEvent, handleExpiredLogin);
    };
  }, []);

  async function login(email, password) {
    const response = await api.post('/auth/login', { email, password });
    const loginResult = response.data;

    // The response sets an HttpOnly cookie; state updates the current screen.
    setUser(loginResult.user);
    return loginResult.user;
  }

  // End the server session before clearing the displayed user.
  async function logout() {
    await api.post('/auth/logout');
    setUser(null);
  }

  async function refresh() {
    const response = await api.get('/auth/me');
    setUser(response.data);
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
