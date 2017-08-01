import processing.serial.*;  
Serial myPort;
float x,y;
int yaricap = 350;        //nesnelerin yaricapi
int w = 300;
int derece = 0;          //servo'nun derece cisinden konumu
int deger = 0;
int hareket = 0;
int[] yeniDeger = new int [181];
int[] eskiDeger = new int [181];
PFont myFont;
int radarUzaklik = 0;
int ilkCalisma = 0;

//arka planı ve seri bağlantıyı oluştur
void setup () {
  size(750,450);
  background(0);   //arkaplan siyah
  myFont = createFont("verdana", 12);
  textFont(myFont);
  //listedeki ilk uygun portu kullan(baundrate = 9600)
  myPort = new Serial(this, Serial.list()[0], 9600);
}

//ekranı çiz
void draw() {
  fill(0);      //çiziliecek şekillerin rengi siyah
  noStroke();   // çiziliecek şekillerin kenar çizgisi yok
  ellipse(yaricap, yaricap, 750, 750);  //elips çiz
  rectMode(CENTER);
  rect(350,402,800,100);   //dikdörtgen çiz(x, y, en, boy)
  //eğer servo en sağ konumda ise hareketi sağdan sola gerçekleştir
  if(derece >= 179) {
    hareket = 1;     //animasyonu sağdan sola gerçekleştir
  }
  //eğer servo 0 derecede ise animasyonu soldan sağa gerçekleştir
  if(derece <=1) {
    hareket = 0;
  }
  
  //radar taraması kur
  strokeWeight(7);
  if(hareket == 0) {  //soldan sağa gidiyorsan
      //renkleri gittikçe solan 20 doğru çiz
      for(int i = 0; i<=20; i++) {
        stroke(0, (10*i), 0);  //yeşilin her iterasyonda solan tonları(R,G,B)
        line(yaricap, yaricap, yaricap + cos(radians(derece +(180+i)))*w, yaricap + sin(radians(derece +(180+i)))*w);
        
      }
  }
    else {   //eğer sağdan sola gidiyorsan
        for (int i = 20; i >= 0; i--) {
        stroke(0, 200-(10*i), 0);
        line(yaricap, yaricap, yaricap + cos(radians(derece+(180+i)))*w, yaricap + sin(radians(derece+(180+i)))*w);
        }
    }
     
     /*sensörde okunan değerlerden şekil oluştur
     ((yaricap) + (servo konumunun kosinüsü)) noktaları oluşturmak için kullanılır
     tarama soldan başlayacağı için radyan açıya 180 eklenmiştir
     iterasyonda servo'nun her bir derecelik hareketinde i +1 artırılır 
     */
     noStroke();
     //ilk tarama
     fill(0, 50, 0);   
     beginShape();
       for(int i=0; i<180; i++) {  //dizideki her derece için
         x = yaricap + cos(radians((180+i)))*((eskiDeger[i]));  //noktanın x koordinatı
         y =  yaricap + sin(radians((180+i)))*((eskiDeger[i])); //noktanın y koordinatı
         vertex(x, y);   //köşeleri çiz
       }
      endShape();
      
      //ikinci tarama
      fill(0, 110, 0);
      beginShape();
        for(int i = 0; i < 180; i++) {
           x = yaricap + cos(radians((180+i)))*(yeniDeger[i]);
           y = yaricap + sin(radians((180+i)))*(yeniDeger[i]);
           vertex(x, y);
        }
        endShape();
        
        //ortalama
        fill(0,170,0);
        beginShape();
        for(int i = 0; i < 180; i++) {
          x = yaricap + cos(radians((180+i)))*((yeniDeger[i] + eskiDeger[i])/2); 
          y = yaricap + sin(radians((180+i)))*((yeniDeger[i] + eskiDeger[i])/2);
          vertex(x, y); 
        }
        endShape();
        
       //ilk 2 taramadan sonra hareket varsa kırmızı çemberle göster
       if(ilkCalisma >= 360) {
         stroke(150, 0, 0);
         strokeWeight(1);
         noFill();
         for (int i = 0; i < 180; i++) {
              if (eskiDeger[i] - yeniDeger[i] > 35 || yeniDeger[i] - eskiDeger[i] > 35) {
                x = yaricap + cos(radians((180+i)))*(yeniDeger[i]);
                y = yaricap + sin(radians((180+i)))*(yeniDeger[i]);
                ellipse(x, y, 10, 10);
              }
         }
       }
       
       //radar uzaklık halkalarını oluştur (50,100,..)
       for (int i = 0; i <=6; i++) {
         noFill();
         strokeWeight(1);
         stroke(0, 255-(30*i), 0);
         ellipse(yaricap, yaricap, (100*i), (100*i));
         fill(0, 100, 0);
         noStroke();
         text(Integer.toString(radarUzaklik + 50), 380, (305-radarUzaklik), 50, 50);
         radarUzaklik += 50;
       }
       
       radarUzaklik = 0;
       
       //her 30 derecede bir radar çizgisi oluştur
       for (int i = 0; i <= 6; i++) {
         strokeWeight(1);
         stroke(0, 55, 0);
         line(yaricap, yaricap, yaricap + cos(radians(180+(30*i)))*w, yaricap + sin(radians(180+(30*i)))*w);
         fill(0, 55, 0);
         noStroke();
         if (180+(30*i) >= 300) {
           text(Integer.toString(180+(30*i)), (yaricap+10) + cos(radians(180+(30*i)))*(w+10), (yaricap+10) + sin(radians(180+(30*i)))*(w+10), 25,50);  
         }
         else {
           text(Integer.toString(180+(30*i)), yaricap + cos(radians(180+(30*i)))*w, yaricap + sin(radians(180+(30*i)))*w, 60,40);
         }
       }
       
       //bilgi metinleri ve değerlerini yaz
       noStroke();
       fill(0);
       rect(350,402,800,100);
       fill(0, 100, 0);
       text("Derece: "+Integer.toString(derece), 100, 380, 100, 50);         
       text("Uzaklık: "+Integer.toString(deger), 100, 400, 100, 50);         // text(string, x, y, width, height)
       text("Radar ekranı Engin Bozkurt tarafından geliştirilmiştir", 540, 380, 250, 50);
       fill(0);
       rect(70,60,150,100);
       fill(0, 100, 0);
       text("Ekran Anahtarı:", 100, 50, 150, 50);
       fill(0,50,0);
       rect(30,53,10,10);
       text("İlk tarama", 115, 70, 150, 50);
       fill(0,110,0);
       rect(30,73,10,10);
       text("İkinci tarama", 115, 90, 150, 50);
       fill(0,170,0);
       rect(30,93,10,10);
       text("Ortalama", 115, 110, 150, 50);
       noFill();
       stroke(150,0,0);
       strokeWeight(1);
       ellipse(29, 113, 10, 10);
       fill(150,0,0);
       text("Hareket", 115, 130, 150, 50);
  }
  
  //seri port'dan değerleri al
  void serialEvent (Serial myPort) {
    String xString = myPort.readStringUntil('\n');  //yeni bir satıra kadar seri portu oku
    
    if(xString != null) {        //satırlar arasında data varsa
      xString = trim(xString);   //boşluk karakteri varsa çıkar
      String getKonum = xString.substring(1, xString.indexOf("V")); //servo'nun konumu
      String getUzaklik = xString.substring(xString.indexOf("V")+1, xString.length());  //sensörde okunan uzaklık
      derece = Integer.parseInt(getKonum);
      deger = Integer.parseInt(getUzaklik);
      eskiDeger[derece] = yeniDeger[derece];
      yeniDeger[derece] = deger;
      
      //ilk 2 taramaya izin veren sayaç
      ilkCalisma++;
      if(ilkCalisma > 360) {
        ilkCalisma = 360;   //değeri 360'da tut   
      }
    }
  }
  
