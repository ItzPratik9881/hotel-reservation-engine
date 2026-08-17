import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getRoomsByHotel } from "../services/roomService";
import "../styles/Rooms.css";

function Rooms() {
  const { hotelId } = useParams();
  const navigate = useNavigate();

  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadRooms = async () => {
      try {
        const data = await getRoomsByHotel(hotelId);

        setRooms(data);
      } catch (error) {
        console.error("Failed to load rooms:", error);
        setError("Unable to load rooms. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    loadRooms();
  }, [hotelId]);

  const handleBack = () => {
    navigate("/hotels");
  };

  const handleBookNow = (roomId) => {
    navigate(`/rooms/${roomId}/book`);
  };

  if (loading) {
    return (
      <div className="rooms-page">
        <div className="rooms-loading">
          Loading rooms...
        </div>
      </div>
    );
  }

  return (
    <div className="rooms-page">
      <header className="rooms-header">
        <div>
          <h1>Available Rooms</h1>
          <p>Choose a room that suits your stay.</p>
        </div>

        <button
          className="back-button"
          onClick={handleBack}
        >
          ← Hotels
        </button>
      </header>

      <main className="rooms-content">
        {error && (
          <div className="rooms-error">
            {error}
          </div>
        )}

        {!error && rooms.length === 0 && (
          <div className="rooms-empty">
            <h2>No Rooms Available</h2>
            <p>
              There are currently no rooms available for this hotel.
            </p>
          </div>
        )}

        {!error && rooms.length > 0 && (
          <section className="rooms-grid">
            {rooms.map((room) => (
              <article
                className="room-card"
                key={room.id}
              >
                <div className="room-card-header">
                  <div className="room-icon">
                    🛏️
                  </div>

                  <span
                    className={
                      room.available
                        ? "room-status active"
                        : "room-status inactive"
                    }
                  >
                    {room.available
                      ? "Available"
                      : "Unavailable"}
                  </span>
                </div>

                <h2>
                  Room {room.roomNumber}
                </h2>

                <p className="room-type">
                  {room.roomType}
                </p>

                <div className="room-details">
                  <div>
                    <span>👥</span>
                    Capacity: {room.capacity} guests
                  </div>

                  <div>
                    <span>💰</span>
                    <strong>
                      ₹{room.pricePerNight}
                    </strong>
                    <small> / night</small>
                  </div>
                </div>

                <button
                  className="book-room-button"
                  disabled={!room.available}
                  onClick={() => handleBookNow(room.id)}
                >
                  {room.available
                    ? "Book Now"
                    : "Unavailable"}
                </button>
              </article>
            ))}
          </section>
        )}
      </main>
    </div>
  );
}

export default Rooms;