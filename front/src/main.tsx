import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import "./components/Modal/ModalWindow.css";
import   './styles/buttons.css'
import './index.css'
import { BrowserRouter } from 'react-router-dom'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    
    <BrowserRouter>
      <App />
    </BrowserRouter>

  </React.StrictMode>,
)
