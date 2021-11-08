//
// Created by Aljaz Stajnko on 30/10/2021.
//

#include <iostream>
#include <vector>
#include <algorithm>
#include <jni.h>

using namespace std;

class VigenereCipher {
    string message, key;
public:
    string generateKey(string, string);
    string encodeText(string, string);
    string decodeText(string, string);
    int findTheSingleDigit(int);

};

string VigenereCipher::generateKey(string message, string key){
    int messageSize = message.size();

    if(key.size() <= messageSize){
        for (int i = 0; i< messageSize; i++)
        {
            if (key.size() == message.size())
                break;
            key.push_back(key[i]);
        }}
    else {
        key = key.substr(0,messageSize);
        cerr<< "\nYour key has been altered to the same lenght as the message.\n"<<endl;
    }
    return key;
}

string VigenereCipher::encodeText(string message, string key){

    vector<int> cipher_message_ASCII;
    int SUM;


    for (int i = 0; i < message.size(); i++){
        int intersection_text = message[i] - 33 + key[i];
        if(intersection_text > 126){
            intersection_text  = intersection_text - 94;
        }

        cipher_message_ASCII.push_back(intersection_text);
    }

    int MIN = *min_element(cipher_message_ASCII.begin(), cipher_message_ASCII.end());


    if( MIN > 9 ){

        SUM = findTheSingleDigit(MIN);
    }

    rotate(cipher_message_ASCII.begin(), cipher_message_ASCII.begin() + SUM, cipher_message_ASCII.end());

    string out_cipher_text(cipher_message_ASCII.begin(), cipher_message_ASCII.end());

    return out_cipher_text;
}

int VigenereCipher::findTheSingleDigit(int n) {
    int sum = 0;
    while(n > 0 || sum > 9) {
        if(n == 0) {
            n = sum;
            sum = 0;
        }
        sum += n % 10;
        n /= 10;
    }
    return sum;
}



string VigenereCipher::decodeText(string encodedMessage, string key){

    vector<int> encodedASCII;
    int shift_value;
    string output_text;

    for (int i = 0 ; i < encodedMessage.size(); i++){
        int x = encodedMessage[i] ;
        encodedASCII.push_back(x);
    }
    int min = *min_element(encodedASCII.begin(), encodedASCII.end());


    shift_value = findTheSingleDigit(min);

    rotate(encodedASCII.rbegin(),encodedASCII.rbegin()+shift_value,encodedASCII.rend());



    for (int i = 0 ; i < encodedMessage.size(); i++){

        int intersection_text = encodedASCII[i] + 33 - key[i];

        if( intersection_text <= 33){
            intersection_text  = intersection_text + 94;
        }

        if(intersection_text >= 126){
            intersection_text  = intersection_text - 94;
        }
        output_text.push_back(intersection_text);
    }

    return output_text;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_aljazs_nfcTagApp_ui_writeNfcTag_WriteActivity_generateKey(JNIEnv *env, jobject thiz,
                                                                   jstring message, jstring key) {
    VigenereCipher cipherV;

    string keyUTF = env->GetStringUTFChars(key, reinterpret_cast<jboolean *>(false));
    string messageUTF = env->GetStringUTFChars(message, reinterpret_cast<jboolean *>(false));
    string generatedKey =cipherV.generateKey(messageUTF,keyUTF);
    return env->NewStringUTF(generatedKey.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_aljazs_nfcTagApp_ui_writeNfcTag_WriteActivity_encodeMessage(JNIEnv *env, jobject thiz,
                                                                     jstring message, jstring key) {
    VigenereCipher test;
    string keyUTF = env->GetStringUTFChars(key, reinterpret_cast<jboolean *>(false));
    string messageUTF = env->GetStringUTFChars(message, reinterpret_cast<jboolean *>(false));
    string encodedMessage = test.encodeText(messageUTF,keyUTF);

    return env->NewStringUTF(encodedMessage.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_aljazs_nfcTagApp_ui_readNfcTag_ReadActivity_generateKey(JNIEnv *env, jobject thiz,
                                                                 jstring message, jstring key) {
    VigenereCipher cipherV;

    string keyUTF = env->GetStringUTFChars(key, reinterpret_cast<jboolean *>(false));
    string messageUTF = env->GetStringUTFChars(message, reinterpret_cast<jboolean *>(false));
    string generatedKey =cipherV.generateKey(messageUTF,keyUTF);
    return env->NewStringUTF(generatedKey.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_aljazs_nfcTagApp_ui_readNfcTag_ReadActivity_decodeMessage(JNIEnv *env, jobject thiz,
                                                                   jstring encoded_message,
                                                                   jstring key) {
    VigenereCipher cipherV;
    string keyUTF = env->GetStringUTFChars(key, reinterpret_cast<jboolean *>(false));
    string encodedMessageUTF = env->GetStringUTFChars(encoded_message, reinterpret_cast<jboolean *>(false));
    string decodedMessage = cipherV.decodeText(encodedMessageUTF,keyUTF);

    return env->NewStringUTF(decodedMessage.c_str());
}