<script>

	import { bookings, arrival, departure } from '../storage/GetBookingsStorage.js';
	import { getBookedStays } from '../rest/GetBookingsController.js'
    import { cancelBooking } from '../rest/CancelBookingRestcontroller.js'
	import { fly } from 'svelte/transition';

	$: buttonStatus = ($departure >= $arrival) ? "is-primary" : "disabled";

    function resetFoundRooms() {
		$bookings = [];
	}

	
    function prepareCancelBooking(bookingId) {
        $bookings = [...$bookings.filter(b => b.bookingId !== bookingId)]
		cancelBooking(bookingId)
    }

</script>

<h1 class="title">Gebuchte Aufenthalte</h1>
<p class="subtitle">
	Hier kannst du überprüfen, wer sich alles zu einem bestimmten<br> 
    Zeitraum im Hotel CQRS befindet!
</p>
<div class="columns">
	<div class="column is-5">
		<label class="label" for="get-bookings-from">Von</label>
		<div class="control">
			<input class="input" type="date" id="get-bookings-from" bind:value={$arrival} />
		</div>
	</div>
	<div class="column is-5">
		<label class="label" for="get-bookings-to">Bis</label>
		<div class="control">
			<input class="input" type="date" id="get-bookings-to" bind:value={$departure}/>
		</div>
	</div>
	<div class="column is-2">
		<label class="label" for="get-bookings-button" hidden>&#8203</label>
		<div class="control">
			<button class="button {buttonStatus}" id="get-bookings-button" on:click={ getBookedStays }>Check!</button>
		</div>
	</div>
</div>

{#if $bookings.length > 0 } 
	<div class="columns" transition:fly="{{ y: -50, duration: 1000 }}">
		<div class="is-1"/>
		<div class="column is-10">
			<table class="table is-fullwidth">
				<thead>
					<th>Kunde</th>
					<th>Ankunft</th>
					<th>Abreise</th>
					<th>Räume</th>
                    <th></th>
				</thead>

				{#each $bookings as { bookingId, from, to, rooms, customerName, } }
					<tr>
						<td> {customerName} </td>
						<td> {from} </td>
						<td> {to} </td>
                        <td class="has-text-centered"> <span class="tag">{rooms}</span> </td>
                        <td> <button class="button is-danger is-light" on:click={ () => prepareCancelBooking(bookingId)}>Stornieren</button> </td>
					</tr>
				{/each}

			</table>
		</div>
		<div class="column is-1">
			<button class="button is-disabled has-text-centered" on:click={resetFoundRooms}>Clear</button>
		</div>
	</div>
{/if}