import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  createRoom,
  getRoomById,
  updateRoom,
} from "../../services/roomService";
import { getAllHotels } from "../../services/hotelService";

import "./AddHotel.css";

const ROOM_TYPES = ["STANDARD", "DELUXE", "SUITE", "EXECUTIVE", "FAMILY"];

function AddRoom() {
  const navigate = useNavigate();
  const { roomId } = useParams();
  const isEditMode = Boolean(roomId);

  const [hotels, setHotels] = useState([]);

  const [formData, setFormData] = useState({
    roomNumber: "",
    roomType: "STANDARD",
    capacity: 2,
    pricePerNight: "",
    hotelId: "",
    available: true,
  });

  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const loadData = async () => {
      try {
        setInitialLoading(true);
        setError("");

        const hotelsResponse = await getAllHotels();
        const hotelList = hotelsResponse.data || [];
        setHotels(hotelList);

        if (isEditMode) {
          const roomResponse = await getRoomById(roomId);

          setFormData({
            roomNumber: roomResponse.roomNumber || "",
            roomType: roomResponse.roomType || "STANDARD",
            capacity: roomResponse.capacity || 1,
            pricePerNight: roomResponse.pricePerNight || "",
            hotelId: String(roomResponse.hotelId || ""),
            available: roomResponse.available ?? true,
          });
        } else if (hotelList.length > 0) {
          setFormData((previous) => ({
            ...previous,
            hotelId: String(hotelList[0].id),
          }));
        }
      } catch (error) {
        console.error("Failed to load room form data:", error);

        setError(
          error.response?.data?.message ||
            "Unable to load required data."
        );
      } finally {
        setInitialLoading(false);
      }
    };

    loadData();
  }, [roomId, isEditMode]);

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

    if (!formData.roomNumber.trim()) {
      setError("Room number is required.");
      return;
    }

    if (!formData.hotelId) {
      setError("Please select a hotel.");
      return;
    }

    if (!formData.capacity || Number(formData.capacity) < 1) {
      setError("Capacity must be at least 1.");
      return;
    }

    if (!formData.pricePerNight || Number(formData.pricePerNight) <= 0) {
      setError("Price per night must be greater than 0.");
      return;
    }

    try {
      setLoading(true);

      if (isEditMode) {
        const roomData = {
          roomNumber: formData.roomNumber.trim(),
          roomType: formData.roomType,
          capacity: Number(formData.capacity),
          pricePerNight: Number(formData.pricePerNight),
          available: Boolean(formData.available),
        };

        await updateRoom(roomId, roomData);
        setSuccess("Room updated successfully!");
      } else {
        const roomData = {
          roomNumber: formData.roomNumber.trim(),
          roomType: formData.roomType,
          capacity: Number(formData.capacity),
          pricePerNight: Number(formData.pricePerNight),
          hotelId: Number(formData.hotelId),
        };

        await createRoom(roomData);
        setSuccess("Room created successfully!");
      }

      setTimeout(() => {
        navigate("/admin/rooms");
      }, 1000);

    } catch (error) {
      console.error("Failed to save room:", error);

      setError(
        error.response?.data?.message ||
          `Unable to ${isEditMode ? "update" : "create"} room.`
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/admin/rooms");
  };

  if (initialLoading) {
    return (
      <div className="add-hotel-page">
        <div className="admin-page-loading">Loading...</div>
      </div>
    );
  }

  return (
    <div className="add-hotel-page">

      <header className="add-hotel-header">

        <div>
          <h1>{isEditMode ? "Edit Room" : "Add New Room"}</h1>
          <p>
            {isEditMode
              ? "Update this room's details."
              : "Add a new room to a hotel."}
          </p>
        </div>

        <button className="add-hotel-back-button" onClick={handleCancel}>
          ← Room Management
        </button>

      </header>

      <main className="add-hotel-content">

        <div className="add-hotel-card">

          {error && <div className="add-hotel-error">{error}</div>}
          {success && <div className="add-hotel-success">{success}</div>}

          <form onSubmit={handleSubmit}>

            <div className="form-section">

              <h2>Room Information</h2>

              <div className="form-row">

                <div className="form-group">
                  <label>Hotel</label>

                  <select
                    name="hotelId"
                    value={formData.hotelId}
                    onChange={handleChange}
                    disabled={loading || isEditMode}
                  >
                    <option value="">Select a hotel</option>
                    {hotels.map((hotel) => (
                      <option key={hotel.id} value={hotel.id}>
                        {hotel.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Room Number</label>

                  <input
                    type="text"
                    name="roomNumber"
                    value={formData.roomNumber}
                    onChange={handleChange}
                    placeholder="e.g. 101"
                    disabled={loading}
                  />
                </div>

              </div>

              <div className="form-row">

                <div className="form-group">
                  <label>Room Type</label>

                  <select
                    name="roomType"
                    value={formData.roomType}
                    onChange={handleChange}
                    disabled={loading}
                  >
                    {ROOM_TYPES.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Capacity (guests)</label>

                  <input
                    type="number"
                    name="capacity"
                    min="1"
                    value={formData.capacity}
                    onChange={handleChange}
                    disabled={loading}
                  />
                </div>

              </div>

              <div className="form-row">

                <div className="form-group">
                  <label>Price per Night (₹)</label>

                  <input
                    type="number"
                    name="pricePerNight"
                    min="0"
                    step="0.01"
                    value={formData.pricePerNight}
                    onChange={handleChange}
                    placeholder="e.g. 2500"
                    disabled={loading}
                  />
                </div>

                {isEditMode && (
                  <div className="form-group form-group-checkbox">
                    <label>
                      <input
                        type="checkbox"
                        name="available"
                        checked={formData.available}
                        onChange={handleChange}
                        disabled={loading}
                      />{" "}
                      Room is available
                    </label>
                  </div>
                )}

              </div>

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
                  : (isEditMode ? "💾 Save Changes" : "🛏️ Create Room")}
              </button>

            </div>

          </form>

        </div>

      </main>

    </div>
  );
}

export default AddRoom;
