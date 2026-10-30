import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { Alert, Button, Container, Nav, Navbar } from 'react-bootstrap';
import { useAuth } from '../../context/LuanLHCE191099_AuthContext.jsx';
import { errorText } from '../../services/LuanLHCE191099_api.js';

// Shared navigation and logout behavior, corresponding to Header in Assignment 1.
export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [logoutError, setLogoutError] = useState('');
  const [isSigningOut, setIsSigningOut] = useState(false);

  async function handleLogout() {
    setLogoutError('');
    setIsSigningOut(true);
    try {
      await logout();
      navigate('/');
    } catch (error) {
      setLogoutError(errorText(error));
    } finally {
      setIsSigningOut(false);
    }
  }

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
              <Nav.Link as={NavLink} to="/news" end>
                Published news
              </Nav.Link>
              {/* Show account management only for Admin. The API still checks permissions independently. */}
              {user?.role === 'ADMIN' && (
                <Nav.Link as={NavLink} to="/manage/accounts">
                  Accounts
                </Nav.Link>
              )}
              {/* Staff can manage news and categories and access their own history/profile. */}
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
                  onClick={handleLogout}
                  disabled={isSigningOut}
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
      {logoutError && (
        <Container className="pt-3">
          <Alert variant="danger">{logoutError}</Alert>
        </Container>
      )}
    </>
  );
}
