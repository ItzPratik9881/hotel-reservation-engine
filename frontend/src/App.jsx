import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Hotels from "./pages/Hotels";
import Rooms from "./pages/Rooms";
import Booking from "./pages/Booking";
import Reservations from "./pages/Reservations";
import ReservationDetails from "./pages/ReservationDetails";
import Payments from "./pages/Payments";

import AdminDashboard from "./pages/AdminDashboard";

import AdminHotels from "./components/Admin/AdminHotels";
import AddHotel from "./components/Admin/AddHotel";
import AdminRooms from "./components/Admin/AdminRooms";
import AddRoom from "./components/Admin/AddRoom";
import AdminReservations from "./components/Admin/AdminReservations";
import AdminPayments from "./components/Admin/AdminPayments";
import AdminUsers from "./components/Admin/AdminUsers";

import ProtectedRoute from "./routes/ProtectedRoute";
import AdminLayout from "./components/Admin/AdminLayout";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ================= PUBLIC ROUTES ================= */}

        <Route
          path="/"
          element={<Navigate to="/login" replace />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />


        {/* ================= CUSTOMER ROUTES ================= */}

        <Route
          element={
            <ProtectedRoute
              allowedRoles={["CUSTOMER"]}
            />
          }
        >

          <Route
            path="/dashboard"
            element={<Dashboard />}
          />

          <Route
            path="/hotels"
            element={<Hotels />}
          />

          <Route
            path="/hotels/:hotelId/rooms"
            element={<Rooms />}
          />

          <Route
            path="/rooms/:roomId/book"
            element={<Booking />}
          />

          <Route
            path="/reservations"
            element={<Reservations />}
          />

          <Route
            path="/reservations/:reservationId"
            element={<ReservationDetails />}
          />

          <Route
            path="/payments"
            element={<Payments />}
          />

        </Route>


        {/* ================= ADMIN ROUTES ================= */}

        <Route
          element={
            <ProtectedRoute
              allowedRoles={["ADMIN"]}
            />
          }
        >

          <Route
            path="/admin"
            element={<AdminLayout />}
          >

            {/* Admin Dashboard */}

            <Route
              path="dashboard"
              element={<AdminDashboard />}
            />

            {/* Admin Hotels */}

            <Route
              path="hotels"
              element={<AdminHotels />}
            />

            <Route
              path="hotels/add"
              element={<AddHotel />}
            />

            <Route
              path="hotels/edit/:hotelId"
              element={<AddHotel />}
            />

            {/* Admin Rooms */}

            <Route
              path="rooms"
              element={<AdminRooms />}
            />

            <Route
              path="rooms/add"
              element={<AddRoom />}
            />

            <Route
              path="rooms/edit/:roomId"
              element={<AddRoom />}
            />

            {/* Admin Reservations */}

            <Route
              path="reservations"
              element={<AdminReservations />}
            />

            {/* Admin Payments */}

            <Route
              path="payments"
              element={<AdminPayments />}
            />

            {/* Admin Users */}

            <Route
              path="users"
              element={<AdminUsers />}
            />

          </Route>

        </Route>


        {/* ================= UNKNOWN ROUTE ================= */}

        <Route
          path="*"
          element={<Navigate to="/login" replace />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
