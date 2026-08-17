import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCurrentUser } from "../services/authService";
import { getUserReservations } from "../services/reservationService";
import "../styles/Reservations.css";

function Reservations() {
  const navigate = useNavigate();

  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadReservations = async () => {
      try {
        const userResponse = await getCurrentUser();

        const user = userResponse.data;

        const reservationResponse =
          await getUserReservations(user.id);

        setReservations(reservationResponse.data);
      } catch (error) {
        console.error(
          "Failed to load reservations:",
          error
        );

        setError(
          error.response?.data?.message ||
          "Unable to load reservations."
        );
      } finally {
        setLoading(false);
      }
    };

    loadReservations();
  }, []);

  const handleBack = () => {
    navigate("/dashboard");
  };

  if (loading) {
    return (
      <div className="reservations-page">
        <div className="reservations-loading">
          Loading reservations...
        </div>
      </div>
    );
  }

  return (
    <div className="reservations-page">
      <header className="reservations-header">
        <div>
          <h1>My Reservations</h1>
          <p>
            View and manage your hotel bookings.
          </p>
        </div>

        <button
          className="back-button"
          onClick={handleBack}
        >
          ← Dashboard
        </button>
      </header>

      <main className="reservations-content">

        {error && (
          <div className="reservations-error">
            {error}
          </div>
        )}

        {!error && reservations.length === 0 && (
          <div className="reservations-empty">
            <h2>No Reservations</h2>
            <p>
              You haven't made any reservations yet.
            </p>

            <button
              onClick={() => navigate("/hotels")}
            >
              Browse Hotels
            </button>
          </div>
        )}

        {!error && reservations.length > 0 && (
          <section className="reservations-list">
            {reservations.map((reservation) => (
              <article
                className="reservation-card"
                key={reservation.id}
              >
                <div className="reservation-icon">
                  📅
                </div>

                <div className="reservation-info">
                  <h2>
                    Reservation #{reservation.id}
                  </h2>

                  <p>
                    Room ID: {reservation.roomId}
                  </p>

                  <p>
                    Check-in: {reservation.checkInDate}
                  </p>

                  <p>
                    Check-out: {reservation.checkOutDate}
                  </p>

                  <p>
                    Guests: {reservation.numberOfGuests}
                  </p>
                </div>

                <div className="reservation-right">
                  <strong>
                    ₹{reservation.totalPrice}
                  </strong>

                  <span
                    className={`reservation-status ${String(
                      reservation.bookingStatus
                    ).toLowerCase()}`}
                  >
                    {reservation.bookingStatus}
                  </span>
                </div>
              </article>
            ))}
          </section>
        )}

      </main>
    </div>
  );
}

export default Reservations;