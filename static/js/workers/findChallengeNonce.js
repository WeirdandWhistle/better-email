self.onmessage = async (e) => {
    // console.log("started nonce finding!");
    let challenge = e.data.challenge;
    let nonce = -1;
    let found = false;
    while(!found){
        nonce++;
        found = await check(challenge, nonce);
    }

    this.postMessage(nonce);
}
async function check(challenge, nonce){
    const text = `${challenge}${nonce}`;
    const msgBuffer = new TextEncoder('utf-8').encode(text);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    const hash =  [...new Uint8Array(hashBuffer)]
      .map(x => x.toString(16).padStart(2, '0'))
      .join('');
    if(hash.startsWith('0000')){
        return true;
    }
    return false;
}