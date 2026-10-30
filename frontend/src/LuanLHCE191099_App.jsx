import { Spinner } from 'react-bootstrap';
import { BrowserRouter, Link, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/LuanLHCE191099_AuthContext.jsx';
import ProtectedRoute from './components/LuanLHCE191099_ProtectedRoute.jsx';
import AppLayout from './components/Layout/LuanLHCE191099_AppLayout.jsx';
import AccountManagement from './pages/LuanLHCE191099_AccountManagement.jsx';
import CategoryManagement from './pages/LuanLHCE191099_CategoryManagement.jsx';
import NewsManagement from './pages/LuanLHCE191099_NewsManagement.jsx';
import LoginPage from './pages/LuanLHCE191099_LoginPage.jsx';
import PublicNewsPage from './pages/LuanLHCE191099_PublicNewsPage.jsx';
import ProfilePage from './pages/LuanLHCE191099_ProfilePage.jsx';
import HistoryPage from './pages/LuanLHCE191099_HistoryPage.jsx';
import './LuanLHCE191099_styles.css';

// The entry route opens login or the management screen for the restored role.
function HomeRedirect() {
  const { user, loading } = useAuth();
  if (loading) {
    return <Spinner animation="border" role="status" />;
  }
  if (user?.role === 'ADMIN') {
    return <Navigate to="/manage/accounts" replace />;
  }
  if (user?.role === 'STAFF') {
    return <Navigate to="/manage/news" replace />;
  }
  return <Navigate to="/login" replace />;
}

// Like Assignment 1, App owns routes and Layout owns the common frame.
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<AppLayout />}>
            <Route path="/" element={<HomeRedirect />} />
            <Route path="/news" element={<PublicNewsPage />} />
            <Route
              path="/login"
              element={
                <LoginPage destinations={{ ADMIN: '/manage/accounts', STAFF: '/manage/news' }} />
              }
            />
            <Route element={<ProtectedRoute roles={['ADMIN']} />}>
              <Route path="/manage/accounts" element={<AccountManagement />} />
            </Route>
            <Route element={<ProtectedRoute roles={['STAFF']} />}>
              <Route path="/manage/categories" element={<CategoryManagement />} />
              <Route path="/manage/news" element={<NewsManagement />} />
              <Route path="/history" element={<HistoryPage />} />
              <Route path="/profile" element={<ProfilePage />} />
            </Route>
            <Route
              path="*"
              element={
                <p>
                  Page not found. <Link to="/">Return home</Link>
                </p>
              }
            />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
