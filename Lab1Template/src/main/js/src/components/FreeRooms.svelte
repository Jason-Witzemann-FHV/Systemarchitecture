<script>

	import { freeRooms, arrival, departure, beds } from '../storage/FreeRoomStorage.js';
	import { findFreeRooms } from '../rest/FreeRoomsController.js'
	import { fly } from 'svelte/transition';

	$: buttonStatus = ($departure >= $arrival) ? "is-primary" : "disabled";

	function resetFoundRooms() {
		$freeRooms = [];
	}

</script>

<h1 class="title">Raum-Verfügbarkeit</h1>
<p class="subtitle">
	Überprüfe, ob gengügend Räume für deine Buchung verfügbar sind!
</p>
<div class="columns">
	<div class="column is-4">
		<label class="label" for="free-rooms-from">Ankunft</label>
		<div class="control">
			<input class="input" type="date" id="free-rooms-from" bind:value={$arrival} />
		</div>
	</div>
	<div class="column is-4">
		<label class="label" for="free-rooms-to">Abreise</label>
		<div class="control">
			<input class="input" type="date" id="free-rooms-to" bind:value={$departure}/>
		</div>
	</div>
	<div class="column is-2">
		<label class="label" for="free-rooms-nr-of-people">Raumgröße</label>
		<div class="control">
			<input
				class="input"
				type="number"
				id="free-rooms-nr-of-people"
				min="1"
				max="9"
				bind:value={$beds}
			/>
		</div>
	</div>
	<div class="column is-2">
		<label class="label" for="free-rooms-nr-of-people" hidden>&#8203</label>
		<div class="control">
			<button class="button {buttonStatus}" id="free-room-button" on:click={ findFreeRooms }>Check!</button>
		</div>
	</div>
</div>

{#if $freeRooms.length > 0 } 
	<div class="columns" transition:fly="{{ y: -50, duration: 1000 }}">
		<div class="is-1"/>
		<div class="column is-10">
			<table class="table is-fullwidth">
				<thead>
					<th>Raumnummer</th>
					<th>Verfügbar von</th>
					<th>Verfügbar bis</th>
					<th>Anzahl an Betten</th>
				</thead>

				{#each $freeRooms as { roomnumber, from, to, amountOfPeople } }
					<tr>
						<td> {roomnumber} </td>
						<td> {from} </td>
						<td> {to} </td>
						<td class="has-text-centered"> <span class="tag is-light">{amountOfPeople} {amountOfPeople == 1 ? "Bett" : "Betten" } </span> </td>
					</tr>
				{/each}

			</table>
		</div>
		<div class="column is-1">
			<button class="button is-disabled has-text-centered" on:click={resetFoundRooms}>Clear</button>
		</div>
	</div>
{/if}