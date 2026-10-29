import CrudPage from './LuanLHCE191099_CrudPage.jsx';
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
export default function AccountPage() {
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
