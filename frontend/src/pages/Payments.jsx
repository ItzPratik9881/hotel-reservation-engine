import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllPayments } from "../services/paymentService";
import "../styles/Payments.css";

function Payments() {
  const navigate = useNavigate();

  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllPayments();

      console.log("Payments API response:", response);

      setPayments(response.data || []);
    } catch (error) {
      console.error("Failed to load payments:", error);

      setError(
        error.response?.data?.message ||
          "Unable to load payment history."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPayments();
  }, []);

  return (
    <div className="payments-page">

      <header className="payments-header">

        <div>
          <h1>Payment History</h1>

          <p>
            View your completed and processed payments.
          </p>
        </div>

        <button
          className="payments-back-button"
          onClick={() => navigate("/dashboard")}
        >
          ← Dashboard
        </button>

      </header>

      <main className="payments-content">

        {loading && (
          <div className="payments-loading">
            Loading payment history...
          </div>
        )}

        {!loading && error && (
          <div className="payments-error">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          payments.length === 0 && (
            <div className="payments-empty">

              <div className="payments-empty-icon">
                💳
              </div>

              <h2>No Payments Yet</h2>

              <p>
                Your payment history will appear here
                after completing a reservation payment.
              </p>

              <button
                onClick={() => navigate("/hotels")}
              >
                Browse Hotels
              </button>

            </div>
          )}

        {!loading &&
          !error &&
          payments.length > 0 && (
            <section className="payments-grid">

              {payments.map((payment) => (

                <article
                  className="payment-card"
                  key={payment.id}
                >

                  <div className="payment-card-top">

                    <div className="payment-icon">
                      💳
                    </div>

                    <span
                      className={`payment-status ${
                        String(
                          payment.paymentStatus
                        ).toLowerCase()
                      }`}
                    >
                      {payment.paymentStatus}
                    </span>

                  </div>

                  <div className="payment-card-body">

                    <h2>
                      Payment #{payment.id}
                    </h2>

                    <p className="payment-reservation">
                      Reservation #
                      {payment.reservationId}
                    </p>

                    <div className="payment-amount">
                      ₹{payment.amount}
                    </div>

                    <div className="payment-details">

                      <div>
                        <span>Payment Method</span>
                        <strong>
                          {payment.paymentMethod}
                        </strong>
                      </div>

                      <div>
                        <span>Transaction ID</span>
                        <strong className="transaction-id">
                          {payment.transactionId}
                        </strong>
                      </div>

                      <div>
                        <span>Paid At</span>
                        <strong>
                          {payment.paidAt
                            ? new Date(
                                payment.paidAt
                              ).toLocaleString()
                            : "N/A"}
                        </strong>
                      </div>

                    </div>

                  </div>

                </article>

              ))}

            </section>
          )}

      </main>

    </div>
  );
}

export default Payments;