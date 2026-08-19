import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getCurrentUser } from "../services/authService";
import { getUserReservations } from "../services/reservationService";

import {
  createPayment,
  getPaymentByReservation,
} from "../services/paymentService";

import "../styles/Reservations.css";

function Reservations() {
  const navigate = useNavigate();

  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [payingReservationId, setPayingReservationId] =
    useState(null);

  const [paymentMethod, setPaymentMethod] =
    useState("CARD");

  const [selectedReservation, setSelectedReservation] =
    useState(null);

  /*
   * Stores payment status for each reservation.
   *
   * Example:
   *
   * {
   *   13: true,
   *   14: false
   * }
   *
   * Reservation 13 has payment
   * Reservation 14 does not have payment
   */
  const [paymentStatus, setPaymentStatus] = useState({});

  /*
   * Check whether a payment exists for a reservation.
   */
  const checkPaymentExists = async (reservationId) => {
    try {
      const response =
        await getPaymentByReservation(reservationId);

      console.log(
        `Payment check for reservation ${reservationId}:`,
        response
      );

      const paymentData = response.data;

      /*
       * Backend can return:
       *
       * []
       *
       * when no payment exists.
       *
       * Or:
       *
       * [{ ...payment }]
       *
       * when payment exists.
       */

      if (Array.isArray(paymentData)) {
        return paymentData.length > 0;
      }

      /*
       * In case backend returns a single object.
       */
      return paymentData != null;
    } catch (error) {
      console.error(
        `Failed to check payment for reservation ${reservationId}:`,
        error
      );

      /*
       * If payment lookup fails, don't assume
       * that payment exists.
       */
      return false;
    }
  };

  /*
   * Load reservations and their payment status.
   */
  const loadReservations = async () => {
    try {
      setLoading(true);
      setError("");

      /*
       * Get currently logged-in user.
       */
      const userResponse = await getCurrentUser();

      const user = userResponse.data;

      /*
       * Get all reservations of the current user.
       */
      const reservationResponse =
        await getUserReservations(user.id);

      const reservationList =
        reservationResponse.data || [];

      setReservations(reservationList);

      /*
       * Check payment status for every reservation.
       */
      const paymentChecks = await Promise.all(
        reservationList.map(async (reservation) => {
          const hasPayment =
            await checkPaymentExists(reservation.id);

          return {
            reservationId: reservation.id,
            hasPayment,
          };
        })
      );

      /*
       * Convert result into an object:
       *
       * {
       *   13: true,
       *   14: false
       * }
       */
      const paymentMap = {};

      paymentChecks.forEach((item) => {
        paymentMap[item.reservationId] =
          item.hasPayment;
      });

      setPaymentStatus(paymentMap);

      console.log(
        "Payment status map:",
        paymentMap
      );
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

  useEffect(() => {
    loadReservations();
  }, []);

  /*
   * Back to dashboard.
   */
  const handleBack = () => {
    navigate("/dashboard");
  };

  /*
   * Open payment modal.
   */
  const handlePayNow = (reservation) => {
    setSelectedReservation(reservation);
    setPayingReservationId(reservation.id);
    setPaymentMethod("CARD");
  };

  /*
   * Close payment modal.
   */
  const handleCancelPayment = () => {
    setSelectedReservation(null);
    setPayingReservationId(null);
  };

  /*
   * Create payment.
   */
  const handlePayment = async () => {
    if (!selectedReservation) {
      return;
    }

    try {
      setLoading(true);
      setError("");

      const paymentData = {
        reservationId: selectedReservation.id,
        paymentMethod: paymentMethod,
      };

      console.log(
        "Creating payment:",
        paymentData
      );

      const response = await createPayment(
        paymentData
      );

      console.log(
        "Payment successful:",
        response
      );

      /*
       * Close payment modal.
       */
      setSelectedReservation(null);
      setPayingReservationId(null);

      /*
       * Reload reservations and payment statuses.
       */
      await loadReservations();
    } catch (error) {
      console.error(
        "Payment failed:",
        error
      );

      setError(
        error.response?.data?.message ||
          "Payment failed. Please try again."
      );

      setLoading(false);
    }
  };

  /*
   * Show payment details.
   */
  const handleViewPayment = async (reservationId) => {
    try {
      const response =
        await getPaymentByReservation(
          reservationId
        );

      console.log(
        "Payment API response:",
        response
      );

      const paymentData = response.data;

      const payment = Array.isArray(paymentData)
        ? paymentData[0]
        : paymentData;

      if (!payment) {
        setError(
          "No payment found for this reservation."
        );

        return;
      }

      alert(
        `Payment Status: ${payment.paymentStatus}\n` +
          `Amount: ₹${payment.amount}\n` +
          `Method: ${payment.paymentMethod}\n` +
          `Transaction ID: ${payment.transactionId}`
      );
    } catch (error) {
      console.error(
        "Failed to fetch payment:",
        error
      );

      setError(
        error.response?.data?.message ||
          "Unable to fetch payment details."
      );
    }
  };

  /*
   * Initial loading screen.
   */
  if (loading && reservations.length === 0) {
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

      {/* HEADER */}

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


      {/* CONTENT */}

      <main className="reservations-content">

        {/* ERROR */}

        {error && (
          <div className="reservations-error">
            {error}
          </div>
        )}


        {/* EMPTY */}

        {!error &&
          reservations.length === 0 && (
            <div className="reservations-empty">

              <h2>No Reservations</h2>

              <p>
                You haven't made any reservations yet.
              </p>

              <button
                onClick={() =>
                  navigate("/hotels")
                }
              >
                Browse Hotels
              </button>

            </div>
          )}


        {/* RESERVATIONS */}

        {reservations.length > 0 && (
          <section className="reservations-list">

            {reservations.map((reservation) => {

              const status = String(
                reservation.bookingStatus
              ).toUpperCase();

              /*
               * Check actual payment existence.
               */
              const hasPayment =
                paymentStatus[reservation.id] === true;

              return (
                <article
                  className="reservation-card"
                  key={reservation.id}
                >

                  {/* ICON */}

                  <div className="reservation-icon">
                    📅
                  </div>


                  {/* RESERVATION INFORMATION */}

                  <div className="reservation-info">

                    <h2
                      className="reservation-details-link"
                      onClick={() =>
                        navigate(`/reservations/${reservation.id}`)
                      }
                      style={{ cursor: "pointer" }}
                    >
                      Reservation #{reservation.id} →
                    </h2>

                    <p>
                      Room ID:{" "}
                      {reservation.roomId}
                    </p>

                    <p>
                      Check-in:{" "}
                      {reservation.checkInDate}
                    </p>

                    <p>
                      Check-out:{" "}
                      {reservation.checkOutDate}
                    </p>

                    <p>
                      Guests:{" "}
                      {reservation.numberOfGuests}
                    </p>

                  </div>


                  {/* RIGHT SIDE */}

                  <div className="reservation-right">

                    <strong>
                      ₹{reservation.totalPrice}
                    </strong>


                    {/* BOOKING STATUS */}

                    <span
                      className={`reservation-status ${status.toLowerCase()}`}
                    >
                      {status}
                    </span>


                    {/* 
                     * PAYMENT ACTION
                     *
                     * Payment exists
                     *       ↓
                     * View Payment
                     *
                     * Payment doesn't exist
                     *       ↓
                     * Pay Now
                     */}

                    {hasPayment ? (
                      <button
                        className="payment-details-button"
                        onClick={() =>
                          handleViewPayment(
                            reservation.id
                          )
                        }
                      >
                        View Payment
                      </button>
                    ) : (
                      <button
                        className="pay-now-button"
                        onClick={() =>
                          handlePayNow(
                            reservation
                          )
                        }
                      >
                        💳 Pay Now
                      </button>
                    )}

                  </div>

                </article>
              );
            })}

          </section>
        )}


        {/* PAYMENT MODAL */}

        {selectedReservation && (
          <div className="payment-overlay">

            <div className="payment-modal">

              <h2>
                Complete Payment
              </h2>

              <p>
                Reservation #
                {selectedReservation.id}
              </p>

              <div className="payment-amount">
                ₹{selectedReservation.totalPrice}
              </div>


              {/* PAYMENT METHOD */}

              <label>
                Payment Method
              </label>

              <select
                value={paymentMethod}
                onChange={(event) =>
                  setPaymentMethod(
                    event.target.value
                  )
                }
              >

                <option value="CARD">
                  Card
                </option>

                <option value="UPI">
                  UPI
                </option>

                <option value="CASH">
                  Cash
                </option>

              </select>


              {/* ACTIONS */}

              <div className="payment-actions">

                <button
                  className="payment-cancel-button"
                  onClick={
                    handleCancelPayment
                  }
                >
                  Cancel
                </button>

                <button
                  className="payment-confirm-button"
                  onClick={handlePayment}
                  disabled={loading}
                >
                  {loading
                    ? "Processing..."
                    : "Pay Now"}
                </button>

              </div>

            </div>

          </div>
        )}

      </main>

    </div>
  );
}

export default Reservations;