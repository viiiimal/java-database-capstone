// ======================================================
// 👤 PATIENT SERVICES MODULE
// Handles signup, login, profile, and appointments
// ======================================================

import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + "/patient";


// ======================================================
// 📝 PATIENT SIGNUP
// ======================================================
export async function patientSignup(data) {
  try {
    const response = await fetch(PATIENT_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    const result = await response.json();

    return {
      success: response.ok,
      message: result.message || "Signup completed"
    };

  } catch (error) {
    console.error("patientSignup error:", error);

    return {
      success: false,
      message: "Signup failed. Please try again."
    };
  }
}


// ======================================================
// 🔐 PATIENT LOGIN
// ======================================================
export async function patientLogin(data) {
  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    // Return full response for token handling
    return response;

  } catch (error) {
    console.error("patientLogin error:", error);
    throw error; // Let UI handle failure
  }
}


// ======================================================
// 👤 GET LOGGED-IN PATIENT DATA
// ======================================================
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);

    if (!response.ok) return null;

    const data = await response.json();

    return data.patient || null;

  } catch (error) {
    console.error("Error fetching patient data:", error);
    return null;
  }
}


// ======================================================
// 📅 GET PATIENT APPOINTMENTS
// Works for both patient dashboard and doctor dashboard
// ======================================================
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(
      `${PATIENT_API}/${id}/${user}/${token}`
    );

    if (!response.ok) return null;

    const data = await response.json();

    return data.appointments || [];

  } catch (error) {
    console.error("Error fetching appointments:", error);
    return null;
  }
}


// ======================================================
// 🔎 FILTER APPOINTMENTS
// ======================================================
export async function filterAppointments(condition, name, token) {
  try {
    const url = `${PATIENT_API}/filter/${condition}/${name}/${token}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      console.error("Filter request failed:", response.statusText);
      return [];
    }

    const data = await response.json();

    return data.appointments || [];

  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Something went wrong while filtering appointments.");
    return [];
  }
}
