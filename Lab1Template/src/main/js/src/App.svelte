<script>

	import 'bulma/css/bulma.css'
	import FreeRooms from './components/FreeRooms.svelte'
	import CreateBooking from './components/CreateBooking.svelte'
	import GetBookings from './components/GetBookings.svelte'

</script>

<section class="hero is-success is-normal">
	<div class="columns">
		<div colum="column">
			<div class="hero-body">
				<p class="title">Hotel CQRS</p>
				<p class="subtitle">
					<i>Sadly, our customers will not recognize our clean architecture... :( </i>
				</p>
			</div>
		</div>
		<div class="column has-text-right">
			<img src="./logo_cqrs.png" class="has-text-right" alt="logo" width="150">
		</div>

	</div>
</section>
<div class="columns mt-6">
	<div class="column is-1" />
	<div class="column is-5">
		<FreeRooms/>
	</div>
	<div class="column is-5">
		<CreateBooking/>
	</div>
	<div class="column is-1"/>
</div>
<div class="columns mt-6">
	<div class="column is-2" />
	<div class="column is-8">
		<GetBookings/>
	</div>
	
	<div class="column is-2" />
</div>
