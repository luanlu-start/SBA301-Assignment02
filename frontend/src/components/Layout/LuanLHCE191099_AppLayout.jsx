import { Container } from 'react-bootstrap';
import { Outlet } from 'react-router-dom';
import Header from './LuanLHCE191099_Header.jsx';

// Keep the shared frame mounted while Outlet displays the matching page.
export default function AppLayout() {
  return (
    <>
      <Header />
      <Container className="py-4">
        <Outlet />
      </Container>
    </>
  );
}
