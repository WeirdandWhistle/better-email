import { get_sodium } from "/js/load_sodium.js";
import * as c from "/js/crypto.js";
import { login, checkLogin, verify } from "/js/login.js";

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

    if(username.length === 0){
        clearTimeout(timeout);
        loginDOM.message.innerText = 'Username does not exist';
        loginDOM.button.innerText = 'Login';
        return;
    }

//    const baseKey = c.Keys.computePassword(username, password);
    try {
        await login(username, password);
        window.location.pathname  = "/";
    } catch({name, message}){
        console.log("caught the error!");
        loginDOM.message.innerText = message;
    }
   clearTimeout(timeout);
   loginDOM.button.innerText = 'Login';
   
});

signupDOM.button.addEventListener('click', async (event)=>{
    signupDOM.button.innerText = 'Loading...';
    const timeout = setTimeout(()=>{
        signupDOM.button.innerText = 'Try again.';
        signupDOM.message.innerText = 'Sorry an error has occured. Please try again.';
    }, 5000);
    const username = signupDOM.username.value.toLowerCase();
    const password = signupDOM.password.value;

    if(username.length === 0){
        clearTimeout(timeout);
        signupDOM.message.innerText = 'Username does not exist';
        signupDOM.button.innerText = 'Signup';
        return;
    }

    try{
    const baseKey = c.Keys.computePassword(username, password);

    const keys = c.Keys.generateKeys(sodium);

    const nonce = sodium.randombytes_buf(c.Crypto.NONCE_LENGTH);

    const vault = new c.Vault(keys.getSecretX25519Key(), keys.getSecretSigningKey(), null, null);

    const vaultKey = c.Crypto.generateVaultKey(sodium, await baseKey);

    let out = vault.encodeVault(sodium, vaultKey, keys);
    out.username = username;
    // console.log(out);

    let res = await fetch("/emapi/v1/signup",{
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(out),
    });
    let temp = (await res.json()); 
    if(temp.status != 200){
        throw new Error(temp.error);
    }
    
    await checkLogin(out, username, password);
    window.location.pathname  = "/";
    } catch(error){
        console.log("error from signup",error);
        signupDOM.message.innerText = error.message;
    }
    signupDOM.button.innerText = 'Signup';
    clearTimeout(timeout);

    
});