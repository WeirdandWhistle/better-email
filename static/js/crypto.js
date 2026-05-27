export class crypto {

    static KEY_LENGTH = 32;
    static NONCE_LENGTH = 12;

   

    constructor(sodium, hash, keys){
        this.sodium = sodium;
        this.hash = hash;
        this.keys = keys;
    }

    encryptMessage(message, targetX25519PublicKey){

        let tempSecretX25519Key = randombytes_buf(KEY_LENGTH);
        let tempPublicX25519Key = crypto_scalarmult_base(tempSecretX25519Key);

        let sharedSecret = crypto_scalarmult(tempSecretX25519Key, targetX25519PublicKey);

        let transciptHash = hash(message);


    }
}
export class Keys {
    //argon2 constants
    static OPS_LIMIT = 1;//
    static MEM_LIMIT = 1 * 1024 * 1024 * 1024;

    static computePassword(sodium, password, username){
        const usernameSalt = sodium.crypto_generichash(sodium.crypto_pwhash_SALTBYTES, username);   
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