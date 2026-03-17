import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

window.onload = function() {
    const adminBtn = document.getElementById("adminBtn");
    const doctorBtn = document.getElementById("doctorBtn");

    if(adminBtn) {
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

function async adminLoginHandler() {
    try {
        const admin = { username, password };
        admin.username = document.getElementById(inputFieldUsername);
        admin.password = document.getElementById(inputFieldPassword);

        const response = await fetch(
            ADMIN_API,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(admin)
            });
        const result = await response.json();
        if(!response.ok) {
            throw new Error(result.message);
        }
        localStorage.setItem("token", result.token);
        selectRole("admin");
        return { success: response.ok, message: result.message }
    } catch (Exception e) {
        console.error("Error :: adminLoginHandler :: ", error)
        return { success: false, message: error.message }
    }
}

function async doctorLoginHandler() {
    try {
        const doctor = { email, password };
        doctor.email = document.getElementById(inputFieldEmail);
        doctor.password = document.getElementById(inputFieldPassword);

        const response = await fetch(
            DOCTOR_API,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(doctor)
            });
        const result = await response.json();
        if(!response.ok) {
            throw new Error(result.message);
        }
        localStorage.setItem("token", result.token);
        selectRole("doctor");
        return { success: response.ok, message: result.message }
    } catch (Exception e) {
        console.error("Error :: doctorLoginHandler :: ", error)
        return { success: false, message: error.message }
    }
}
