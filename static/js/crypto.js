export class Crypto {

    static KEY_LENGTH = 32;
    static NONCE_LENGTH = 12;

    static FUNNY_WORD2 = "When life gives you lemons, DONT MAKE LEMONADE! Make life take the lemons back! GET MAD! I DONT WANT YOUR DAMM LEMONS! Demand to see lifes manager!";
    static FUNNY_WORD3 = "Study's show that keeping a ladder inside is more dangerous than a loaded gun... Thats why I have 10 guns; in case some MANIAC trys to sneak in with a ladder!";

    static CLIENT = "Base Better Mail Client v0.0.1";
    static CHACHA_VERSION = "IETF";

    static async sha256(text){
        const msgBuffer = new TextEncoder('utf-8').encode(text);
        const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
        return this.buf2hex(hashBuffer);
    }
    static buf2hex(buffer) {
      return [...new Uint8Array(buffer)]
          .map(x => x.toString(16).padStart(2, '0'))
          .join('');
    }
    static async check(challenge, nonce){
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
    static async findChallengeNonce(challenge){
        let worker = new Worker('js/workers/findChallengeNonce.js');

        let pass = {
            challenge: challenge,
            compare: '0000',
        };

        const start = Date.now();

        worker.postMessage(pass);

        return new Promise((resolve, reject) => {
            worker.onmessage = function(e){
                let end = (Date.now() - start) / 1000;
                resolve(`nonce: ${e.data}, time: ${end}`);
            }
        });
        
    }
    static createAdditionDataMail(nonce, targetX25519PublicKey, tempPublicX25519Key, challengeNonce){
        return concatArr(nonce, targetX25519PublicKey, tempPublicX25519Key, challengeNonce);
    }

    constructor(sodium, keys){
        this.sodium = sodium;
        this.keys = keys;
    }
    getTransciptHash(message, targetX25519PublicKey){
        return this.sodium.crypto_hash_sha256(message + to_hex(targetX25519PublicKey) + FUNNY_WORD2);
    }
    async encryptMessage(message, targetX25519PublicKey){

        const tempSecretX25519Key = this.sodium.randombytes_buf(KEY_LENGTH);
        const tempPublicX25519Key = this.sodium.crypto_scalarmult_base(tempSecretX25519Key);

        const sharedSecret = this.sodium.crypto_scalarmult(tempSecretX25519Key, targetX25519PublicKey);

        const transciptHash = this.getTransciptHash(message, targetX25519PublicKey);
        let challengeNonce = Crypto.findChallengeNonce(transciptHash);
        const signatureBase64 = this.sodium.crypto_sign_detached(transciptHash, this.keys.getSecrectSigningKey(), 'base64');

        const nonce = this.sodium.randombytes_buf(NONCE_LENGTH);



        const fullMessage = {
            signature: signatureBase64,
            from: {
                X25519: this.keys.getPublicX25519Key(),
                signing: this.keys.getSecrectSigningKey()
            },
            metadata: {
                client: CLIENT
            },
            message: message
        };

        const fullMessageString = JSON.stringify(fullMessage);
        
        challengeNonce = await challengeNonce;
        const additonalData = Crypto.createAdditionDataMail(nonce, targetX25519PublicKey, tempPublicX25519Key, challengeNonce);

        const encryptedMessage = this.sodium.crypto_aead_chacha20poly1305_ietf_encrypt(fullMessageString, additonalData, null, nonce, sharedSecret);

        return {
            nonce: nonce,
            tempPublicX25519Key: publicX25519Key,
            challengeNonce: challengeNonce,
            encryptedMessage: concatArr(encryptedMessage),
        };
    }
    async decryptMessage(out){
        const sharedSecret = this.sodium.crypto_scalarmult(this.keys.getSecrectX25519Key(), out.tempPublicX25519Key);

        const additonalData = createAdditionDataMail(out.nonce, this.keys.getPublicX25519Key(), out.tempPublicX25519Key, out.challengeNonce);

        const decryptedMessgae = this.sodium.crypto_aead_chacha20poly1305_ietf_decrypt(null, out.encryptMessage, additonalData, out.nonce, sharedSecret);

        const fullMessage = JSON.parse(decryptedMessgae);

        const senderPublicSigningKey = fullMessage.from.signing;

        const transciptHash = this.getTransciptHash(fullMessage.message, this.keys.getPublicX25519Key);

        const verifyChallenge = await Crypto.check(transciptHash, out.challengeNonce);
        const verifySignature = this.sodium.crypto_sign_verify_detached(this.sodium.from_base64(fullMessage.signature), transciptHash, senderPublicSigningKey);
        if(!verifyChallenge || !verifySignature){
            return null;
        }

        return fullMessage;
    }
}
export class Keys {
    //argon2 constants
    static OPS_LIMIT = 1;
    static MEM_LIMIT = 1 * 1024 * 1024 * 1024;

    static FUNNY_WORD1 = "Who do you see? Who do you think your talking to? Someone opens the door and gets shot, you think that of me? I am not in danger. I am the danger! I AM THE ONE WHO KNOCKS!";

    static computePassword(sodium, password, username){
        const usernameSalt = sodium.crypto_generichash(sodium.crypto_pwhash_SALTBYTES, username + this.FUNNY_WORD1);
        // why is my phone 7 times faster than my desktop?
        return sodium.crypto_pwhash(crypto.KEY_LENGTH, password, usernameSalt, Keys.OPS_LIMIT, Keys.MEM_LIMIT, sodium.crypto_pwhash_ALG_DEFAULT);
    }

    constructor(publicSigningKey, secretSigningKey, publicX25519Key, secretX25519Key){
        this.publicSigningKey = publicSigningKey;
        this.secretSigningKey = secretSigningKey;
        this.publicX25519Key = publicX25519Key;
        this.secretX25519Key = secretX25519Key;
    }
    getPublicSigningKey(){
        return this.publicSigningKey;
    }
    getSecrectSigningKey(){
        return this.secretSigningKey;
    }
    getPublicX25519Key(){
        return this.publicX25519Key;
    }
    getSecrectX25519Key(){
        return this.secretX25519Key;
    }
}

export function concatArr(...arrays) {
  const totalLength = arrays.reduce((sum, arr) => sum + arr.length, 0);
  const result = new Uint8Array(totalLength);
  let offset = 0;
  for (const arr of arrays) {
    result.set(arr, offset);
    offset += arr.length;
  }  
  return result;
}