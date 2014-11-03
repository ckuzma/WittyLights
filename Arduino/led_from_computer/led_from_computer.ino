// Example serial number send: 0000010010001 (length = 13)
// Corrected-length serial number: 00000100100 (length = 11)

#include <Wire.h>

// Indicate pins that LEDs are attached to
int led1 = 2;
int led2 = 3;
int led3 = 4;
int led4 = 5;
int led5 = 6;
int led6 = 7;
int led7 = 8;
int led8 = 9;
int led9 = 10;
int led10 = 11;
int led11 = 12;

void ledOn(int, int);
void ledOff(int, int);

// Board setup routine
void setup()  { 
  Serial.begin(9600);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  pinMode(led4, OUTPUT);
  pinMode(led5, OUTPUT);
  pinMode(led6, OUTPUT);
  pinMode(led7, OUTPUT);
  pinMode(led8, OUTPUT);
  pinMode(led9, OUTPUT);
  pinMode(led10, OUTPUT);
  pinMode(led11, OUTPUT);

}  

 
void loop(){
  String readString = "";
  while (Serial.available()) {
    delay(3);  //delay to allow buffer to fill 
    if (Serial.available() >0) {
      char c = Serial.read();  //gets one byte from serial buffer
      readString += c; //makes the string readString
    } 
  }
  //delay(2000);
  if(readString.length() > 0){
    Serial.println(readString);
  }
  updateLights(readString);
}

void updateLights(String inbound){
  for(int x = 0; x < inbound.length(); x++){
    int powerValue = inbound[x] - '0';
    if (powerValue == 1){
      ledOn(x);
    }
    if (powerValue == 0){
      ledOff(x);
    }
  }
}

void ledOn(int number){
  digitalWrite(number, HIGH);
}

void ledOff(int number){
  digitalWrite(number, LOW);
}
