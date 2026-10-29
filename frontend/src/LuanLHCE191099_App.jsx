import { BrowserRouter, Link, NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import { Button, Container, Nav, Navbar } from 'react-bootstrap';
import { AuthProvider, RequireRole, useAuth } from './LuanLHCE191099_Auth.jsx';
import LoginPage from './LuanLHCE191099_LoginPage.jsx';
import PublicNews from './LuanLHCE191099_PublicNews.jsx';
import AccountPage from './LuanLHCE191099_AccountPage.jsx';
import CategoryPage from './LuanLHCE191099_CategoryPage.jsx';
import NewsPage, { newsColumns } from './LuanLHCE191099_NewsPage.jsx';
import CrudPage from './LuanLHCE191099_CrudPage.jsx';
import ProfilePage from './LuanLHCE191099_ProfilePage.jsx';
import './LuanLHCE191099_styles.css';
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
function Shell() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  return (
    <>
      <Navbar bg="dark" variant="dark" expand="lg" className="site-nav">
        <Container>
          <Navbar.Brand as={Link} to="/" className="site-brand">
            FU / NEWS
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="site-menu" />
          <Navbar.Collapse id="site-menu">
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/" end>
                Published news
              </Nav.Link>
              {user?.role === 'ADMIN' && (
                <Nav.Link as={NavLink} to="/manage/accounts">
                  Accounts
                </Nav.Link>
              )}
              {user?.role === 'STAFF' && (
                <>
                  <Nav.Link as={NavLink} to="/manage/categories">
                    Categories
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/manage/news">
                    News
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/history">
                    My news history
                  </Nav.Link>
                  <Nav.Link as={NavLink} to="/profile">
                    My profile
                  </Nav.Link>
                </>
              )}
            </Nav>
            {user ? (
              <div className="d-flex gap-3 align-items-center text-white">
                <small>{user.name}</small>
                <Button
                  size="sm"
                  variant="outline-light"
                  onClick={() => {
                    logout();
                    navigate('/');
                  }}
                >
                  Sign out
                </Button>
              </div>
            ) : (
              <Button as={Link} to="/login" variant="outline-light" size="sm">
                Sign in
              </Button>
            )}
          </Navbar.Collapse>
        </Container>
      </Navbar>
      <Container className="py-4">
        <Routes>
          <Route path="/" element={<PublicNews />} />
          <Route
            path="/login"
            element={
              <LoginPage destinations={{ ADMIN: '/manage/accounts', STAFF: '/manage/news' }} />
            }
          />
          <Route element={<RequireRole roles={['ADMIN']} />}>
            <Route path="/manage/accounts" element={<AccountPage />} />
          </Route>
          <Route element={<RequireRole roles={['STAFF']} />}>
            <Route path="/manage/categories" element={<CategoryPage />} />
            <Route path="/manage/news" element={<NewsPage />} />
            <Route
              path="/history"
              element={
                <CrudPage
                  title="My news history"
                  endpoint="/news/history"
                  columns={newsColumns}
                  readOnly
                />
              }
            />
            <Route
              path="/profile"
              element={<ProfilePage endpoint="/profile" fields={profileFields} />}
            />
          </Route>
          <Route
            path="*"
            element={
              <p>
                Page not found. <Link to="/">Return to news</Link>
              </p>
            }
          />
        </Routes>
      </Container>
      <footer className="container py-4 text-secondary small">
        FUNewsManagementSystem · Assignment 02 · LuanLH / CE191099
      </footer>
    </>
  );
}
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Shell />
      </AuthProvider>
    </BrowserRouter>
  );
}
