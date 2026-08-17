import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createReservation } from "../services/reservationService";
import { getCurrentUser } from "../services/authService";
import "../styles/Booking.css";

function Booking() {
  const { roomId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    checkInDate: "",
    checkOutDate: "",
    numberOfGuests: 1,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]:
        name === "numberOfGuests"
          ? Number(value)
          : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");

    if (
      !formData.checkInDate ||
      !formData.checkOutDate
    ) {
      setError("Please select both check-in and check-out dates.");
      return;
    }

    if (
      new Date(formData.checkOutDate) <=
      new Date(formData.checkInDate)
    ) {
      setError("Check-out date must be after check-in date.");
      return;
    }

    if (formData.numberOfGuests < 1) {
      setError("Number of guests must be at least 1.");
      return;
    }

    try {
      setLoading(true);

      // Get currently logged-in user
      const userResponse = await getCurrentUser();

      const user = userResponse.data;

      const reservationData = {
        userId: user.id,
        roomId: Number(roomId),
        checkInDate: formData.checkInDate,
        checkOutDate: formData.checkOutDate,
        numberOfGuests: formData.numberOfGuests,
      };

      console.log(
        "Creating reservation:",
        reservationData
      );

      const response = await createReservation(
        reservationData
      );

      console.log(
        "Reservation created:",
        response
      );

      // Temporary success navigation.
      // We will create the reservation details page next.
      navigate("/reservations");
    } catch (error) {
      console.error(
        "Booking failed:",
        error
      );

      const message =
        error.response?.data?.message ||
        "Unable to create reservation. Please try again.";

      setError(message);
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <div className="booking-page">
      <div className="booking-container">

        <header className="booking-header">
          <div>
            <h1>Book Your Room</h1>
            <p>
              Complete the details below to reserve this room.
            </p>
          </div>

          <button
            className="back-button"
            onClick={handleBack}
          >
            ← Back
          </button>
        </header>

        {error && (
          <div className="booking-error">
            {error}
          </div>
        )}

        <form
          className="booking-card"
          onSubmit={handleSubmit}
        >

          <div className="booking-room-info">
            <div className="booking-room-icon">
              🛏️
            </div>

            <div>
              <h2>Room {roomId}</h2>
              <p>
                Complete your reservation details.
              </p>
            </div>
          </div>

          <div className="booking-form-grid">

            <div className="form-group">
              <label htmlFor="checkInDate">
                Check-in Date
              </label>

              <input
                id="checkInDate"
                name="checkInDate"
                type="date"
                value={formData.checkInDate}
                onChange={handleChange}
                min={
                  new Date()
                    .toISOString()
                    .split("T")[0]
                }
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="checkOutDate">
                Check-out Date
              </label>

              <input
                id="checkOutDate"
                name="checkOutDate"
                type="date"
                value={formData.checkOutDate}
                onChange={handleChange}
                min={
                  formData.checkInDate ||
                  new Date()
                    .toISOString()
                    .split("T")[0]
                }
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="numberOfGuests">
                Number of Guests
              </label>

              <input
                id="numberOfGuests"
                name="numberOfGuests"
                type="number"
                min="1"
                value={formData.numberOfGuests}
                onChange={handleChange}
                required
              />
            </div>

          </div>

          <div className="booking-summary">
            <h3>Reservation Summary</h3>

            <div className="summary-row">
              <span>Room ID</span>
              <strong>{roomId}</strong>
            </div>

            <div className="summary-row">
              <span>Check-in</span>
              <strong>
                {formData.checkInDate || "Not selected"}
              </strong>
            </div>

            <div className="summary-row">
              <span>Check-out</span>
              <strong>
                {formData.checkOutDate || "Not selected"}
              </strong>
            </div>

            <div className="summary-row">
              <span>Guests</span>
              <strong>
                {formData.numberOfGuests}
              </strong>
            </div>
          </div>

          <button
            type="submit"
            className="confirm-booking-button"
            disabled={loading}
          >
            {loading
              ? "Creating Reservation..."
              : "Confirm Booking"}
          </button>

        </form>
      </div>
    </div>
  );
}

export default Booking;