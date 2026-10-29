import { Alert } from 'react-bootstrap';
import CrudPage, { activeOptions, statusBadge } from './LuanLHCE191099_CrudPage.jsx';
import useApiList from './LuanLHCE191099_useApiList.js';
export const newsColumns = [
  { key: 'id', label: 'ID' },
  { key: 'title', label: 'Title' },
  { key: 'categoryName', label: 'Category' },
  { key: 'createdByName', label: 'Author' },
  { key: 'status', label: 'Status', render: statusBadge },
  { key: 'tags', label: 'Tags', render: (value) => value.map((tag) => tag.name).join(', ') || '—' },
];
export default function NewsPage() {
  const categories = useApiList('/categories');
  const fields = [
    { name: 'title', label: 'Title', required: true, maxLength: 200 },
    { name: 'headline', label: 'Headline', maxLength: 500 },
    {
      name: 'categoryId',
      label: 'Category',
      type: 'select',
      required: true,
      numeric: true,
      options: categories.data.map((c) => ({ value: c.id, label: c.name })),
    },
    { name: 'content', label: 'Content', type: 'textarea', required: true, maxLength: 10000 },
    { name: 'source', label: 'Source', maxLength: 255 },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      numeric: true,
      options: activeOptions,
    },
    {
      name: 'tags',
      label: 'Tags',
      type: 'textarea',
      placeholder: 'campus | University life',
      help: 'One tag per line: name | note. The note is optional.',
    },
  ];
  const prepare = (value) => ({
    ...value,
    tags: value.tags
      .split('\n')
      .map((line) => line.trim())
      .filter(Boolean)
      .map((line) => {
        const [name, ...note] = line.split('|');
        return { name: name.trim(), note: note.join('|').trim() };
      }),
  });
  const toForm = (value, row) => ({
    ...value,
    tags: row.tags.map((tag) => `${tag.name}${tag.note ? ' | ' + tag.note : ''}`).join('\n'),
  });
  return (
    <>
      {categories.error && <Alert variant="danger">{categories.error}</Alert>}
      <CrudPage
        title="News management"
        endpoint="/news"
        fields={fields}
        columns={newsColumns}
        defaults={{ status: 1, tags: '' }}
        prepare={prepare}
        toForm={toForm}
      />
    </>
  );
}
