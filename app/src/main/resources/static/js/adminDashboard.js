/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form
*/
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCards.js";
import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';
const addDoctorBtn = document.getElementById('addDocBtn')

if(addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => {
        openModal("addDoctor");
    });
}

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();

        const mainContentDiv = document.getElementById("mainContentDiv");
        if(mainContentDiv) {
            mainContentDiv.innerHTML = "";
        }

        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor)
            mainContentDiv.appendChild(card);
        });
    } catch (error) {
        console.error("Error :: loadDoctorCards ::" + error);
    }
}

const searchBar = document.getElementById('searchBar');
if(searchBar) {
    searchBar.addEventListener("input", filterDoctorsOnChange);
}

const searchTimeSelect = document.getElementById('searchTimeSelect');
if(searchTimeSelect) {
    searchTimeSelect.addEventListener("change", filterDoctorsOnChange);
}

const searchSpecialtySelect = document.getElementById('searchSpecialtySelect');
if(searchSpecialtySelect) {
    searchSpecialtySelect.addEventListener("change", filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {
    try {
        //const searchValue = searchBar.getValue();
        //const timeValue = searchTimeSelect.getValue();
        //const specialtyValue = searchSpecialtySelect.getValue();
        let searchValue = searchBar.getValue();
        let timeValue = searchTimeSelect.getValue();
        let specialtyValue = searchSpecialtySelect.getValue();

        if(searchValue == null || searchValue == undefined || searchValue == "") {
            searchValue = null;
        }

        if(timeValue == null || timeValue == undefined || timeValue == "") {
            timeValue = null;
        }

        if(specialtyValue == null || specialtyValue == undefined || specialtyValue == "") {
            specialtyValue = null;
        }

        const result = await filterDoctors(searchValue, timeValue, specialtyValue);
        if(!result.success) {
            alert("No doctors found with the given filters.");
            return;
        }

        renderDoctorCards(result.doctors);
    } catch (error) {
        console.error("Error :: filterDoctorsOnChange ::" + error);
        alert("Error :: filterDoctorsOnChange ::" + error);
    }
}

function renderDoctorCards(doctor) {
    const mainContentDiv = document.getElementById("mainContentDiv");
    if(mainContentDiv) {
        mainContentDiv.innerHTML = "";
    }

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor)
        mainContentDiv.appendChild(card);
    });
}

window.adminAddDoctor = async function () {
    try {
        const name = document.getElementById("name").value;
        const specialty = document.getElementById("specialty").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const phone = document.getElementById("phone").value;
        const availableTimes = document.getElementById("availableTimes").value;
        const yearsOfExperience = document.getElementById("yearsOfExperience").value;
        const clinicAddress = document.getElementById("clinicAddress").value;
        const rating = document.getElementById("rating").value;

        const doctor =
            { name, specialty, email, password, phone, availableTimes, yearsOfExperience, clinicAddress, rating };
        const token = localStorage.getItem("token");
        if(!token) {
            alert("Invalid log in credentials. Please log in.");
            return;
        }

        const result = await saveDoctor(data);
        alert(message);
        if(!result.success) {
            return;
        }

        document.getElementById("modal").style.display = "none";
        window.location.reload();
        // loadDoctorCards();
    } catch (error) {
        console.error("Error :: adminAddDoctor :: ", error);
        alert("❌ An error occurred while adding a Doctor.");
    }
}

document.addEventListener("DOMContentLoaded", loadDoctorCards);
/*
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});
*/
