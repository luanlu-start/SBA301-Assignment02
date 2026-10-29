import { Form } from 'react-bootstrap';
export default function FormFields({ fields, value, onChange, editing = false }) {
  return fields.map((field) => (
    <Form.Group className="mb-3" key={field.name} controlId={`field-${field.name}`}>
      <Form.Label>
        {field.label}
        {field.required && !(editing && field.type === 'password') ? ' *' : ''}
      </Form.Label>
      {field.type === 'select' ? (
        <Form.Select
          value={value[field.name] ?? ''}
          required={field.required}
          onChange={(event) => onChange({ ...value, [field.name]: event.target.value })}
        >
          <option value="">{field.optional ? 'None' : 'Select...'}</option>
          {(field.options || []).map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </Form.Select>
      ) : (
        <Form.Control
          as={field.type === 'textarea' ? 'textarea' : 'input'}
          rows={field.type === 'textarea' ? 4 : undefined}
          type={field.type === 'textarea' ? undefined : field.type || 'text'}
          value={value[field.name] ?? ''}
          required={field.required && !(editing && field.type === 'password')}
          min={field.min}
          max={field.max}
          minLength={field.type === 'password' ? 6 : undefined}
          maxLength={field.maxLength}
          step={field.step}
          placeholder={field.placeholder}
          autoComplete={field.type === 'password' ? 'new-password' : undefined}
          onChange={(event) => onChange({ ...value, [field.name]: event.target.value })}
        />
      )}
      {field.help && <Form.Text muted>{field.help}</Form.Text>}
    </Form.Group>
  ));
}
