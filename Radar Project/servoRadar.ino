#include <Servo.h>

Servo solSagServo;  //Servo nesnesi
int servoKonumu;   //servo konumunu veren değişken
const int okumaSayisi = 10; //sensör okuma sayısı
int index = 0;   //güncel okumanın indeksi
int toplam = 0;  //bütün okunan değerlerin toplamı
int ortalama = 0;  //bütün okunan değerlerin ortalaması
int echoPin = 2;   
int triggerPin = 3; 
unsigned long pulseZaman = 0; //palsı okumak için geçen zaman
unsigned long uzaklik = 0;

void setup() {
   solSagServo.attach(9);
   //HIGH cikislari verilecegi icin output olarak ayarlandı
   pinMode(triggerPin, OUTPUT);
   //HIGH inputları alinacagi icin icin output olarak ayarlandı
   pinMode(echoPin, INPUT);
   //seri iletişimi başlat
   Serial.begin(9600);
}

void loop() {
  for(servoKonumu = 0; servoKonumu < 180; servoKonumu++) {  //servo soldan sağa hareket ediyor
    solSagServo.write(servoKonumu);
    for(index = 0; index <=okumaSayisi; index++) {
      digitalWrite(triggerPin, LOW);
      delayMicroseconds(50);
      digitalWrite(triggerPin, HIGH); //ses dalgası gönderidi
      delayMicroseconds(50);
      digitalWrite(triggerPin, LOW);      //ses dalgasını kapat
      pulseZaman = pulseIn(echoPin, HIGH);   //sinyalin dönmesi için geçen zamanı hesapla
      uzaklik = pulseZaman/58;        //cm'ye çevir
      toplam = toplam + uzaklik;      //toplamı güncelle
      delay(10);
  }
  
  ortalama = toplam / okumaSayisi;
  if(index >= okumaSayisi) {
    index = 0;
    toplam = 0;
  }
  
  Serial.print("X");                // derece değerinden önceki önek
  Serial.print(servoKonumu);       // güncel servo konumu
  Serial.print("V");                // değerleri ayırmak için kullanılan ek
  Serial.println(ortalama);          // sensör okumalarının ortalaması
  }
  
  //servo sağdan sola hareket ediyor
   for(servoKonumu = 180; servoKonumu > 0; servoKonumu--) {
     solSagServo.write(servoKonumu);
    for(index = 0; index <=okumaSayisi; index++) {
      digitalWrite(triggerPin, LOW);
      delayMicroseconds(50);
      digitalWrite(triggerPin, HIGH); //ses dalgası gönderidi
      delayMicroseconds(50);
      digitalWrite(triggerPin, LOW);      //ses dalgasını kapat
      pulseZaman = pulseIn(echoPin, HIGH);   //sinyalin dönmesi için geçen zamanı hesapla
      uzaklik = pulseZaman/58;        //cm'ye çevir
      toplam = toplam + uzaklik;      //toplamı güncelle
      delay(10);
  }
  
  ortalama = toplam / okumaSayisi;
  if(index >= okumaSayisi) {
    index = 0;
    toplam = 0;
  }
  
  Serial.print("X");                 // derece değerinden önceki önek
  Serial.print(servoKonumu);        // güncel servo konumu
  Serial.print("V");               // değerleri ayırmak için kullanılan ek
  Serial.println(ortalama);       // sensör okumalarının ortalaması
  }
 }
 
