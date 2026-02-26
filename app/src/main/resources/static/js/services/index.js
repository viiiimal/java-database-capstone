// ===== IMPORTS =====
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// ===== API ENDPOINTS =====
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";


// ======================================================
// 🚀 BUTTON EVENT BINDING AFTER PAGE LOAD
// ======================================================
window.onload = function () {

  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }
};


// ======================================================
// 🔐 ADMIN LOGIN HANDLER
// ======================================================
window.adminLoginHandler = async function () {

  try {

    // Step 1: Get credentials
    const username = document.getElementById("adminUsername").value;
    const password = document.getElementById("adminPassword").value;

    // Step 2: Create object
    const admin = { username, password };

    // Step 3: Send POST request
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(admin)
    });

    // Step 4: Handle success
    if (response.ok) {
      const data = await response.json();

      localStorage.setItem("token", data.token);

      // Move to admin role flow
      selectRole("admin");

    } else {
      alert("Invalid admin credentials!");
    }

  } catch (error) {
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again.");
  }
};


// ======================================================
// 👨‍⚕️ DOCTOR LOGIN HANDLER
// ======================================================
window.doctorLoginHandler = async function () {

  try {

    // Step 1: Get credentials
    const email = document.getElementById("doctorEmail").value;
    const password = document.getElementById("doctorPassword").value;

    // Step 2: Create object
    const doctor = { email, password };

    // Step 3: Send POST request
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    // Step 4: Handle success
    if (response.ok) {
      const data = await response.json();

      localStorage.setItem("token", data.token);

      // Move to doctor role flow
      selectRole("doctor");

    } else {
      alert("Invalid doctor credentials!");
    }

  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again.");
  }
};