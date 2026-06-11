import { get_sodium } from "/js/load_sodium.js";
import * as c from "/js/crypto.js";
import { login, checkLogin } from "/js/login.js";

const loginDOM = {
    button: document.getElementById("login-button"),
    username: document.getElementById("login-username"),
    password: document.getElementById("login-password"),
    message: document.getElementById("login-message")
};
const signupDOM = {
    button: document.getElementById("signup-button"),
    username: document.getElementById("signup-username"),
    password: document.getElementById("signup-password"),
    message: document.getElementById("signup-message")
};

const sodium = await get_sodium();

console.log(sodium.from_base64(""))

loginDOM.button.addEventListener('click', async (event)=>{
    loginDOM.button.innerText = 'Loading...';
    const timeout = setTimeout(()=>{
        loginDOM.button.innerText = 'Try again.';
        loginDOM.message.innerText = 'Sorry an error has occured. Please try again.';
    }, 5000);
    
    const username = loginDOM.username.value;
    const password = loginDOM.password.value;

    console.log(`usernmae ${username}, passoword ${password}`);

//    const baseKey = c.Keys.computePassword(username, password);
    try {
        login(username, password);
    } catch(error){
        loginDOM.message.innerText = '): something unexpected happened... please try again.';
        console.log("error from login", error);
    }
   clearTimeout(timeout);
});

signupDOM.button.addEventListener('click', async (event)=>{
    signupDOM.button.innerText = 'Loading...';
    const timeout = setTimeout(()=>{
        signupDOM.button.innerText = 'Try again.';
        signupDOM.message.innerText = 'Sorry an error has occured. Please try again.';
    }, 5000);
    const username = signupDOM.username.value.toLowerCase();
    const password = signupDOM.password.value;
    try{
    const baseKey = c.Keys.computePassword(username, password);

    const keys = c.Keys.generateKeys(sodium);

    const nonce = sodium.randombytes_buf(c.Crypto.NONCE_LENGTH);

    const vault = new c.Vault(keys.getSecretX25519Key(), keys.getSecretSigningKey(), null, null);

    const vaultKey = c.Crypto.generateVaultKey(sodium, await baseKey);

    let out = vault.encodeVault(sodium, vaultKey, keys);
    out.username = username;
    // console.log(out);

    await fetch("/emapi/v1/signup",{
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(out),
    });
    
    checkLogin(out, username, password);
    } catch(error){
        console.log("error from signup",error);
        signupDOM.message.innerText = 'Somthing bad happened while trying to signup. please try again.';
    }

    clearTimeout(timeout);
});