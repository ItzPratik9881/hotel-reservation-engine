import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getAdminDashboard,
  getAdminRevenue,
  getBookingAnalytics,
} from "../services/adminService";
import "../styles/AdminDashboard.css";

function AdminDashboard() {
  const navigate = useNavigate();

  const [dashboard, setDashboard] = useState(null);
  const [revenue, setRevenue] = useState(null);
  const [bookingAnalytics, setBookingAnalytics] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        dashboardResponse,
        revenueResponse,
        bookingResponse,
      ] = await Promise.all([
        getAdminDashboard(),
        getAdminRevenue(),
        getBookingAnalytics(),
      ]);

      console.log(
        "Dashboard response:",
        dashboardResponse
      );

      console.log(
        "Revenue response:",
        revenueResponse
      );

      console.log(
        "Booking analytics response:",
        bookingResponse
      );

      setDashboard(dashboardResponse.data);
      setRevenue(revenueResponse.data);
      setBookingAnalytics(bookingResponse.data);

    } catch (error) {
      console.error(
        "Failed to load admin dashboard:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Unable to load admin dashboard."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");
    localStorage.removeItem("user");

    navigate("/login");
  };

  if (loading) {
    return (
      <div className="admin-dashboard">
        <div className="admin-loading">
          Loading admin dashboard...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="admin-dashboard">
        <div className="admin-error">
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">

      {/* ================= HEADER ================= */}

      <header className="admin-header">

        <div className="admin-brand">
          <h1>Admin Dashboard</h1>

          <p>
            Hotel Reservation Management
          </p>
        </div>

        <div className="admin-header-actions">

          <span className="admin-badge">
            ADMIN
          </span>

          <button
            className="admin-logout-button"
            onClick={handleLogout}
          >
            Logout
          </button>

        </div>

      </header>


      {/* ================= CONTENT ================= */}

      <main className="admin-content">

        <div className="admin-welcome">
          <h2>
            Welcome, Administrator 👋
          </h2>

          <p>
            Here's an overview of your hotel
            reservation system.
          </p>
        </div>


        {/* ================= MAIN STATISTICS ================= */}

        <section className="admin-stats-grid">

          <div className="admin-stat-card">

            <div className="admin-stat-icon">
              🏨
            </div>

            <div>
              <span>
                Total Hotels
              </span>

              <strong>
                {dashboard?.totalHotels ?? 0}
              </strong>
            </div>

          </div>


          <div className="admin-stat-card">

            <div className="admin-stat-icon">
              🛏️
            </div>

            <div>
              <span>
                Total Rooms
              </span>

              <strong>
                {dashboard?.totalRooms ?? 0}
              </strong>
            </div>

          </div>


          <div className="admin-stat-card">

            <div className="admin-stat-icon">
              👥
            </div>

            <div>
              <span>
                Total Users
              </span>

              <strong>
                {dashboard?.totalUsers ?? 0}
              </strong>
            </div>

          </div>


          <div className="admin-stat-card">

            <div className="admin-stat-icon">
              📅
            </div>

            <div>
              <span>
                Reservations
              </span>

              <strong>
                {dashboard?.totalReservations ?? 0}
              </strong>
            </div>

          </div>

        </section>


        {/* ================= REVENUE ================= */}

        <section className="admin-section">

          <div className="admin-section-header">
            <div>
              <h2>
                Revenue Overview
              </h2>

              <p>
                Current revenue performance
              </p>
            </div>
          </div>


          <div className="revenue-grid">

            <div className="revenue-card">

              <span>
                Today's Revenue
              </span>

              <strong>
                ₹{revenue?.todayRevenue ?? 0}
              </strong>

            </div>


            <div className="revenue-card">

              <span>
                Monthly Revenue
              </span>

              <strong>
                ₹{revenue?.monthlyRevenue ?? 0}
              </strong>

            </div>


            <div className="revenue-card">

              <span>
                Yearly Revenue
              </span>

              <strong>
                ₹{revenue?.yearlyRevenue ?? 0}
              </strong>

            </div>

          </div>

        </section>


        {/* ================= BOOKING ANALYTICS ================= */}

        <section className="admin-section">

          <div className="admin-section-header">

            <div>
              <h2>
                Booking Analytics
              </h2>

              <p>
                Reservation status overview
              </p>
            </div>

          </div>


          <div className="booking-grid">

            <div className="booking-card">
              <span>Total</span>
              <strong>
                {bookingAnalytics?.totalReservations ?? 0}
              </strong>
            </div>

            <div className="booking-card confirmed">
              <span>Confirmed</span>
              <strong>
                {bookingAnalytics?.confirmedReservations ?? 0}
              </strong>
            </div>

            <div className="booking-card cancelled">
              <span>Cancelled</span>
              <strong>
                {bookingAnalytics?.cancelledReservations ?? 0}
              </strong>
            </div>

            <div className="booking-card">
              <span>Checked In</span>
              <strong>
                {bookingAnalytics?.checkedInReservations ?? 0}
              </strong>
            </div>

            <div className="booking-card">
              <span>Checked Out</span>
              <strong>
                {bookingAnalytics?.checkedOutReservations ?? 0}
              </strong>
            </div>

          </div>

        </section>


        {/* ================= ROOM STATUS ================= */}

        <section className="admin-section">

          <div className="admin-section-header">

            <div>
              <h2>
                Room Status
              </h2>

              <p>
                Current room availability
              </p>
            </div>

          </div>


          <div className="room-status-grid">

            <div className="room-status-card">

              <span>
                Available Rooms
              </span>

              <strong>
                {dashboard?.availableRooms ?? 0}
              </strong>

            </div>


            <div className="room-status-card occupied">

              <span>
                Occupied Rooms
              </span>

              <strong>
                {dashboard?.occupiedRooms ?? 0}
              </strong>

            </div>


            <div className="room-status-card">

              <span>
                Occupancy Rate
              </span>

              <strong>
                {dashboard?.occupancyRate ?? 0}%
              </strong>

            </div>

          </div>

        </section>


        {/* ================= QUICK ACTIONS ================= */}

        <section className="admin-section">

          <div className="admin-section-header">

            <div>
              <h2>
                Quick Actions
              </h2>

              <p>
                Manage your reservation system
              </p>
            </div>

          </div>


          <div className="quick-actions">

            <button
              onClick={() => navigate("/admin/hotels")}
            >
              🏨
              <span>
                Manage Hotels
              </span>
            </button>

            <button
              onClick={() => navigate("/admin/rooms")}
            >
              🛏️
              <span>
                Manage Rooms
              </span>
            </button>

            <button
              onClick={() => navigate("/admin/reservations")}
            >
              📅
              <span>
                View Reservations
              </span>
            </button>

            <button
              onClick={() => navigate("/admin/payments")}
            >
              💳
              <span>
                View Payments
              </span>
            </button>

          </div>

        </section>

      </main>

    </div>
  );
}

export default AdminDashboard;