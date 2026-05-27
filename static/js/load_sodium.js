let sodium;

window.sodium = {
    onload: ((s)=>{
        console.log('sodium!');
        sodium = s;
    })
}

export async function get_sodium(){
    if(sodium){
        await sodium.ready;
        return sodium;
    }
    let count = 0;
    while(!sodium && count < 3 * 1000){
        await new Promise(res => setTimeout(res, 1));
        count++;
    }
    if(sodium){
        await sodium.ready;
        return sodium;
    }
    alert("Our cryptographic tools failed to load. sorry ):");
    console.log("libsodium did not load!!!!! PANIC!");
    location.reload();
    return null;
}