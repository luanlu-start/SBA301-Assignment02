import axios from 'axios';

export const expiredEvent = 'a2-login-expired';

// Pages use relative resource paths; Vite proxies /api to Spring Boot on port 8082.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000,
  withCredentials: true,
});

// Before a write, obtain Spring's CSRF value. The session cookie itself is managed by the browser.
api.interceptors.request.use(async (config) => {
  const method = (config.method || 'get').toLowerCase();
  const changesData = ['post', 'put', 'patch', 'delete'].includes(method);

  if (changesData) {
    const response = await api.get('/auth/csrf');
    config.headers[response.data.headerName] = response.data.token;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      window.dispatchEvent(new Event(expiredEvent));
    }
    return Promise.reject(error);
  },
);

// Convert the backend JSON error into text displayed by the calling page.
export function errorText(error) {
  const response = error.response;
  if (!response) {
    return 'Cannot connect to the API. Check that the backend is running.';
  }

  const fieldErrors = response.data?.errors;
  if (fieldErrors) {
    const messages = [];
    for (const fieldName of Object.keys(fieldErrors)) {
      messages.push(`${fieldName}: ${fieldErrors[fieldName]}`);
    }
    return messages.join(' / ');
  }
  if (response.data?.message) {
    return response.data.message;
  }
  return `Request failed (${response.status}).`;
}

export default api;
