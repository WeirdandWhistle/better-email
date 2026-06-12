import * as c from "/js/crypto.js";
import { get_sodium } from "/js/load_sodium.js";

const sodium = await get_sodium();

export async function login(username, password){
    let baseKey = c.Keys.computePassword(username, password);
    const res = await fetch(`/emapi/v1/signup?username=${username}`);

    baseKey = await baseKey;
    const vaultKey = c.Crypto.generateVaultKey(sodium, baseKey);

    const resJson = await res.json();
    if(resJson.status !== 200){
        if(resJson.status >= 500){
            throw new Error("Server is not ok.");
        }
        throw new Error(resJson.error);
    }
    const vault = c.Vault.decodeVault(sodium, vaultKey, resJson);

    if(!sessionStorage.getItem("vault")){
        sessionStorage.setItem("vault", vault.getJSONText(sodium));
    }
    if(!sessionStorage.getItem("keys")){
        const keys =  {
            publicSigningKey: resJson.publicSigningKey,
            secretSigningKey: sodium.to_base64(vault.getSecretSigningKey(), sodium.sodium_base64_VARIANT_URLSAFE),
            publicX25519Key: resJson.publicX25519Key,
            secretX25519Key: sodium.to_base64(vault.getSecretX25519Key(), sodium.sodium_base64_VARIANT_URLSAFE)
        };
        sessionStorage.setItem('keys', JSON.stringify(keys));

    }
    sessionStorage.setItem("loggedIn", true);

    return vault;
}
export async function checkLogin(json, username, password){
    const vault = await login(username, password);

    const good = true;
    if(json.other && json.other !== vault.other) good = false;
    if(json.verifyedPeople && json.verifyedPeople !== vault.verifyedPeople) good = false;

    if(!good){
        // Vault on server is not the same as local. SERVER CAN NOT BE TRUSTED! or some caching error. try again.
        sessionStorage.setItem("logginIn", false);
        throw new Error("Server is doing weird things. Please try again.");
    }
    return vault;
}
export async function verify(UUID){
    const hex = sodium.randombytes_buf(16);
    let out = {
        hex: sodium.to_hex(hex),
        UUID: UUID,
    };

    const res = await fetch("/emapi/v1/verify",{
        method: 'POST',
        body: JSON.stringify(out),
        headers: {
            'Content-Type' : 'application/json',
        },
    });

    const json = await res.json();
    // console.log('verify json',json);
    if(json.status != 200){
        throw new Error("Server request is not ok.");
    }
    out = {
        value: sodium.crypto_hash_sha256(c.concatArr(hex, sodium.from_hex(json.rand))),
        challenge: json.challenge,
    }
    
    return out;
}

if(!sessionStorage.getItem("loggedIn")){
    try {
        const page = window.location.pathname.split("/")[1];
        console.log('page',window.location.pathname.split("/"));
        if(page != 'login'){
            window.location.href = '/login/';
        }
    } catch (error) {
        
    }    
}