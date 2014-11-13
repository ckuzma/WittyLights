#include <Wire.h>



void ledOn(int, int);
void ledOff(int, int);
void partyLights();

// Board setup routine
void setup()  { 
  Serial.begin(9600);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);

}  

 
void loop(){
  String readString = "";
  while (Serial.available()) {
    delay(3);  //delay to allow buffer to fill 
    if (Serial.available() >0) {
      char c = Serial.read();  //gets one byte from serial buffer
      readString += c; //makes the string readString
      readString = readString;
    } 
  }
  Serial.println(readString);
  //delay(2000);
  if(readString.length() > 0){
    Serial.println(readString);
  }
  if(readString == "2"){
    partyLights();
  }
  else{
    updateLights(readString);
  }
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

void partyLights(){
  int y = 0;
  while (y < 5){
  int x = 0;
  while (x < 8){
    digitalWrite(x+2,HIGH);
    delay(50);
    digitalWrite(x+2,LOW);
    x+=1;
  }
  while (x > -1){
    digitalWrite(x+2,HIGH);
    delay(50);
    digitalWrite(x+2,LOW);
    x-=1;
  }
  y+=1;
  }
}

void ledOn(int number){
  digitalWrite(number+2, HIGH);
}

void ledOff(int number){
  digitalWrite(number+2, LOW);
}
