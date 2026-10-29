import { Alert } from 'react-bootstrap';
import CrudPage, { activeOptions, statusBadge } from './LuanLHCE191099_CrudPage.jsx';
import useApiList from './LuanLHCE191099_useApiList.js';
const columns = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  { key: 'parentName', label: 'Parent' },
  { key: 'status', label: 'Status', render: statusBadge },
];
export default function CategoryPage() {
  const categories = useApiList('/categories');
  const fields = [
    { name: 'name', label: 'Name', required: true, maxLength: 100 },
    { name: 'description', label: 'Description', type: 'textarea', maxLength: 500 },
    {
      name: 'parentId',
      label: 'Parent category',
      type: 'select',
      optional: true,
      numeric: true,
      options: categories.data.map((c) => ({ value: c.id, label: c.name })),
    },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      numeric: true,
      options: activeOptions,
    },
  ];
  return (
    <>
      {categories.error && <Alert variant="danger">{categories.error}</Alert>}
      <CrudPage
        title="Category management"
        endpoint="/categories"
        fields={fields}
        columns={columns}
        defaults={{ status: 1 }}
        onChanged={categories.reload}
        deleteHint="Categories used by news or by child categories cannot be deleted."
      />
    </>
  );
}
