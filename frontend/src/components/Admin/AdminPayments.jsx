import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getAllPayments } from "../../services/paymentService";

import "./AdminShared.css";

const STATUS_OPTIONS = ["ALL", "PENDING", "SUCCESS", "FAILED", "REFUNDED"];

function AdminPayments() {
  const navigate = useNavigate();

  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllPayments();
      setPayments(response.data || []);
    } catch (error) {
      console.error("Failed to load payments:", error);

      setError(
        error.response?.data?.message || "Unable to load payments."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPayments();
  }, []);

  const filteredPayments = useMemo(() => {
    if (statusFilter === "ALL") {
      return payments;
    }
    return payments.filter(
      (payment) => payment.paymentStatus === statusFilter
    );
  }, [payments, statusFilter]);

  const totalCollected = useMemo(() => {
    return payments
      .filter((payment) => payment.paymentStatus === "SUCCESS")
      .reduce((sum, payment) => sum + Number(payment.amount || 0), 0);
  }, [payments]);

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-page-loading">Loading payments...</div>
      </div>
    );
  }

  return (
    <div className="admin-page">

      <header className="admin-page-header">
        <div>
          <h1>Payment Management</h1>
          <p>
            View all payments · Total collected: ₹{totalCollected.toFixed(2)}
          </p>
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

        {filteredPayments.length === 0 ? (
          <div className="admin-page-empty">
            <h2>No Payments Found</h2>
            <p>There are no payments matching this filter.</p>
          </div>
        ) : (
          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Reservation</th>
                  <th>Amount</th>
                  <th>Method</th>
                  <th>Status</th>
                  <th>Transaction ID</th>
                  <th>Paid At</th>
                </tr>
              </thead>
              <tbody>
                {filteredPayments.map((payment) => {
                  const status = String(
                    payment.paymentStatus || ""
                  ).toLowerCase();

                  return (
                    <tr key={payment.id}>
                      <td>#{payment.id}</td>
                      <td>Reservation #{payment.reservationId}</td>
                      <td>₹{payment.amount}</td>
                      <td>{payment.paymentMethod}</td>
                      <td>
                        <span className={`status-badge ${status}`}>
                          {payment.paymentStatus}
                        </span>
                      </td>
                      <td>{payment.transactionId || "—"}</td>
                      <td>
                        {payment.paidAt
                          ? new Date(payment.paidAt).toLocaleString()
                          : "—"}
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

export default AdminPayments;
