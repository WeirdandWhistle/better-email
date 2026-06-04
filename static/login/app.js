import { get_sodium } from "/js/load_sodium.js";
import * as c from "/js/crypto.js"

const login = {
    button: document.getElementById("login-button"),
    username: document.getElementById("login-username"),
    password: document.getElementById("login-password"),
    message: document.getElementById("login-message")
};
const signup = {
    button: document.getElementById("signup-button"),
    username: document.getElementById("signup-username"),
    password: document.getElementById("signup-password"),
    message: document.getElementById("signup-message")
};

const sodium = await get_sodium();

login.button.addEventListener('click', async (event)=>{
    const username = login.username.value;
    const password = login.password.value;

    console.log(`usernmae ${username}, passoword ${password}`);

   const baseKey = c.Keys.computePassword(username, password);

   
});

signup.button.addEventListener('click', async (event)=>{
    const username = signup.username.value.toLowerCase();
    const password = signup.password.value;

    const baseKey = c.Keys.computePassword(username, password);

    const keys = c.Keys.generateKeys(sodium);

    const nonce = sodium.randombytes_buf(c.Crypto.NONCE_LENGTH);

    const vault = new c.Vault(keys.getSecretX25519Key(), keys.getSecretSigningKey(), null, null);

    const vaultKey = c.Crypto.generateVaultKey(sodium, await baseKey);

    const secureVault = vault.encodeVault(sodium, vaultKey, keys);

    const out = {
        signingKey: sodium.to_base64(keys.getPublicSigningKey(),sodium.sodium_base64_VARIANT_URLSAFE),
        X25519Key: sodium.to_base64(keys.getPublicX25519Key(),sodium.sodium_base64_VARIANT_URLSAFE),
        nonce: sodium.to_base64(nonce, sodium.sodium_base64_VARIANT_URLSAFE),
        vault: secureVault,
        username: username,
    };
    // console.log(out);

    fetch("/emapi/v1/signup",{
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(out),
    });
});