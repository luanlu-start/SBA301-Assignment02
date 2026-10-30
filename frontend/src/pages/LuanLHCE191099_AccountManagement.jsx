import CrudPage from '../components/Common/LuanLHCE191099_CrudPage.jsx';

// Account-specific form metadata used by CrudPage and FormFields.
// role is converted to a number before sending: Admin=1 and Staff=2. Password is never a table column.
const fields = [
  { name: 'name', label: 'Name', required: true, maxLength: 100 },
  { name: 'email', label: 'Email', type: 'email', required: true, maxLength: 150 },
  {
    name: 'password',
    label: 'Password',
    type: 'password',
    required: true,
    maxLength: 100,
    help: 'Leave blank when editing to keep the current password.',
  },
  {
    name: 'role',
    label: 'Role',
    type: 'select',
    numeric: true,
    required: true,
    options: [
      { value: 1, label: 'Admin' },
      { value: 2, label: 'Staff' },
    ],
  },
];
const columns = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Name' },
  { key: 'email', label: 'Email' },
  { key: 'role', label: 'Role', render: (value) => (value === 1 ? 'Admin' : 'Staff') },
];
// Compose the shared CRUD component for /api/accounts; new accounts default to Staff.
// Backend AccountController permits only ADMIN, and AccountService enforces deletion restrictions.
export default function AccountManagement() {
  return (
    <CrudPage
      title="Account management"
      endpoint="/accounts"
      fields={fields}
      columns={columns}
      defaults={{ role: 2 }}
      deleteHint="An account that has created news cannot be deleted."
    />
  );
}
