import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';

// Equivalente a layout.html (sidebar + topbar + slot de contenido).
export function AppLayout() {
  return (
    <div className="d-flex">
      <Sidebar />
      <div className="flex-grow-1 p-3">
        <Topbar />
        <div id="contenido">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
