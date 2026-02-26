// ======================================================
// 🛠 ADMIN DASHBOARD — DOCTOR MANAGEMENT
// ======================================================

import { openModal } from "./components/modals.js";
import {
  getDoctors,
  filterDoctors,
  saveDoctor
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


// ======================================================
// ➕ OPEN ADD DOCTOR MODAL
// ======================================================
document.addEventListener("DOMContentLoaded", () => {
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) {
    addBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  loadDoctorCards();
});


// ======================================================
// 📥 LOAD ALL DOCTORS
// ======================================================
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();

    renderDoctorCards(doctors);

  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}


// ======================================================
// 🎨 RENDER DOCTOR CARDS
// ======================================================
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
    return;
  }

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}


// ======================================================
// 🔎 SEARCH & FILTER EVENTS
// ======================================================
document.getElementById("searchBar")?.addEventListener(
  "input",
  filterDoctorsOnChange
);

document.getElementById("filterTime")?.addEventListener(
  "change",
  filterDoctorsOnChange
);

document.getElementById("filterSpecialty")?.addEventListener(
  "change",
  filterDoctorsOnChange
);


// ======================================================
// 🔍 FILTER DOCTORS
// ======================================================
async function filterDoctorsOnChange() {
  const name =
    document.getElementById("searchBar")?.value.trim() || null;

  const time =
    document.getElementById("filterTime")?.value || null;

  const specialty =
    document.getElementById("filterSpecialty")?.value || null;

  try {
    const doctors = await filterDoctors(name, time, specialty);

    if (doctors && doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      document.getElementById("content").innerHTML =
        "<p>No doctors found with the given filters.</p>";
    }

  } catch (error) {
    console.error("Filter error:", error);
    alert("Error filtering doctors.");
  }
}


// ======================================================
// ➕ ADD NEW DOCTOR (FORM SUBMIT)
// Called from modal form
// ======================================================
window.adminAddDoctor = async function () {

  // ---- Collect form values ----
  const name = document.getElementById("docName").value;
  const specialty = document.getElementById("docSpecialty").value;
  const email = document.getElementById("docEmail").value;
  const password = document.getElementById("docPassword").value;
  const phone = document.getElementById("docPhone").value;

  // ---- Availability checkboxes ----
  const availableTimes = Array.from(
    document.querySelectorAll('input[name="availability"]:checked')
  ).map(cb => cb.value);

  // ---- Validate token ----
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Admin not authenticated.");
    return;
  }

  // ---- Build doctor object ----
  const doctor = {
    name,
    specialty,
    email,
    password,
    phone,
    availableTimes
  };

  try {
    const result = await saveDoctor(doctor, token);

    if (result.success) {
      alert("Doctor added successfully!");

      closeModal();   // close modal (from modals.js)
      loadDoctorCards(); // refresh list

    } else {
      alert(result.message || "Failed to add doctor.");
    }

  } catch (error) {
    console.error("Add doctor error:", error);
    alert("Error adding doctor.");
  }
};