import React from 'react';
import ReactDOM from 'react-dom/client';
import 'bootstrap/dist/css/bootstrap.min.css';
import App from './LuanLHCE191099_App.jsx';

// Frontend entry point: index.html provides the DOM element whose id is "root".
// React mounts App there; App sets up routing, authentication context and pages.
// StrictMode enables extra development checks; it does not create two production screens.
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
