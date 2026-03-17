// doctorServices
import { API_BASE_URL } from "../config/config.js";
const DOCTOR_API = API_BASE_URL + '/doctor'

export async function getDoctors() {
    try {
        const response = await fetch(`${DOCTOR_API}`);
        const result = await response.json();
        if(!response.ok) {

        }
        return { success: response.ok, message: result.message, result.doctors}
    } catch (error) {
        console.error("Error :: getDoctors :: ", error)
        return { success: false, message: error.message, doctors: [] }
    }
}

export async function deleteDoctor(id, token) {
    try {
        let doctorParametrizedURL = DOCTOR_API + "/?id=" + id + "&token=" + token;

        let response = await fetch(
            doctorParametrizedURL,
            {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
            });

        const result = await response.json();
        if(!response.ok) {
            throw new Error(result.message);
        }
        return { success: response.ok, message: result.message }
    } catch (error) {
        console.error("Error :: deleteDoctor :: ", error)
        return { success: false, message: error.message }
    }
}

export async function saveDoctor(doctor, token) {
    try {
        let doctorParametrizedURL = DOCTOR_API + "/?token=" + token;

        let response = await fetch(
            doctorParametrizedURL,
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
        return { success: response.ok, message: result.message }
    } catch (Exception e) {
        console.error("Error :: saveDoctor :: ", error)
        return { success: false, message: error.message }
    }
}

export async function filterDoctors(name ,time ,specialty) {
    try {
        /*
        const response = await fetch(
            `${PATIENT_API}/filter/${condition}/${name}/${token}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
        */
        let doctorParametrizedURL;

        if(name != null && name != undefined && name != "") {
            doctorParametrizedURL = DOCTOR_API + "/?name=" + name;
        }

        if(time != null && time != undefined && time != "") {
            if(doctorParametrizedURL) {
                doctorParametrizedURL += "&time=" + time;
            } else {
                doctorParametrizedURL = DOCTOR_API + "/?time=" + time;
            }
        }

        if(specialty != null && specialty != undefined && specialty != "") {
            if(doctorParametrizedURL) {
                doctorParametrizedURL += "&specialty=" + specialty;
            } else {
                doctorParametrizedURL = DOCTOR_API + "/?specialty=" + specialty;
            }
        }

        const response = await fetch(
            doctorParametrizedURL,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

        const result = await response.json();
        if(!response.ok) {
            throw new Error(result.message);
        }
        return { success: response.ok, message: result.message }
    } catch (Exception e) {
        console.error("Error :: filterDoctors :: ", error)
        return { success: false, message: error.message }
    }
}