import * as c from "/js/crypto.js";
import { get_sodium } from "/js/load_sodium.js";

const sodium = await get_sodium();

export async function login(username, password){
    let baseKey = c.Keys.computePassword(username, password);
    const res = await fetch(`/emapi/v1/signup?username=${username}`);

    baseKey = await baseKey;
    const vaultKey = c.Crypto.generateVaultKey(sodium, baseKey);

    const vault = c.Vault.decodeVault(sodium, vaultKey, await res.json());

    return vault;
}
export async function checkLogin(json, username, password){
    const vault = await login(username, password);

    const good = true;
    if(json.other && json.other !== vault.other) good = false;
    if(json.verifyedPeople && json.verifyedPeople !== vault.verifyedPeople) good = false;

    if(!good){
        throw new Error("Vault on server is not the same as local. SERVER CAN NOT BE TRUSTED! or some caching error. try again.");
    }
    return vault;
}