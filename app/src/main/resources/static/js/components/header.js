/*
    This code dynamically renders the header section of the page based on the user's role, session status,
    and available actions (such as login, logout, or role-switching).
    - The `renderHeader` function is responsible for rendering the entire header
      based on the user's session, role, and whether they are logged in.
*/
function renderHeader() {
    const headerDiv = document.getElementById("headerDiv");
    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="./assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>`;

    if (window.location.pathname.endsWith("/")) {
        headerDiv.innerHTML = headerContent + '</header>';
        logout();
        return;
    }


    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        headerDiv.innerHTML = headerContent + '</header>';
        alert("Session expired or invalid login. Please log in again.");
        logout();
        return;
    }

    headerContent += '<nav>';
    switch(role) {
        case "admin":
            headerContent += `
                <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
                <a href="#" onclick="logout()">Logout</a>`;
            break;
        case "doctor":
            headerContent += `
                <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
                <a href="#" onclick="logout()">Logout</a>`;
            break;
        case "patient":
            headerContent += `
                <button id="patientLogin" class="adminBtn">Login</button>
                <button id="patientSignup" class="adminBtn">Sign Up</button>`;
            break;
        case "loggedPatient":
            headerContent += `
                <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
                <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
                <a href="#" onclick="logoutPatient()">Logout</a>`;
            break;
        default:
            console.log('Wrong role type');
            break;
    }
    headerDiv.innerHTML = headerContent += "</nav></header>";
    //attachHeaderButtonListeners();
}

// Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.
function attachHeaderButtonListeners() {
    const patientLoginBtn = document.getElementById("patientLogin");
    patientLoginBtn.addEventListener("click", loginPatient);

    const patientSignupBtn = document.getElementById("patientSignup");
    patientLoginBtn.addEventListener("click", signupPatient());

}

function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    //window.location.href = "/";
    window.location.href = "http://localhost:63342/coursera-java-c11-java-capstone-project/back-end/static/index.html?_ijt=qvqb49rndq2kp1qs1rut7aebv4&_ij_reload=RELOAD_ON_SAVE";
}

function logoutPatient() {
    localStorage.setItem("userRole", "patient");
    localStorage.removeItem("token");
    window.location.href = "/pages/patientDashboard.html"
}

renderHeader();
/*


  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
   
