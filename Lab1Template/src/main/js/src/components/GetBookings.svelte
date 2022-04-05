<script>

	import { bookings, arrival, departure } from '../storage/GetBookingsStorage.js';
	import { getBookedStays } from '../rest/GetBookingsController.js'
    import { cancelBooking } from '../rest/CancelBookingRestcontroller.js'
	import { fly } from 'svelte/transition';
	import { Field, Input, Button, Tag } from 'svelma'

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
		<Field label="Von">
            <Input type="date" bind:value={$arrival} />
        </Field>
	</div>
	<div class="column is-5">
		<Field label="Bis">
            <Input type="date" bind:value={$departure} />
        </Field>
	</div>
	<div class="column is-2">
		<Field label="&#8203">
            <Button type="{buttonStatus}" on:click={ getBookedStays }>Check!</Button>
        </Field>
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
                        <td class="has-text-centered"> <Tag>{rooms}</Tag> </td>
                        <td> <Button type="is-danger is-light" on:click={ () => prepareCancelBooking(bookingId)}>Stornieren</Button> </td>
					</tr>
				{/each}

			</table>
		</div>
		<div class="column is-1">
			<Button type="is-disabled has-text-centered" on:click={resetFoundRooms}>Clear</Button>
		</div>
	</div>
{/if}