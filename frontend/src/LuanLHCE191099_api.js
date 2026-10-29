import axios from 'axios';
export const sessionKey = 'a2-login';
export const expiredEvent = 'a2-login-expired';
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api', timeout: 15000 });
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(sessionKey);
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && sessionStorage.getItem(sessionKey)) {
      sessionStorage.removeItem(sessionKey);
      window.dispatchEvent(new Event(expiredEvent));
    }
    return Promise.reject(error);
  },
);
export function errorText(error) {
  if (error.response?.data?.errors)
    return Object.entries(error.response.data.errors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(' / ');
  return (
    error.response?.data?.message ||
    (error.response
      ? `Request failed (${error.response.status}).`
      : 'Cannot connect to the API. Check that the backend is running.')
  );
}
export const money = (value) =>
  new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(value || 0);
export default api;
