import { get } from 'svelte/store';

import { freeRooms, arrival, departure, beds } from '../storage/FreeRoomStorage.js';


export function findFreeRooms() {

    let numberBeds = get(beds)
    let dateArrival = get(arrival)
    let dateDeparture = get(departure)

    let req = `http://localhost:8082/freeRooms?from=${dateArrival}&to=${dateDeparture}&numberOfPeople=${numberBeds}`
    fetch(req)
        .then((response) => {
            if (response.ok) {
                return response.json()
            } else {
                throw new Error("The server was unable to handle the request.");
            }
        })
        .then(data => freeRooms.update(old => data))
}