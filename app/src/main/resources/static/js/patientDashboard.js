// ======================================================
// 🧑‍⚕️ PATIENT DASHBOARD — VIEW & FILTER DOCTORS
// ======================================================

import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';


// ======================================================
// 🚀 INITIAL PAGE LOAD
// ======================================================
document.addEventListener("DOMContentLoaded", () => {

  // Render common layout if your project uses it
  if (typeof renderContent === "function") {
    renderContent();
  }

  loadDoctorCards();

  // Login & Signup modal triggers
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

  // Search & Filters
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});


// ======================================================
// 📥 LOAD ALL DOCTORS
// ======================================================
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();

    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });

  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}


// ======================================================
// 🔎 SEARCH & FILTER LOGIC
// ======================================================
async function filterDoctorsOnChange() {

  const name = document.getElementById("searchBar")?.value.trim() || null;
  const time = document.getElementById("filterTime")?.value || null;
  const specialty = document.getElementById("filterSpecialty")?.value || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];

    renderDoctorCards(doctors);

  } catch (error) {
    console.error("Filtering failed:", error);
    alert("❌ Error while filtering doctors.");
  }
}


// ======================================================
// 🧱 RENDER DOCTOR CARDS
// ======================================================
function renderDoctorCards(doctors) {

  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (doctors.length > 0) {
    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } else {
    contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
  }
}


// ======================================================
// 📝 PATIENT SIGNUP
// ======================================================
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };

    const { success, message } = await patientSignup(data);

    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
      alert(message);
    }

  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ Signup error.");
  }
};


// ======================================================
// 🔐 PATIENT LOGIN
// ======================================================
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await patientLogin({ email, password });

    if (response.ok) {

      const result = await response.json();

      localStorage.setItem("token", result.token);
      selectRole("loggedPatient");

      window.location.href = "/pages/loggedPatientDashboard.html";

    } else {
      alert("❌ Invalid credentials!");
    }

  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Login error.");
  }
};