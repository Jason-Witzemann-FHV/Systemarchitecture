import { writable } from 'svelte/store';

export const freeRooms = writable([]);

export const arrival = writable(new Date().toISOString().slice(0,10));

export const departure = writable(new Date(new Date().getTime() + 86400000).toISOString().slice(0,10));

export const beds = writable(2);