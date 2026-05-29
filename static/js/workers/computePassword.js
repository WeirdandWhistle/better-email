importScripts('/js/sodium.js');

onmessage = async (message) => {
    await sodium.ready;
    console.log("from computePassword worker:", message.data);
    console.log(sodium.ready);
    
    const d = message.data;

    const username = d.username;
    const password = d.password;
    const OPS_LIMIT = d.OPS_LIMIT;
    const MEM_LIMIT = d.MEM_LIMIT;
    const KEY_LENGTH = d.KEY_LENGTH;
    const FUNNY_WORD1 = d.FUNNY_WORD1;

    const usernameSalt = sodium.crypto_generichash(sodium.crypto_pwhash_SALTBYTES, username + FUNNY_WORD1);
    // why is my phone 7 times faster than my desktop?
    const out = sodium.crypto_pwhash(KEY_LENGTH, password, usernameSalt, OPS_LIMIT, MEM_LIMIT, sodium.crypto_pwhash_ALG_DEFAULT);
    postMessage(out);
}