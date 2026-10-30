import CrudPage from '../components/Common/LuanLHCE191099_CrudPage.jsx';
import { newsColumns } from './LuanLHCE191099_NewsManagement.jsx';

// Staff history reuses the news table, with write actions disabled.
export default function HistoryPage() {
  return (
    <CrudPage title="My news history" endpoint="/news/history" columns={newsColumns} readOnly />
  );
}
