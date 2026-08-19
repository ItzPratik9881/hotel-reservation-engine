import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getAllRooms, deleteRoom } from "../../services/roomService";
import { getAllHotels } from "../../services/hotelService";

import "./AdminShared.css";

function AdminRooms() {
  const navigate = useNavigate();

  const [rooms, setRooms] = useState([]);
  const [hotelMap, setHotelMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deletingId, setDeletingId] = useState(null);

  const [hotelFilter, setHotelFilter] = useState("ALL");

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [roomsData, hotelsResponse] = await Promise.all([
        getAllRooms(),
        getAllHotels(),
      ]);

      const hotels = hotelsResponse.data || [];

      const map = {};
      hotels.forEach((hotel) => {
        map[hotel.id] = hotel.name;
      });

      setHotelMap(map);
      setRooms(roomsData || []);
    } catch (error) {
      console.error("Failed to load rooms:", error);

      setError(
        error.response?.data?.message || "Unable to load rooms."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleDelete = async (roomId) => {
    if (!window.confirm("Delete this room? This cannot be undone.")) {
      return;
    }

    try {
      setDeletingId(roomId);
      await deleteRoom(roomId);
      setRooms((previous) => previous.filter((room) => room.id !== roomId));
    } catch (error) {
      console.error("Failed to delete room:", error);

      setError(
        error.response?.data?.message || "Unable to delete room."
      );
    } finally {
      setDeletingId(null);
    }
  };

  const filteredRooms =
    hotelFilter === "ALL"
      ? rooms
      : rooms.filter((room) => String(room.hotelId) === hotelFilter);

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-page-loading">Loading rooms...</div>
      </div>
    );
  }

  return (
    <div className="admin-page">

      <header className="admin-page-header">

        <div>
          <h1>Room Management</h1>
          <p>Manage rooms across all hotels.</p>
        </div>

        <div className="admin-page-header-actions">

          <button
            className="admin-back-button"
            onClick={() => navigate("/admin/dashboard")}
          >
            ← Admin Dashboard
          </button>

          <button
            className="admin-primary-button"
            onClick={() => navigate("/admin/rooms/add")}
          >
            + Add Room
          </button>

        </div>

      </header>

      <main className="admin-page-content">

        {error && (
          <div className="admin-page-error">{error}</div>
        )}

        <div className="admin-filter-bar">
          <select
            value={hotelFilter}
            onChange={(event) => setHotelFilter(event.target.value)}
          >
            <option value="ALL">All Hotels</option>
            {Object.entries(hotelMap).map(([id, name]) => (
              <option key={id} value={id}>
                {name}
              </option>
            ))}
          </select>
        </div>

        {filteredRooms.length === 0 ? (
          <div className="admin-page-empty">
            <h2>No Rooms Found</h2>
            <p>There are currently no rooms matching this filter.</p>
          </div>
        ) : (
          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Room #</th>
                  <th>Hotel</th>
                  <th>Type</th>
                  <th>Capacity</th>
                  <th>Price / Night</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRooms.map((room) => (
                  <tr key={room.id}>
                    <td>{room.roomNumber}</td>
                    <td>{hotelMap[room.hotelId] || `Hotel #${room.hotelId}`}</td>
                    <td>{room.roomType}</td>
                    <td>{room.capacity}</td>
                    <td>₹{room.pricePerNight}</td>
                    <td>
                      <span
                        className={`status-badge ${
                          room.available ? "available" : "unavailable"
                        }`}
                      >
                        {room.available ? "Available" : "Unavailable"}
                      </span>
                    </td>
                    <td>
                      <div className="admin-table-actions">
                        <button
                          className="admin-table-action-button edit"
                          onClick={() => navigate(`/admin/rooms/edit/${room.id}`)}
                        >
                          ✏️ Edit
                        </button>
                        <button
                          className="admin-table-action-button delete"
                          disabled={deletingId === room.id}
                          onClick={() => handleDelete(room.id)}
                        >
                          {deletingId === room.id ? "Deleting..." : "🗑️ Delete"}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

      </main>

    </div>
  );
}

export default AdminRooms;
