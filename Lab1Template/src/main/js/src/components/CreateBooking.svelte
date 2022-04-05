<script>

    import { arrival, departure, socialSecurityNumber, chosenRooms } from '../storage/CreateBookingStorage.js';
    import { createBooking } from '../rest/CreateBookingController.js'
	import { fade } from 'svelte/transition';
    import { Field, Input, Button } from 'svelma'

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

        <Field label="Ankunft">
            <Input type="date" bind:value={$arrival} />
        </Field>

        <Field label="Abreise">
            <Input type="date" bind:value={$departure} />
        </Field>

        <Field label="Sozialversicherungsnummer">
            <Input type="text" bind:value={$socialSecurityNumber} placeholder="1258 311298"/>
        </Field>

        <Button type="{buttonStatus} mt-5" on:click={createBooking}>Buchen!</Button>

    </div>

    <div class="column is-6">

        <label class="label" for="hehe">Räume</label>

        {#each $chosenRooms as { room } }
            <div class="columns" transition:fade="{{ duration: 250 }}">
                <div class="column is-9">
                    <Input type="text" bind:value={room} placeholder="S_001"/>
                </div>   
                <div class="column is-3">
                    <Button type="is-danger is-light" on:click={ () => removeRoom({room}) }>Entfernen</Button>
                </div>
            </div>
        {/each}
        <Button type="is-primary is-light" on:click={addRoom}>Neuer Raum</Button>
    </div>
    
</div>

