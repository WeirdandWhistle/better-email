import { get_sodium } from "./load_sodium.js";

let sodium = await get_sodium();

console.log("sodum is here!", sodium);