import { Form } from 'react-bootstrap';

// Build controlled inputs from the field list supplied by a page.
export default function FormFields({ fields, value, onChange, editing = false }) {
  function updateField(fieldName, newValue) {
    const updatedValues = { ...value };
    updatedValues[fieldName] = newValue;
    onChange(updatedValues);
  }

  function isRequired(field) {
    const optionalPassword = editing && field.type === 'password';
    return Boolean(field.required && !optionalPassword);
  }

  function renderInput(field) {
    if (field.type === 'select') {
      return (
        <Form.Select
          value={value[field.name] ?? ''}
          required={field.required}
          onChange={(event) => updateField(field.name, event.target.value)}
        >
          <option value="">{field.optional ? 'None' : 'Select...'}</option>
          {(field.options || []).map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </Form.Select>
      );
    }

    const isTextarea = field.type === 'textarea';
    return (
      <Form.Control
        as={isTextarea ? 'textarea' : 'input'}
        rows={isTextarea ? 4 : undefined}
        type={isTextarea ? undefined : field.type || 'text'}
        value={value[field.name] ?? ''}
        required={isRequired(field)}
        min={field.min}
        max={field.max}
        minLength={field.type === 'password' ? 6 : undefined}
        maxLength={field.maxLength}
        step={field.step}
        placeholder={field.placeholder}
        autoComplete={field.type === 'password' ? 'new-password' : undefined}
        onChange={(event) => updateField(field.name, event.target.value)}
      />
    );
  }

  return fields.map((field) => (
    <Form.Group className="mb-3" key={field.name} controlId={`field-${field.name}`}>
      <Form.Label>
        {field.label}
        {isRequired(field) ? ' *' : ''}
      </Form.Label>
      {renderInput(field)}
      {field.help && <Form.Text muted>{field.help}</Form.Text>}
    </Form.Group>
  ));
}
