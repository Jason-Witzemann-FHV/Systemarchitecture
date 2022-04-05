import { get } from 'svelte/store';
import { arrival, departure, socialSecurityNumber, chosenRooms} from '../storage/CreateBookingStorage.js';
import { Snackbar } from 'svelma'

export function createBooking() {

    let dateArrival = get(arrival)
    let dateDeparture = get(departure)
    let stringSocialSecurityNumber = get(socialSecurityNumber)
    let arrayChosenRooms = get(chosenRooms).map(r => `roomsArray=${r.room}`).join("&")

    let req = `http://localhost:8081/createBooking?`
        + `scnr=${stringSocialSecurityNumber}&`
        + `${arrayChosenRooms}&`
        + `arrivalDateString=${dateArrival}&`
        + `departureDateString=${dateDeparture}`

    fetch(req, {method: "POST"})
        .then(response => response.text())
        .then(text => {
            if(text === "true") {
                Snackbar.create({
                    message: "Buchung erfolgreich registriert!", 
                    type: "is-success",
               })
            } else {
                Snackbar.create({
                    message: "Ungültige Buchungsdaten!", 
                    type: "is-danger",
                })
            }
        })
}

