import { get_sodium } from "./js/load_sodium.js";
import * as c from "./js/crypto.js";
import { login, verify} from "/js/login.js";

let sodium = null;

if(!sessionStorage.getItem("state")){
    sessionStorage.setItem("state", "mail_list");
}

const compose_button = document.getElementById("compose_button");
const compose = document.getElementById("compose");
const mail_list = document.getElementById("mail_list");
const main = document.getElementById("main");
const sendButton = document.getElementById("send-button");
const mailMessage = document.getElementById("mail-message");
const toBox = document.getElementById("to");
const subjectBox = document.getElementById("subject");

compose_button.addEventListener("click", function(event){
    switch(sessionStorage.getItem("state")){
        case "mail_list":
            sessionStorage.setItem("state", "compose");
            break;
        case "compose":
            sessionStorage.setItem("state", "mail_list");
            break;
        default:
            sessionStorage.setItem("state", "mail_list");
    }
    update_main();    
});

function update_main(){
     switch(sessionStorage.getItem("state")){
        case "mail_list":
            compose_button.innerText = "Compose";
            mail_list.style.display = "flex";
            compose.style.display = "none";
            break;
        case "compose":
            compose_button.innerText = "View Mail";
            mail_list.style.display = "none";
            compose.style.display = "flex";
            break;
    }
}

function load_mail(){
    console.log("load mail!");
}

sendButton.addEventListener('click', async (event)=>{
    // load keys
    if(!sessionStorage.getItem('keys')){
        window.location.pathname = '/login/';
        return;
    }
    const keys = c.Keys.fromJSON(sodium, JSON.parse(sessionStorage.getItem('keys')));

    // who are we sending to?
    const toName = toBox.value;
    let res = await fetch(`/emapi/v1/user?username=${toName}`);
    let json = await res.json();
    if(json.status != 200){
        alert(json.error);
        throw new Error(json.error);
    }


    // compile message json
    const message = {
        subject: subjectBox.value,
        from: sessionStorage.getItem("username"),
        text: mailMessage.value,
        date: (Date.now()/1000),
    };

    const lock = new c.Crypto(sodium, keys);
    const out = await lock.encryptMessage(message, sodium.from_base64(json.X25519Key, sodium.sodium_base64_VARIANT_URLSAFE));
    console.log(out);

    const base64JSON = {
        nonce: sodium.to_base64(out.nonce, sodium.sodium_base64_VARIANT_URLSAFE),
        targetX25519PublicKey: sodium.to_base64(out.targetX25519PublicKey, sodium.sodium_base64_VARIANT_URLSAFE),
        tempPublicX25519Key: sodium.to_base64(out.tempPublicX25519Key, sodium.sodium_base64_VARIANT_URLSAFE),
        challengeNonce: sodium.to_base64(out.challengeNonce, sodium.sodium_base64_VARIANT_URLSAFE),
        encryptedMessage: sodium.to_base64(out.encryptedMessage, sodium.sodium_base64_VARIANT_URLSAFE),
    };

    res = await fetch("/emapi/v1/message",{
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify(base64JSON),
    });

    if(!res.ok){
        alert("Message failed to send. check logs");
        console.log((await res.json()).error);
    }
});

update_main();

sodium = await get_sodium();