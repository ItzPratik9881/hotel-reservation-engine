import { useNavigate } from "react-router-dom";

import "./AdminShared.css";

/**
 * NOTE: There is currently no backend endpoint to list all users
 * (UserRepository/UserService have no "get all users" API exposed
 * via a controller). This page is a placeholder so the sidebar link
 * works and doesn't 404. Wire this up once a
 * GET /api/v1/users (admin-only) endpoint exists on the backend.
 */
function AdminUsers() {
  const navigate = useNavigate();

  return (
    <div className="admin-page">

      <header className="admin-page-header">
        <div>
          <h1>User Management</h1>
          <p>View and manage registered users.</p>
        </div>

        <div className="admin-page-header-actions">
          <button
            className="admin-back-button"
            onClick={() => navigate("/admin/dashboard")}
          >
            ← Admin Dashboard
          </button>
        </div>
      </header>

      <main className="admin-page-content">
        <div className="admin-page-empty">
          <h2>Coming Soon</h2>
          <p>
            User management requires a backend "list users" endpoint
            that doesn't exist yet. Add a GET /api/v1/users API
            (admin-only) to UserController/UserService, then this
            page can be wired up the same way Hotels and Rooms are.
          </p>
        </div>
      </main>

    </div>
  );
}

export default AdminUsers;
