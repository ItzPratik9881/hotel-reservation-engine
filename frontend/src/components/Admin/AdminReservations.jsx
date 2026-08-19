import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  getAllReservations,
  cancelReservation,
  checkInReservation,
  checkOutReservation,
} from "../../services/reservationService";

import "./AdminShared.css";

const STATUS_OPTIONS = [
  "ALL",
  "PENDING",
  "CONFIRMED",
  "CHECKED_IN",
  "CHECKED_OUT",
  "CANCELLED",
  "NO_SHOW",
];

function AdminReservations() {
  const navigate = useNavigate();

  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actioningId, setActioningId] = useState(null);
  const [statusFilter, setStatusFilter] = useState("ALL");

  const loadReservations = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllReservations();
      setReservations(response.data || []);
    } catch (error) {
      console.error("Failed to load reservations:", error);

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

  const runAction = async (action, reservationId, successStatus) => {
    try {
      setActioningId(reservationId);
      setError("");

      await action(reservationId);

      setReservations((previous) =>
        previous.map((reservation) =>
          reservation.id === reservationId
            ? { ...reservation, bookingStatus: successStatus }
            : reservation
        )
      );
    } catch (error) {
      console.error("Reservation action failed:", error);

      setError(
        error.response?.data?.message ||
          "Unable to update this reservation."
      );
    } finally {
      setActioningId(null);
    }
  };

  const handleCancel = (id) => {
    if (!window.confirm("Cancel this reservation?")) {
      return;
    }
    runAction(cancelReservation, id, "CANCELLED");
  };

  const handleCheckIn = (id) => runAction(checkInReservation, id, "CHECKED_IN");
  const handleCheckOut = (id) => runAction(checkOutReservation, id, "CHECKED_OUT");

  const filteredReservations = useMemo(() => {
    if (statusFilter === "ALL") {
      return reservations;
    }
    return reservations.filter(
      (reservation) => reservation.bookingStatus === statusFilter
    );
  }, [reservations, statusFilter]);

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-page-loading">Loading reservations...</div>
      </div>
    );
  }

  return (
    <div className="admin-page">

      <header className="admin-page-header">
        <div>
          <h1>Reservation Management</h1>
          <p>View and manage all reservations in the system.</p>
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

        {error && <div className="admin-page-error">{error}</div>}

        <div className="admin-filter-bar">
          <select
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
          >
            {STATUS_OPTIONS.map((status) => (
              <option key={status} value={status}>
                {status === "ALL" ? "All Statuses" : status}
              </option>
            ))}
          </select>
        </div>

        {filteredReservations.length === 0 ? (
          <div className="admin-page-empty">
            <h2>No Reservations Found</h2>
            <p>There are no reservations matching this filter.</p>
          </div>
        ) : (
          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User</th>
                  <th>Room</th>
                  <th>Check-in</th>
                  <th>Check-out</th>
                  <th>Guests</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredReservations.map((reservation) => {
                  const status = String(
                    reservation.bookingStatus || ""
                  ).toLowerCase();

                  const busy = actioningId === reservation.id;

                  return (
                    <tr key={reservation.id}>
                      <td>#{reservation.id}</td>
                      <td>User #{reservation.userId}</td>
                      <td>Room #{reservation.roomId}</td>
                      <td>{reservation.checkInDate}</td>
                      <td>{reservation.checkOutDate}</td>
                      <td>{reservation.numberOfGuests}</td>
                      <td>₹{reservation.totalPrice}</td>
                      <td>
                        <span className={`status-badge ${status}`}>
                          {reservation.bookingStatus}
                        </span>
                      </td>
                      <td>
                        <div className="admin-table-actions">
                          {reservation.bookingStatus === "CONFIRMED" && (
                            <button
                              className="admin-table-action-button checkin"
                              disabled={busy}
                              onClick={() => handleCheckIn(reservation.id)}
                            >
                              Check In
                            </button>
                          )}

                          {reservation.bookingStatus === "CHECKED_IN" && (
                            <button
                              className="admin-table-action-button checkout"
                              disabled={busy}
                              onClick={() => handleCheckOut(reservation.id)}
                            >
                              Check Out
                            </button>
                          )}

                          {["PENDING", "CONFIRMED"].includes(
                            reservation.bookingStatus
                          ) && (
                            <button
                              className="admin-table-action-button cancel"
                              disabled={busy}
                              onClick={() => handleCancel(reservation.id)}
                            >
                              Cancel
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

      </main>

    </div>
  );
}

export default AdminReservations;
