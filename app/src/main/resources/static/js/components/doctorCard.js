
import { showBookingOverlay } from './loggedPatient.js';
import { deleteDoctor } from './services/doctorServices.js';
import { getPatientData } from './services/patientServices.js';

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    const cardInfoDiv = document.createElement("div");
    const cardActionsDiv = document.createElement("div");
    const name = document.createElement("h3");
    const specialty = document.createElement("span");
    const email = document.createElement("span");
    const availability = document.createElement("p");

    card.classList.add("doctor-card");
    actionsDiv.classList.add("card-actions");
    cardInfoDiv.classList.add("doctor-info");

    name.textContent = doctor.name;
    cardInfoDiv.appendChild(name);
    cardInfoDiv.appendChild(specialization);
    cardInfoDiv.appendChild(email);
    cardInfoDiv.appendChild(availability);

    cardActionsDiv

    const role = localStorage.getItem("userRole");
    if(role == null || role == undefined) {
        return;
    }

    switch(role.toLowerCase()) {
        case "admin":
            const removeBtn = document.createElement("button");
            removeBtn.textContent = "Delete";
            removeBtn.addEventListener("click", async () => {
                alert("Confirm deletion!");
                const token = localStorage.getItem("token");
                let isDeleted = doctorService.deleteDoctor(doctor.id, token);
                if(isDeleted) {
                    Document.deleteElement(doctorCard);
                }
            });
            break;
        case "patient":
            const bookNow = document.createElement("button");
            bookNow.textContent = "Book Now";
            bookNow.addEventListener("click", () => {
                alert("Patient needs to login first.");
            });
            break;
        case "loggedPatient":
            const bookNow = document.createElement("button");
            bookNow.textContent = "Book Now";
            bookNow.addEventListener("click", async (e) => {
                const token = localStorage.getItem("token");
                const patientData = await getPatientData(token);
                showBookingOverlay(e, doctor, patientData);
            });
            break;
        default:
            return;
    }
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);
    return card;
}
/*

  Function to create and return a DOM element for a single doctor card



    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
