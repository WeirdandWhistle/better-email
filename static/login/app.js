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


console.log(get_sodium());
const sodium = await get_sodium();

login.button.addEventListener('click', async (event)=>{
    const username = login.username.value;
    const password = login.password.value;

    console.log(`usernmae ${username}, passoword ${password}`);

   console.log(await c.Keys.computePassword(username, password));

});