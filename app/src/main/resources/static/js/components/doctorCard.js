// doctorCard.js

// ===== IMPORTS =====

// Overlay for booking appointments
import { showBookingOverlay } from "../loggedPatient.js";

// API to delete doctor (admin role)
import { deleteDoctor } from "../services/doctorServices.js";

// Fetch patient details (used during booking)
import { getPatientData } from "../services/patientServices.js";


/**
 * Create and return a DOM element for a single doctor card
 */
export function createDoctorCard(doctor) {

  // ===== MAIN CARD CONTAINER =====
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // ===== CURRENT USER ROLE =====
  const role = localStorage.getItem("userRole");

  // ===== DOCTOR INFO CONTAINER =====
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  // Doctor Name
  const name = document.createElement("h3");
  name.textContent = doctor.name;

  // Specialization
  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialty}`;

  // Email
  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  // Available Times
  const availability = document.createElement("p");
  const times = doctor.availableTimes?.join(", ") || "Not available";
  availability.textContent = `Available: ${times}`;

  // Append info
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);


  // ===== ACTION BUTTON CONTAINER =====
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");


  // =====================================================
  // ===== ADMIN ROLE ACTIONS =====
  // =====================================================
  if (role === "admin") {

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";

    deleteBtn.addEventListener("click", async () => {

      const confirmDelete = confirm(
        `Are you sure you want to delete Dr. ${doctor.name}?`
      );
      if (!confirmDelete) return;

      try {
        // Get admin token
        const token = localStorage.getItem("token");

        // Call API
        await deleteDoctor(doctor.id, token);

        alert("Doctor deleted successfully");

        // Remove card from UI
        card.remove();

      } catch (error) {
        alert("Failed to delete doctor");
        console.error(error);
      }
    });

    actionsDiv.appendChild(deleteBtn);
  }


  // =====================================================
  // ===== PATIENT (NOT LOGGED-IN) ACTIONS =====
  // =====================================================
  else if (role === "patient") {

    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";

    bookBtn.addEventListener("click", () => {
      alert("Please login to book an appointment.");
    });

    actionsDiv.appendChild(bookBtn);
  }


  // =====================================================
  // ===== LOGGED-IN PATIENT ACTIONS =====
  // =====================================================
  else if (role === "loggedPatient") {

    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";

    bookBtn.addEventListener("click", async (event) => {

      const token = localStorage.getItem("token");

      // Redirect if token missing
      if (!token) {
        alert("Session expired. Please login again.");
        window.location.href = "/";
        return;
      }

      try {
        // Fetch patient data
        const patientData = await getPatientData(token);

        // Show booking overlay
        showBookingOverlay(event, doctor, patientData);

      } catch (error) {
        alert("Unable to proceed with booking.");
        console.error(error);
      }
    });

    actionsDiv.appendChild(bookBtn);
  }


  // ===== FINAL ASSEMBLY =====
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}