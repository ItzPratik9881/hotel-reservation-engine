import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { testAuthentication } from "../services/authService";
import "../styles/Dashboard.css";

function Dashboard() {
  const navigate = useNavigate();

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifyAuthentication = async () => {
      try {
        const response = await testAuthentication();

        setMessage(response.data);
      } catch (error) {
        console.error("Authentication failed:", error);

        localStorage.removeItem("token");
        localStorage.removeItem("tokenType");

        navigate("/login");
      } finally {
        setLoading(false);
      }
    };

    verifyAuthentication();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");

    navigate("/login");
  };

  const handleViewHotels = () => {
    navigate("/hotels");
  };

  const handleViewRooms = () => {
    navigate("/rooms");
  };

  const handleViewReservations = () => {
    navigate("/reservations");
  };

  const handleViewPayments = () => {
    navigate("/payments");
  };

  if (loading) {
    return <div className="dashboard-loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <h1>Hotel Reservation</h1>
          <p>Welcome to your dashboard</p>
        </div>

        <button
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </header>

      <main className="dashboard-content">
        <section className="welcome-card">
          <h2>Welcome 👋</h2>
          <p>You are successfully authenticated.</p>

          <div className="auth-status">
            <span>●</span> {message}
          </div>
        </section>

        <section className="dashboard-grid">
          {/* Hotels */}
          <div className="dashboard-card">
            <div className="card-icon">🏨</div>

            <h3>Hotels</h3>

            <p>
              Browse available hotels and explore their details.
            </p>

            <button onClick={handleViewHotels}>
              View Hotels
            </button>
          </div>

          {/* Rooms */}
          <div className="dashboard-card">
            <div className="card-icon">🛏️</div>

            <h3>Rooms</h3>

            <p>
              Find available rooms for your next stay.
            </p>

            <button onClick={handleViewRooms}>
              View Rooms
            </button>
          </div>

          {/* Reservations */}
          <div className="dashboard-card">
            <div className="card-icon">📅</div>

            <h3>My Reservations</h3>

            <p>
              View and manage your hotel reservations.
            </p>

            <button onClick={handleViewReservations}>
              My Reservations
            </button>
          </div>

          {/* Payments */}
          <div className="dashboard-card">
            <div className="card-icon">💳</div>

            <h3>Payments</h3>

            <p>
              View your payment and booking history.
            </p>

            <button onClick={handleViewPayments}>
              View Payments
            </button>
          </div>
        </section>
      </main>
    </div>
  );
}

export default Dashboard;