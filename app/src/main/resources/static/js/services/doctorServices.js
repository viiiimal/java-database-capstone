// ===== IMPORT BASE API URL =====
import { API_BASE_URL } from "../config/config.js";

// ===== DOCTOR API BASE ENDPOINT =====
const DOCTOR_API = API_BASE_URL + "/doctor";


// ======================================================
// 🩺 GET ALL DOCTORS
// ======================================================
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);

    const data = await response.json();

    // Return doctors list safely
    return data.doctors || data || [];

  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}


// ======================================================
// ❌ DELETE DOCTOR (ADMIN ONLY)
// ======================================================
export async function deleteDoctor(id, token) {
  try {

    const url = `${DOCTOR_API}/${id}?token=${token}`;

    const response = await fetch(url, {
      method: "DELETE"
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor deleted"
    };

  } catch (error) {
    console.error("Error deleting doctor:", error);

    return {
      success: false,
      message: "Failed to delete doctor"
    };
  }
}


// ======================================================
// ➕ SAVE (ADD) NEW DOCTOR
// ======================================================
export async function saveDoctor(doctor, token) {
  try {

    const url = `${DOCTOR_API}?token=${token}`;

    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor saved successfully"
    };

  } catch (error) {
    console.error("Error saving doctor:", error);

    return {
      success: false,
      message: "Failed to save doctor"
    };
  }
}


// ======================================================
// 🔍 FILTER DOCTORS
// ======================================================
export async function filterDoctors(name, time, specialty) {
  try {

    // Handle empty filters
    const queryParams = new URLSearchParams();

    if (name) queryParams.append("name", name);
    if (time) queryParams.append("time", time);
    if (specialty) queryParams.append("specialty", specialty);

    const url = `${DOCTOR_API}/filter?${queryParams.toString()}`;

    const response = await fetch(url);

    if (!response.ok) {
      console.error("Filter request failed");
      return [];
    }

    const data = await response.json();

    return data.doctors || data || [];

  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors.");
    return [];
  }
}