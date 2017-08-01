import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class radarEkran extends PApplet {

  
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

//arka plan\u0131 ve seri ba\u011flant\u0131y\u0131 olu\u015ftur
public void setup () {
  size(750,450);
  background(0);   //arkaplan siyah
  myFont = createFont("verdana", 12);
  textFont(myFont);
  //listedeki ilk uygun portu kullan(baundrate = 9600)
  myPort = new Serial(this, Serial.list()[0], 9600);
}

//ekran\u0131 \u00e7iz
public void draw() {
  fill(0);      //\u00e7iziliecek \u015fekillerin rengi siyah
  noStroke();   // \u00e7iziliecek \u015fekillerin kenar \u00e7izgisi yok
  ellipse(yaricap, yaricap, 750, 750);  //elips \u00e7iz
  rectMode(CENTER);
  rect(350,402,800,100);   //dikd\u00f6rtgen \u00e7iz(x, y, en, boy)
  //e\u011fer servo en sa\u011f konumda ise hareketi sa\u011fdan sola ger\u00e7ekle\u015ftir
  if(derece >= 179) {
    hareket = 1;     //animasyonu sa\u011fdan sola ger\u00e7ekle\u015ftir
  }
  //e\u011fer servo 0 derecede ise animasyonu soldan sa\u011fa ger\u00e7ekle\u015ftir
  if(derece <=1) {
    hareket = 0;
  }
  
  //radar taramas\u0131 kur
  strokeWeight(7);
  if(hareket == 0) {  //soldan sa\u011fa gidiyorsan
      //renkleri gittik\u00e7e solan 20 do\u011fru \u00e7iz
      for(int i = 0; i<=20; i++) {
        stroke(0, (10*i), 0);  //ye\u015filin her iterasyonda solan tonlar\u0131(R,G,B)
        line(yaricap, yaricap, yaricap + cos(radians(derece +(180+i)))*w, yaricap + sin(radians(derece +(180+i)))*w);
        
      }
  }
    else {   //e\u011fer sa\u011fdan sola gidiyorsan
        for (int i = 20; i >= 0; i--) {
        stroke(0, 200-(10*i), 0);
        line(yaricap, yaricap, yaricap + cos(radians(derece+(180+i)))*w, yaricap + sin(radians(derece+(180+i)))*w);
        }
    }
     
     /*sens\u00f6rde okunan de\u011ferlerden \u015fekil olu\u015ftur
     ((yaricap) + (servo konumunun kosin\u00fcs\u00fc)) noktalar\u0131 olu\u015fturmak i\u00e7in kullan\u0131l\u0131r
     tarama soldan ba\u015flayaca\u011f\u0131 i\u00e7in radyan a\u00e7\u0131ya 180 eklenmi\u015ftir
     iterasyonda servo'nun her bir derecelik hareketinde i +1 art\u0131r\u0131l\u0131r 
     */
     noStroke();
     //ilk tarama
     fill(0, 50, 0);   
     beginShape();
       for(int i=0; i<180; i++) {  //dizideki her derece i\u00e7in
         x = yaricap + cos(radians((180+i)))*((eskiDeger[i]));  //noktan\u0131n x koordinat\u0131
         y =  yaricap + sin(radians((180+i)))*((eskiDeger[i])); //noktan\u0131n y koordinat\u0131
         vertex(x, y);   //k\u00f6\u015feleri \u00e7iz
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
        
       //ilk 2 taramadan sonra hareket varsa k\u0131rm\u0131z\u0131 \u00e7emberle g\u00f6ster
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
       
       //radar uzakl\u0131k halkalar\u0131n\u0131 olu\u015ftur (50,100,..)
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
       
       //her 30 derecede bir radar \u00e7izgisi olu\u015ftur
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
       
       //bilgi metinleri ve de\u011ferlerini yaz
       noStroke();
       fill(0);
       rect(350,402,800,100);
       fill(0, 100, 0);
       text("Derece: "+Integer.toString(derece), 100, 380, 100, 50);         
       text("Uzakl\u0131k: "+Integer.toString(deger), 100, 400, 100, 50);         // text(string, x, y, width, height)
       text("Radar ekran\u0131 Engin Bozkurt taraf\u0131ndan geli\u015ftirilmi\u015ftir", 540, 380, 250, 50);
       fill(0);
       rect(70,60,150,100);
       fill(0, 100, 0);
       text("Ekran Anahtar\u0131:", 100, 50, 150, 50);
       fill(0,50,0);
       rect(30,53,10,10);
       text("\u0130lk tarama", 115, 70, 150, 50);
       fill(0,110,0);
       rect(30,73,10,10);
       text("\u0130kinci tarama", 115, 90, 150, 50);
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
  
  //seri port'dan de\u011ferleri al
  public void serialEvent (Serial myPort) {
    String xString = myPort.readStringUntil('\n');  //yeni bir sat\u0131ra kadar seri portu oku
    
    if(xString != null) {        //sat\u0131rlar aras\u0131nda data varsa
      xString = trim(xString);   //bo\u015fluk karakteri varsa \u00e7\u0131kar
      String getKonum = xString.substring(1, xString.indexOf("V")); //servo'nun konumu
      String getUzaklik = xString.substring(xString.indexOf("V")+1, xString.length());  //sens\u00f6rde okunan uzakl\u0131k
      derece = Integer.parseInt(getKonum);
      deger = Integer.parseInt(getUzaklik);
      eskiDeger[derece] = yeniDeger[derece];
      yeniDeger[derece] = deger;
      
      //ilk 2 taramaya izin veren saya\u00e7
      ilkCalisma++;
      if(ilkCalisma > 360) {
        ilkCalisma = 360;   //de\u011feri 360'da tut   
      }
    }
  }
  
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "radarEkran" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
