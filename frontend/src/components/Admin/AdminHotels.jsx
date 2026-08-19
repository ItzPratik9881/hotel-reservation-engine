import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllHotels } from "../../services/hotelService";
import "./AdminHotels.css";

function AdminHotels() {
  const navigate = useNavigate();

  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadHotels = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllHotels();

      console.log("Hotels API response:", response);

      setHotels(response.data || []);
    } catch (error) {
      console.error("Failed to load hotels:", error);

      setError(
        error.response?.data?.message ||
          "Unable to load hotels."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHotels();
  }, []);

  const handleBack = () => {
    navigate("/admin");
  };

  const handleAddHotel = () => {
    navigate("/admin/hotels/add");
  };

  const handleEditHotel = (hotelId) => {
    navigate(`/admin/hotels/edit/${hotelId}`);
  };

  if (loading) {
    return (
      <div className="admin-hotels-page">
        <div className="admin-hotels-loading">
          Loading hotels...
        </div>
      </div>
    );
  }

  return (
    <div className="admin-hotels-page">

      <header className="admin-hotels-header">

        <div>
          <h1>Hotel Management</h1>

          <p>
            Manage hotels available in the system.
          </p>
        </div>

        <div className="admin-hotels-header-actions">

          <button
            className="admin-hotels-back-button"
            onClick={handleBack}
          >
            ← Admin Dashboard
          </button>

          <button
            className="admin-add-hotel-button"
            onClick={handleAddHotel}
          >
            + Add Hotel
          </button>

        </div>

      </header>

      <main className="admin-hotels-content">

        {error && (
          <div className="admin-hotels-error">
            {error}
          </div>
        )}

        {!error && hotels.length === 0 && (
          <div className="admin-hotels-empty">

            <h2>No Hotels Found</h2>

            <p>
              There are currently no hotels in the system.
            </p>

            <button onClick={handleAddHotel}>
              Add First Hotel
            </button>

          </div>
        )}

        {hotels.length > 0 && (
          <section className="admin-hotels-grid">

            {hotels.map((hotel) => {

              const active =
                Boolean(hotel.active);

              return (
                <article
                  className="admin-hotel-card"
                  key={hotel.id}
                >

                  <div className="admin-hotel-card-top">

                    <div className="admin-hotel-icon">
                      🏨
                    </div>

                    <span
                      className={`admin-hotel-status ${
                        active
                          ? "active"
                          : "inactive"
                      }`}
                    >
                      {active
                        ? "ACTIVE"
                        : "INACTIVE"}
                    </span>

                  </div>

                  <div className="admin-hotel-info">

                    <h2>{hotel.name}</h2>

                    <p className="admin-hotel-location">
                      📍 {hotel.city}, {hotel.state}
                    </p>

                    <p>
                      <strong>
                        ⭐ {hotel.starRating}
                      </strong>{" "}
                      Star Hotel
                    </p>

                    <p className="admin-hotel-description">
                      {hotel.description}
                    </p>

                    <p className="admin-hotel-address">
                      {hotel.address}
                    </p>

                  </div>

                  <div className="admin-hotel-card-footer">

                    <span>
                      Hotel ID: #{hotel.id}
                    </span>

                    <button
                      className="admin-edit-hotel-button"
                      onClick={() =>
                        handleEditHotel(hotel.id)
                      }
                    >
                      ✏️ Edit
                    </button>

                  </div>

                </article>
              );
            })}

          </section>
        )}

      </main>

    </div>
  );
}

export default AdminHotels;