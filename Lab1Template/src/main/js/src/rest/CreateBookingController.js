import { get } from 'svelte/store';
import { arrival, departure, socialSecurityNumber, chosenRooms} from '../storage/CreateBookingStorage.js';

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

    console.log(req)
    fetch(req, {method: "POST"})

}