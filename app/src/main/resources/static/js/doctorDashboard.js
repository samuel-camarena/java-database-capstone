import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const appointmentsTableBody = document.getElementById('appointmentsTableBody');
const datePicker = document.getElementById('datePicker')
if(datePicker) {
    datePicker.setDate = Time.today(); //   Initialize selectedDate with today's date in 'YYYY-MM-DD' format
}

const token = localStorage.getItem("token");
const selectedDate = null;
const patientName = null;

const searchBar = document.getElementById('searchBar');
if(searchBar) {
    searchBar.addEventListener("input", filterAppointmentsOnChange);
}

const searchTimeSelect = document.getElementById('searchTimeSelect');
if(searchTimeSelect) {
    searchTimeSelect.addEventListener("change", filterAppointmentsOnChange);
}

const searchSpecialtySelect = document.getElementById('searchSpecialtySelect');
if(searchSpecialtySelect) {
    searchSpecialtySelect.addEventListener("change", filterAppointmentsOnChange);
}

function async filterAppointmentsOnChange() {
    try {
        const searchValue = searchBar.value.trim(); // Trim and check the input value
        //const searchValue = document.getElementById("searchBar").value.trim()
        const timeValue = searchTimeSelect.value;
        const specialtyValue = searchSpecialtySelect.value;

        const name = searchValue.length > 0 ? searchValue : null;
        const time = timeValue.length > 0 ? timeValue : null;
        const specialty = specialtyValue.length > 0 ? specialtyValue : null;

        loadAppointments();
    } catch (error) {
        console.error("Error :: filterAppointmentsOnChange :: " + error);
    }
}

const todayBtn = document.getElementById('todayBtn');
if(todayBtn) {
    todayBtn.addEventListener("click", () => {
        const datePicker = document.getElementById('datePicker')
        if(datePicker) {
            selectedDate = Time.today();
            datePicker.setDate = Time.today(); //   Initialize selectedDate with today's date in 'YYYY-MM-DD' format
        }
    });

    loadAppointments();
}

const datePicker = document.getElementById('datePicker');
if(datePicker) {
    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.getValue();
        loadAppointments();
    });
}

/*
    Fetch and display appointments based on selected date and optional patient name
*/
function async loadAppointments() {
    try {
        const result = await getAllAppointments(selectedDate, patientName, token);
        if(!appointmentsTableBody) {
            alert("❌ No table body to display appointments!");
            console.error("Error :: loadAppointments :: No table body to display appointments!");
            return;
        }
        appointmentsTableBody.innerHTML = "";

        if(!result.success) {
            appointmentsTableBody.innerHTML = "<p>No Appointments found for today.</p>";
            return;
        }

        result.appointments.forEach(appoint => {
            const patient = {
                id: appoint.id,
                name: appoint.name,
                phone: appoint.phone,
                email: appoint.email
            };
            const row = createPatientRow(appoint);
            appointmentsTableBody.appendChild(row);
        };
    } catch (error) {
        appointmentsTableBody.innerHTML = "<p>Error loading appointments. Try again later.</p>";
        console.error("Error :: loadAppointments :: " + error);
    }
}

document.addEventListener("DOMContentLoaded", renderContent);
document.addEventListener("DOMContentLoaded", loadAppointments);
/*
document.addEventListener("DOMContentLoaded", () => {
  loadAppointments();
});
*/