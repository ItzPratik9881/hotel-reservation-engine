import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  cancelReservation,
  getReservationById,
} from "../services/reservationService";
import { getPaymentByReservation } from "../services/paymentService";
import { getRoomById } from "../services/roomService";

import "../styles/ReservationDetails.css";

function ReservationDetails() {
  const navigate = useNavigate();
  const { reservationId } = useParams();

  const [reservation, setReservation] = useState(null);
  const [room, setRoom] = useState(null);
  const [payment, setPayment] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cancelling, setCancelling] = useState(false);

  const loadDetails = async () => {
    try {
      setLoading(true);
      setError("");

      const reservationResponse = await getReservationById(reservationId);
      const reservationData = reservationResponse.data;
      setReservation(reservationData);

      if (reservationData?.roomId) {
        try {
          const roomData = await getRoomById(reservationData.roomId);
          setRoom(roomData);
        } catch (roomError) {
          console.error("Failed to load room:", roomError);
        }
      }

      try {
        const paymentResponse = await getPaymentByReservation(reservationId);
        const paymentData = paymentResponse.data;

        if (Array.isArray(paymentData)) {
          setPayment(paymentData.length > 0 ? paymentData[0] : null);
        } else {
          setPayment(paymentData || null);
        }
      } catch (paymentError) {
        console.error("Failed to load payment:", paymentError);
      }
    } catch (error) {
      console.error("Failed to load reservation:", error);

      setError(
        error.response?.data?.message ||
          "Unable to load reservation details."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [reservationId]);

  const handleCancel = async () => {
    if (!window.confirm("Cancel this reservation?")) {
      return;
    }

    try {
      setCancelling(true);
      setError("");

      await cancelReservation(reservationId);

      setReservation((previous) => ({
        ...previous,
        bookingStatus: "CANCELLED",
      }));
    } catch (error) {
      console.error("Failed to cancel reservation:", error);

      setError(
        error.response?.data?.message ||
          "Unable to cancel this reservation."
      );
    } finally {
      setCancelling(false);
    }
  };

  if (loading) {
    return (
      <div className="reservation-details-page">
        <div className="reservation-details-loading">
          Loading reservation details...
        </div>
      </div>
    );
  }

  if (error && !reservation) {
    return (
      <div className="reservation-details-page">
        <div className="reservation-details-error">{error}</div>

        <button
          className="reservation-details-back-button"
          onClick={() => navigate("/reservations")}
        >
          ← Back to Reservations
        </button>
      </div>
    );
  }

  const status = String(reservation?.bookingStatus || "").toLowerCase();
  const canCancel = ["PENDING", "CONFIRMED"].includes(
    reservation?.bookingStatus
  );

  return (
    <div className="reservation-details-page">

      <header className="reservation-details-header">
        <div>
          <h1>Reservation #{reservation?.id}</h1>
          <p>Full details for this booking.</p>
        </div>

        <button
          className="reservation-details-back-button"
          onClick={() => navigate("/reservations")}
        >
          ← Back to Reservations
        </button>
      </header>

      <main className="reservation-details-content">

        {error && (
          <div className="reservation-details-error">{error}</div>
        )}

        <section className="reservation-details-card">

          <div className="reservation-details-card-top">
            <h2>Booking Information</h2>
            <span className={`reservation-status ${status}`}>
              {reservation?.bookingStatus}
            </span>
          </div>

          <div className="reservation-details-grid">

            <div>
              <span>Check-in</span>
              <strong>{reservation?.checkInDate}</strong>
            </div>

            <div>
              <span>Check-out</span>
              <strong>{reservation?.checkOutDate}</strong>
            </div>

            <div>
              <span>Guests</span>
              <strong>{reservation?.numberOfGuests}</strong>
            </div>

            <div>
              <span>Total Price</span>
              <strong>₹{reservation?.totalPrice}</strong>
            </div>

            <div>
              <span>Room</span>
              <strong>
                {room
                  ? `${room.roomNumber} · ${room.roomType}`
                  : `Room #${reservation?.roomId}`}
              </strong>
            </div>

            <div>
              <span>Room Capacity</span>
              <strong>{room ? `${room.capacity} guests` : "—"}</strong>
            </div>

          </div>

        </section>

        <section className="reservation-details-card">
          <h2>Payment</h2>

          {payment ? (
            <div className="reservation-details-grid">
              <div>
                <span>Amount</span>
                <strong>₹{payment.amount}</strong>
              </div>

              <div>
                <span>Method</span>
                <strong>{payment.paymentMethod}</strong>
              </div>

              <div>
                <span>Status</span>
                <strong>{payment.paymentStatus}</strong>
              </div>

              <div>
                <span>Transaction ID</span>
                <strong>{payment.transactionId || "—"}</strong>
              </div>
            </div>
          ) : (
            <p className="reservation-details-muted">
              No payment has been made for this reservation yet.
            </p>
          )}
        </section>

        {canCancel && (
          <div className="reservation-details-actions">
            <button
              className="reservation-details-cancel-button"
              onClick={handleCancel}
              disabled={cancelling}
            >
              {cancelling ? "Cancelling..." : "Cancel Reservation"}
            </button>
          </div>
        )}

      </main>

    </div>
  );
}

export default ReservationDetails;
