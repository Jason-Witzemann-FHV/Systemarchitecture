<script>

    import { arrival, departure, socialSecurityNumber, chosenRooms } from '../storage/CreateBookingStorage.js';
    import { createBooking } from '../rest/CreateBookingController.js'
	import { fade } from 'svelte/transition';

    $: buttonStatus = ($departure >= $arrival && $socialSecurityNumber.length > 0 && $chosenRooms.filter(r => r.room !== "").length > 0) ? "is-primary" : "disabled";

    function addRoom() {
        $chosenRooms = [...$chosenRooms, {"room": ""}]
    }

    function removeRoom(room) {
        $chosenRooms = [...$chosenRooms.filter(r => r.room !== room.room)]
    }

</script>

<h1 class="title">Erstelle eine Buchung!</h1>

<p class="subtitle">Wir freuen uns auf deinen Besuch!</p>

<div class="columns">
    <div class="column is-6">

        <label class="label" for="create-booking-from">Ankunft</label>
        <input class="input" type="date" id="create-booking-from" bind:value={$arrival}/>
        
        <label class="label mt-5" for="create-booking-to">Abreise</label>
        <input class="input" type="date" id="create-booking-to" bind:value={$departure}/>

        <label class="label mt-5" for="create-booking-ssn">Sozialversicherungsnummer</label>
        <input class="input" type="text" id="create-booking-ssn" bind:value={$socialSecurityNumber} placeholder="1258 311298"/>
       
        <button class="button {buttonStatus} mt-5" id="free-room-button" on:click={createBooking}>Buchen!</button>

    </div>

    <div class="column is-6">

        <label class="label" for="hehe">Räume</label>

        {#each $chosenRooms as { room } }
            <div class="columns" transition:fade="{{ duration: 250 }}">
                <div class="column is-9">
                    <input class="input" type="text" bind:value={room} placeholder="S_001"/>
                </div>   
                <div class="column is-3">
                    <button class="button is-danger is-light" on:click={ () => removeRoom({room}) }>Entfernen</button>
                </div>
            </div>
        {/each}
        <button class="button is-primary is-light" on:click={addRoom}>Neuer Raum</button>
    </div>
    
</div>

