import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllHotels } from "../services/hotelService";
import "../styles/Hotels.css";

function Hotels() {
  const navigate = useNavigate();

  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadHotels = async () => {
      try {
        const response = await getAllHotels();
        setHotels(response.data || []);
      } catch (error) {
        console.error("Failed to load hotels:", error);
        setError("Unable to load hotels. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    loadHotels();
  }, []);

  const handleViewRooms = (hotelId) => {
    navigate(`/hotels/${hotelId}/rooms`);
  };

  const handleBack = () => {
    navigate("/dashboard");
  };

  if (loading) {
    return (
      <div className="hotels-page">
        <div className="hotels-loading">
          Loading hotels...
        </div>
      </div>
    );
  }

  return (
    <div className="hotels-page">
      <header className="hotels-header">
        <div>
          <h1>Available Hotels</h1>
          <p>Explore hotels and find the perfect stay.</p>
        </div>

        <button
          className="back-button"
          onClick={handleBack}
        >
          ← Dashboard
        </button>
      </header>

      <main className="hotels-content">
        {error && (
          <div className="hotels-error">
            {error}
          </div>
        )}

        {!error && hotels.length === 0 && (
          <div className="hotels-empty">
            <h2>No Hotels Available</h2>
            <p>
              There are currently no active hotels available for booking.
            </p>
          </div>
        )}

        {!error && hotels.length > 0 && (
          <section className="hotel-grid">
            {hotels.map((hotel) => (
              <article
                className="hotel-card"
                key={hotel.id}
              >
                <div className="hotel-card-top">
                  <div className="hotel-icon">
                    🏨
                  </div>

                  <div className="hotel-rating">
                    ⭐ {hotel.starRating}
                  </div>
                </div>

                <h2>{hotel.name}</h2>

                <p className="hotel-location">
                  📍 {hotel.city}, {hotel.state}
                </p>

                <p className="hotel-description">
                  {hotel.description ||
                    "Enjoy a comfortable and memorable stay with us."}
                </p>

                <p className="hotel-address">
                  {hotel.address}, {hotel.country}
                </p>

                <div className="hotel-status">
                  <span
                    className={
                      hotel.active
                        ? "status-active"
                        : "status-inactive"
                    }
                  >
                    ●
                  </span>

                  {hotel.active ? " Available" : " Currently unavailable"}
                </div>

                <button
                  className="view-rooms-button"
                  disabled={!hotel.active}
                  onClick={() => handleViewRooms(hotel.id)}
                >
                  View Rooms
                </button>
              </article>
            ))}
          </section>
        )}
      </main>
    </div>
  );
}

export default Hotels;