// ======================================================
// 🩺 DOCTOR DASHBOARD — APPOINTMENT MANAGEMENT
// ======================================================

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";


// ======================================================
// 📌 GLOBAL VARIABLES
// ======================================================

// Table body where rows will be rendered
const tableBody = document.getElementById("patientTableBody");

// Today's date in YYYY-MM-DD format
let selectedDate = new Date().toISOString().split("T")[0];

// Auth token
const token = localStorage.getItem("token");

// Patient name filter
let patientName = null;


// ======================================================
// 🔎 SEARCH BAR FUNCTIONALITY
// ======================================================
const searchBar = document.getElementById("searchBar");

searchBar?.addEventListener("input", () => {
  const value = searchBar.value.trim();

  patientName = value ? value : "null";

  loadAppointments();
});


// ======================================================
// 📅 TODAY BUTTON
// ======================================================
const todayButton = document.getElementById("todayButton");

todayButton?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];

  const datePicker = document.getElementById("datePicker");
  if (datePicker) datePicker.value = selectedDate;

  loadAppointments();
});


// ======================================================
// 📆 DATE PICKER
// ======================================================
const datePicker = document.getElementById("datePicker");

datePicker?.addEventListener("change", () => {
  selectedDate = datePicker.value;
  loadAppointments();
});


// ======================================================
// 📥 LOAD APPOINTMENTS
// ======================================================
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(
      selectedDate,
      patientName,
      token
    );

    // Clear old rows
    tableBody.innerHTML = "";

    // No data case
    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="noPatientRecord">
            No Appointments found for today.
          </td>
        </tr>`;
      return;
    }

    // Render each appointment
    appointments.forEach(app => {

      const patient = {
        id: app.patient?.id,
        name: app.patient?.name,
        phone: app.patient?.phone,
        email: app.patient?.email,
        appointmentId: app.id
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("Error loading appointments:", error);

    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="noPatientRecord">
          Error loading appointments. Try again later.
        </td>
      </tr>`;
  }
}


// ======================================================
// 🚀 INITIAL LOAD
// ======================================================
document.addEventListener("DOMContentLoaded", () => {

  // If your project uses renderContent()
  if (typeof renderContent === "function") {
    renderContent();
  }

  // Set date picker default
  if (datePicker) datePicker.value = selectedDate;

  loadAppointments();
});