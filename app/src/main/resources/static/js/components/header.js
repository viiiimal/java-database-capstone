// header.js

export function renderHeader() {

    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;
  
    // ===== 1️⃣ ROOT PAGE CHECK =====
    if (window.location.pathname.endsWith("/")) {
  
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
  
      headerDiv.innerHTML = `
        <header class="header">
          <div class="logo-section">
            <img src="../assets/images/logo/logo.png"
                 alt="Hospital CRM Logo"
                 class="logo-img">
            <span class="logo-title">Hospital CMS</span>
          </div>
        </header>
      `;
  
      return;
    }
  
    // ===== 2️⃣ GET SESSION DATA =====
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
  
    // ===== 3️⃣ INVALID SESSION CHECK =====
    if (
      (role === "loggedPatient" || role === "admin" || role === "doctor") &&
      !token
    ) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
    }
  
    // ===== 4️⃣ BASE HEADER STRUCTURE =====
    let headerContent = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png"
               alt="Hospital CRM Logo"
               class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
        <nav>
    `;
  
    // ===== 5️⃣ ROLE-BASED CONTENT =====
  
    if (role === "admin") {
  
      headerContent += `
        <button id="addDocBtn" class="adminBtn">
          Add Doctor
        </button>
        <a href="#" id="logoutBtn">Logout</a>
      `;
  
    } else if (role === "doctor") {
  
      headerContent += `
        <button class="adminBtn"
                onclick="selectRole('doctor')">
          Home
        </button>
        <a href="#" id="logoutBtn">Logout</a>
      `;
  
    } else if (role === "patient") {
  
      headerContent += `
        <button id="patientLogin" class="adminBtn">Login</button>
        <button id="patientSignup" class="adminBtn">Sign Up</button>
      `;
  
    } else if (role === "loggedPatient") {
  
      headerContent += `
        <button id="home" class="adminBtn"
          onclick="window.location.href='/pages/loggedPatientDashboard.html'">
          Home
        </button>
  
        <button id="patientAppointments" class="adminBtn"
          onclick="window.location.href='/pages/patientAppointments.html'">
          Appointments
        </button>
  
        <a href="#" id="logoutPatientBtn">Logout</a>
      `;
    }
  
    // ===== 6️⃣ CLOSE HEADER =====
    headerContent += `
        </nav>
      </header>
    `;
  
    // ===== 7️⃣ INJECT INTO PAGE =====
    headerDiv.innerHTML = headerContent;
  
    // ===== 8️⃣ ATTACH LISTENERS =====
    attachHeaderButtonListeners();
  }
  
  
  // ============================================
  // 🔧 HELPER FUNCTIONS
  // ============================================
  
  function attachHeaderButtonListeners() {
  
    // Admin Add Doctor button
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
      addDocBtn.addEventListener("click", () => {
        openModal("addDoctor");
      });
    }
  
    // Admin / Doctor logout
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", logout);
    }
  
    // Logged patient logout
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
      logoutPatientBtn.addEventListener("click", logoutPatient);
    }
  
    // Patient login button
    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
      patientLogin.addEventListener("click", () => {
        openModal("loginPatient");
      });
    }
  
    // Patient signup button
    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
      patientSignup.addEventListener("click", () => {
        openModal("signupPatient");
      });
    }
  }
  
  
  // ===== LOGOUT FUNCTIONS =====
  
  // Admin / Doctor logout
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
  }
  
  // Logged patient logout
  function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
  }