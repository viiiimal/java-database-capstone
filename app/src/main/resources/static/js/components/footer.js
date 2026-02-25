// footer.js

/*
  Dynamically renders the footer across all pages
*/

function renderFooter() {

    // ===== 1️⃣ SELECT FOOTER CONTAINER =====
    const footer = document.getElementById("footer");
    if (!footer) return;
  
    // ===== 2️⃣ INSERT FOOTER HTML =====
    footer.innerHTML = `
      <footer class="footer">
  
        <!-- FOOTER CONTAINER -->
        <div class="footer-container">
  
          <!-- LOGO SECTION -->
          <div class="footer-logo">
            <img src="../assets/images/logo/logo.png"
                 alt="Hospital CMS Logo">
            <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
          </div>
  
          <!-- LINKS SECTION -->
          <div class="footer-links">
  
            <!-- Company Column -->
            <div class="footer-column">
              <h4>Company</h4>
              <a href="#">About</a>
              <a href="#">Careers</a>
              <a href="#">Press</a>
            </div>
  
            <!-- Support Column -->
            <div class="footer-column">
              <h4>Support</h4>
              <a href="#">Account</a>
              <a href="#">Help Center</a>
              <a href="#">Contact Us</a>
            </div>
  
            <!-- Legals Column -->
            <div class="footer-column">
              <h4>Legals</h4>
              <a href="#">Terms & Conditions</a>
              <a href="#">Privacy Policy</a>
              <a href="#">Licensing</a>
            </div>
  
          </div> <!-- End footer-links -->
  
        </div> <!-- End footer-container -->
  
      </footer>
    `;
  }
  
  
  // ===== 3️⃣ CALL FUNCTION =====
  renderFooter();