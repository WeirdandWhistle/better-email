self.onmessage = async (e) => {
    console.log("started nonce finding!");
    let challenge = e.data;
    let nonce = -1;
    let hash;

    console.log("e",challenge);
    let newArr = add(challenge, 1);
    console.log("newArr",newArr);
    // console.log("sha256",hash);
    while(1){
        break;
        nonce++;
        hash = await sha256(`${challenge}${nonce}`);
        // console.log(hash);
        if(hash.startsWith('0000')){
            break;
        }
    }

    // console.log(`nonce: ${nonce} hashes into: ${await sha256(`${challenge}${nonce}`)}`);

    this.postMessage(nonce);
}
// console.log('web worker loaded from worker?');

async function sha256(text){
    const msgBuffer = new TextEncoder('utf-8').encode(text);
    // console.log("msgBuffer",msgBuffer);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    // console.log("hashBuffer",hashBuffer);
    return buf2hex(hashBuffer);
}

function buf2hex(buffer) {
  return [...new Uint8Array(buffer)]
      .map(x => x.toString(16).padStart(2, '0'))
      .join('');
}