import { get_sodium } from "./js/load_sodium.js";
import { Keys, concatArr, Crypto } from "./js/crypto.js";


if(!sessionStorage.getItem("state")){
    sessionStorage.setItem("state", "mail_list");
}

const compose_button = document.getElementById("compose_button");
const compose = document.getElementById("compose");
const mail_list = document.getElementById("mail_list");
const main = document.getElementById("main");

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

update_main();

let sodium = await get_sodium();

