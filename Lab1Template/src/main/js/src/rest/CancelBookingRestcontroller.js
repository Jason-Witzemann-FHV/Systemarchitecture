
export function cancelBooking(bookingId) {

    let req = `http://localhost:8081/cancelBooking?bookingIdString=${bookingId}`
    console.log(req)
    fetch(req, {method: "POST"})

}