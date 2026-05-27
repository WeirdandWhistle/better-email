import { get_sodium } from "./load_sodium.js";
import { Keys, concatArr, crypto } from "./crypto.js";

let state = "compose";

const compose_button = document.getElementById("compose_button");
const compose = document.getElementById("compose");
const mail_list = document.getElementById("mail_list");
const main = document.getElementById("main");

compose_button.addEventListener("click", function(event){
    switch(state){
        case "mail_list":
            state = "compose";
            break;
        case "compose":
            state = "mail_list";
            break;
    }
    update_main();    
});

function update_main(){
     switch(state){
        case "mail_list":
            compose_button.innerText = "View Mail";
            mail_list.style.display = "flex";
            compose.style.display = "none";
            break;
        case "compose":
            compose_button.innerText = "Compose";     
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

console.log("sodum is here!", sodium);