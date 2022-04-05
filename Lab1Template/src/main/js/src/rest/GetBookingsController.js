import { get } from 'svelte/store';

import { bookings, arrival, departure } from '../storage/GetBookingsStorage.js';


export function getBookedStays() {


    let dateArrival = get(arrival)
    let dateDeparture = get(departure)

    let req = `http://localhost:8082/bookings?from=${dateArrival}&to=${dateDeparture}`
    fetch(req)
        .then((response) => {
            if (response.ok) {
                return response.json()
            } else {
                throw new Error("The server was unable to handle the request.");
            }
        })
        .then(data => bookings.update(old => data))

}