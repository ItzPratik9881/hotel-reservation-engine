import { NavLink, useNavigate } from "react-router-dom";
import "./AdminSidebar.css";

function AdminSidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");

    navigate("/login");
  };

  return (
    <aside className="admin-sidebar">

      <div className="admin-sidebar-header">
        <div className="admin-logo">
          🏨
        </div>

        <div>
          <h2>Hotel Admin</h2>
          <p>Management Panel</p>
        </div>
      </div>

      <nav className="admin-navigation">

        <NavLink
          to="/admin/dashboard"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>🏠</span>
          <span>Dashboard</span>
        </NavLink>

        <NavLink
          to="/admin/hotels"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>🏨</span>
          <span>Hotels</span>
        </NavLink>

        <NavLink
          to="/admin/rooms"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>🛏️</span>
          <span>Rooms</span>
        </NavLink>

        <NavLink
          to="/admin/reservations"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>📅</span>
          <span>Reservations</span>
        </NavLink>

        <NavLink
          to="/admin/payments"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>💳</span>
          <span>Payments</span>
        </NavLink>

        <NavLink
          to="/admin/users"
          className={({ isActive }) =>
            `admin-nav-item ${isActive ? "active" : ""}`
          }
        >
          <span>👥</span>
          <span>Users</span>
        </NavLink>

      </nav>

      <div className="admin-sidebar-footer">

        <div className="admin-user">
          <div className="admin-user-avatar">
            A
          </div>

          <div className="admin-user-info">
            <strong>Administrator</strong>
            <span>ADMIN</span>
          </div>
        </div>

        <button
          className="admin-logout-button"
          onClick={handleLogout}
        >
          <span>🚪</span>
          <span>Logout</span>
        </button>

      </div>

    </aside>
  );
}

export default AdminSidebar;