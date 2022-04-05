<script>

	import { freeRooms, arrival, departure, beds } from '../storage/FreeRoomStorage.js';
	import { findFreeRooms } from '../rest/FreeRoomsController.js'
	import { fly } from 'svelte/transition';
	import { Field, Input, Button, Tag } from 'svelma'

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
		<Field label="Ankunft">
            <Input type="date" bind:value={$arrival} />
        </Field>
	</div>
	<div class="column is-4">
		<Field label="Abreise">
            <Input type="date" bind:value={$departure} />
        </Field>
	</div>
	<div class="column is-2">
		<Field label="Raumgröße">
            <Input type="number" min="1" max="9" bind:value={$beds} />
        </Field>
	</div>
	<div class="column is-2">
		<Field label="&#8203">
            <Button type="{buttonStatus}" on:click={ findFreeRooms }>Check!</Button>
        </Field>
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
						<td class="has-text-centered"> <Tag type="is-light">{amountOfPeople} {amountOfPeople == 1 ? "Bett" : "Betten" } </Tag> </td>
					</tr>
				{/each}

			</table>
		</div>
		<div class="column is-1">
			<Button type="is-disabled has-text-centered" on:click={resetFoundRooms}>Clear</Button>
		</div>
	</div>
{/if}