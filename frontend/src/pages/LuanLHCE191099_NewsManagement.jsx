import { Alert } from 'react-bootstrap';
import CrudPage, {
  activeOptions,
  statusBadge,
} from '../components/Common/LuanLHCE191099_CrudPage.jsx';
import useApiList from '../hooks/LuanLHCE191099_useApiList.js';

// These columns are reused by News management and the read-only personal history page.
export const newsColumns = [
  { key: 'id', label: 'ID' },
  { key: 'title', label: 'Title' },
  { key: 'categoryName', label: 'Category' },
  { key: 'createdByName', label: 'Author' },
  { key: 'status', label: 'Status', render: statusBadge },
  { key: 'tags', label: 'Tags', render: (value) => value.map((tag) => tag.name).join(', ') || '—' },
];
// Load categories so the editor can send categoryId while displaying a readable category name.
export default function NewsManagement() {
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
      options: categories.data.map((category) => ({ value: category.id, label: category.name })),
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
  // Convert one "name | note" line into one tag DTO. Empty lines are ignored.
  function prepareRequest(values) {
    const tags = [];
    const lines = values.tags.split('\n');
    for (const rawLine of lines) {
      const line = rawLine.trim();
      if (line === '') {
        continue;
      }

      const separator = line.indexOf('|');
      let name = line;
      let note = '';
      if (separator !== -1) {
        name = line.slice(0, separator).trim();
        note = line.slice(separator + 1).trim();
      }
      tags.push({ name, note });
    }

    return { ...values, tags };
  }

  // Edit does the reverse conversion: saved tag DTOs become textarea lines.
  function mapRecordToForm(values, record) {
    const lines = [];
    for (const tag of record.tags) {
      let line = tag.name;
      if (tag.note) {
        line += ' | ' + tag.note;
      }
      lines.push(line);
    }
    return { ...values, tags: lines.join('\n') };
  }

  return (
    <>
      {categories.error && <Alert variant="danger">{categories.error}</Alert>}
      <CrudPage
        title="News management"
        endpoint="/news"
        fields={fields}
        columns={newsColumns}
        defaults={{ status: 1, tags: '' }}
        prepareRequest={prepareRequest}
        mapRecordToForm={mapRecordToForm}
      />
    </>
  );
}
