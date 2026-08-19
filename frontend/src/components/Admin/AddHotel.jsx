import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  createHotel,
  getHotelById,
  updateHotel,
} from "../../services/hotelService";
import "./AddHotel.css";

function AddHotel() {
  const navigate = useNavigate();
  const { hotelId } = useParams();
  const isEditMode = Boolean(hotelId);

  const [formData, setFormData] = useState({
    name: "",
    address: "",
    city: "",
    state: "",
    country: "India",
    starRating: 5,
    description: "",
    active: true,
  });

  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(isEditMode);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!isEditMode) {
      return;
    }

    const loadHotel = async () => {
      try {
        setInitialLoading(true);
        setError("");

        const response = await getHotelById(hotelId);
        const hotel = response.data;

        setFormData({
          name: hotel.name || "",
          address: hotel.address || "",
          city: hotel.city || "",
          state: hotel.state || "",
          country: hotel.country || "India",
          starRating: hotel.starRating || 5,
          description: hotel.description || "",
          active: hotel.active ?? true,
        });
      } catch (error) {
        console.error("Failed to load hotel:", error);

        setError(
          error.response?.data?.message ||
            "Unable to load hotel details."
        );
      } finally {
        setInitialLoading(false);
      }
    };

    loadHotel();
  }, [hotelId, isEditMode]);

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!formData.name.trim()) {
      setError("Hotel name is required.");
      return;
    }

    if (!formData.address.trim()) {
      setError("Address is required.");
      return;
    }

    if (!formData.city.trim()) {
      setError("City is required.");
      return;
    }

    if (!formData.state.trim()) {
      setError("State is required.");
      return;
    }

    if (!formData.country.trim()) {
      setError("Country is required.");
      return;
    }

    if (!formData.description.trim()) {
      setError("Description is required.");
      return;
    }

    try {
      setLoading(true);

      if (isEditMode) {
        const hotelData = {
          name: formData.name.trim(),
          address: formData.address.trim(),
          city: formData.city.trim(),
          state: formData.state.trim(),
          country: formData.country.trim(),
          starRating: Number(formData.starRating),
          description: formData.description.trim(),
          active: Boolean(formData.active),
        };

        console.log("Updating hotel:", hotelId, hotelData);

        const response = await updateHotel(hotelId, hotelData);

        console.log("Hotel updated:", response);

        setSuccess("Hotel updated successfully!");
      } else {
        const hotelData = {
          name: formData.name.trim(),
          address: formData.address.trim(),
          city: formData.city.trim(),
          state: formData.state.trim(),
          country: formData.country.trim(),
          starRating: Number(formData.starRating),
          description: formData.description.trim(),
        };

        console.log("Creating hotel:", hotelData);

        const response = await createHotel(hotelData);

        console.log("Hotel created:", response);

        setSuccess("Hotel created successfully!");
      }

      setTimeout(() => {
        navigate("/admin/hotels");
      }, 1000);

    } catch (error) {
      console.error("Failed to save hotel:", error);

      setError(
        error.response?.data?.message ||
          `Unable to ${isEditMode ? "update" : "create"} hotel.`
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/admin/hotels");
  };

  if (initialLoading) {
    return (
      <div className="add-hotel-page">
        <div className="admin-page-loading">
          Loading hotel details...
        </div>
      </div>
    );
  }

  return (
    <div className="add-hotel-page">

      <header className="add-hotel-header">

        <div>
          <h1>{isEditMode ? "Edit Hotel" : "Add New Hotel"}</h1>

          <p>
            {isEditMode
              ? "Update this hotel's details."
              : "Add a new hotel to the reservation system."}
          </p>
        </div>

        <button
          className="add-hotel-back-button"
          onClick={handleCancel}
        >
          ← Hotel Management
        </button>

      </header>

      <main className="add-hotel-content">

        <div className="add-hotel-card">

          {error && (
            <div className="add-hotel-error">
              {error}
            </div>
          )}

          {success && (
            <div className="add-hotel-success">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="form-section">

              <h2>Hotel Information</h2>

              <div className="form-group">

                <label>
                  Hotel Name
                </label>

                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter hotel name"
                  disabled={loading}
                />

              </div>

              <div className="form-group">

                <label>
                  Address
                </label>

                <input
                  type="text"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Enter complete address"
                  disabled={loading}
                />

              </div>

              <div className="form-row">

                <div className="form-group">

                  <label>
                    City
                  </label>

                  <input
                    type="text"
                    name="city"
                    value={formData.city}
                    onChange={handleChange}
                    placeholder="Enter city"
                    disabled={loading}
                  />

                </div>

                <div className="form-group">

                  <label>
                    State
                  </label>

                  <input
                    type="text"
                    name="state"
                    value={formData.state}
                    onChange={handleChange}
                    placeholder="Enter state"
                    disabled={loading}
                  />

                </div>

              </div>

              <div className="form-row">

                <div className="form-group">

                  <label>
                    Country
                  </label>

                  <input
                    type="text"
                    name="country"
                    value={formData.country}
                    onChange={handleChange}
                    placeholder="Enter country"
                    disabled={loading}
                  />

                </div>

                <div className="form-group">

                  <label>
                    Star Rating
                  </label>

                  <select
                    name="starRating"
                    value={formData.starRating}
                    onChange={handleChange}
                    disabled={loading}
                  >
                    <option value={1}>
                      1 Star
                    </option>

                    <option value={2}>
                      2 Stars
                    </option>

                    <option value={3}>
                      3 Stars
                    </option>

                    <option value={4}>
                      4 Stars
                    </option>

                    <option value={5}>
                      5 Stars
                    </option>
                  </select>

                </div>

              </div>

              <div className="form-group">

                <label>
                  Description
                </label>

                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Describe the hotel..."
                  rows="5"
                  disabled={loading}
                />

              </div>

              {isEditMode && (
                <div className="form-group form-group-checkbox">

                  <label>
                    <input
                      type="checkbox"
                      name="active"
                      checked={formData.active}
                      onChange={handleChange}
                      disabled={loading}
                    />{" "}
                    Hotel is active
                  </label>

                </div>
              )}

            </div>

            <div className="form-actions">

              <button
                type="button"
                className="cancel-hotel-button"
                onClick={handleCancel}
                disabled={loading}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="create-hotel-button"
                disabled={loading}
              >
                {loading
                  ? (isEditMode ? "Saving..." : "Creating...")
                  : (isEditMode ? "💾 Save Changes" : "🏨 Create Hotel")}
              </button>

            </div>

          </form>

        </div>

      </main>

    </div>
  );
}

export default AddHotel;
