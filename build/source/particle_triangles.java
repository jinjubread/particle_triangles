import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class particle_triangles extends PApplet {



ArrayList particles;

int pointNumber = 40;
int trim = 20, offset = 20, distance = 200;

float randomBoundary, randomBoundaryHeight;
float alphaModulate, n;

float midPointX, midPointY;
float lineA, lineB;

// Change to true if you want to capture every frame for gif animation.
boolean captureFrame = false;

public void setup() {
  
  
  
  colorMode(HSB,360,100,100,1);
  background(0, 0, 100, 1);

  // let's make a random point!
  particles = new ArrayList();
  randomBoundary = width - trim*2;
  randomBoundaryHeight = height - trim*2;

  // generating points.
  for(int i = 0; i < pointNumber; i++){
    Particle particle = new Particle();
    particles.add(particle);
  }
}

public void draw() {
  fill(0xff080B1A, 1);
  noStroke();
  rect(0, 0, width, height);

  // \ubb54\uac00 \uc0bc\uac01\ud615\uc744 \ub9cc\ub4e4\ub824\uba74 \uc774\ub807\uac8c \ud574\uc57c\ud558\ub294 \uac78\uae4c...

  for(int i = 0; i < particles.size(); i++){
    pushMatrix();

    Particle particle1 = (Particle) particles.get(i);
    // particle1.update();

    ellipseMode(CENTER);

    for(int j = i + 1; j < particles.size(); j++){
      Particle particle2 = (Particle) particles.get(j);
      // particle2.update();

      if (dist(particle1.x, particle1.y, particle2.x, particle2.y) < distance) {
        fill(particle2.c);

        // make triangle
        for (int k = i + 2; k < particles.size(); k++ ) {
          Particle particle3 = (Particle) particles.get(k);
          particle1.display();
          particle2.display();
          particle3.display();

          // in the middle of the 3 points
          midPointX = (particle1.x + particle2.x + particle3.x) / 3;
          midPointY = (particle1.y + particle2.y + particle3.y) / 3;

          // \ube44\ub840\ub97c \uad6c\ud574\ubcf4\uc790..
          lineA = dist(particle2.x, particle2.y, particle3.x, particle3.y);
          lineB = dist((particle2.x + particle3.x)/2, (particle2.y + particle3.y)/2, particle1.x, particle1.y);
          alphaModulate = map((lineB), distance, distance + offset, 1, 0);

          if(lineA <= distance && lineB <= distance){
            // \uc810\uc774 \uc14b\ub2e4 \uc0ac\uc815\uac70\ub9ac\uc77c\ub54c.
            // alphaModulate = 1;
            if((lineA >= lineB) == true){
              alphaModulate = map((lineB), distance, distance + offset, 1, 0);
            } else {
              alphaModulate = map((lineA), distance, distance + offset, 1, 0);
            }
            particle1.update();
          } else if(lineA <= distance && lineB > distance) {
            // \ub458\uc740 \uac00\uae4c\uc6b4\ub370 \ud558\ub098\ub294 \uc544\ub2d0\ub54c 1 (fade-out)
            alphaModulate = map((lineB), distance, distance + offset, 1, 0);

          } else if(lineB <= distance && lineA > distance) {
            // \ub458\uc740 \uac00\uae4c\uc6b4\ub370 \ud558\ub098\ub294 \uc544\ub2d0\ub54c 2 (fade-out)
            alphaModulate = map((lineA), distance, distance + offset, 1, 0);
            particle3.update();
            // \ubcc4\uac00\ub8e8 \uac19\uc740 \ub290\ub08c.
            pushStyle();
            fill(56, 47 - (particle2.randomiser / 2), 100, (particle1.randomiser)/100);
            ellipse(midPointX, midPointY, particle1.r, particle1.r);
            popStyle();
          } else {
            // \uc14b\ub2e4 \uba40\ub54c.
            alphaModulate = 0;
            particle2.update();
          }

          n = norm(alphaModulate, 0, 1);
          triangleGen(particle2.x, particle2.y, particle3.x, particle3.y, midPointX, midPointY, particle2.c, n);
          triangleGen(particle1.x, particle1.y, particle3.x, particle3.y, midPointX, midPointY, particle3.c, n);
          triangleGen(particle1.x, particle1.y, particle2.x, particle2.y, midPointX, midPointY, particle1.c, n);


        }
      }
    }
    popMatrix();
  }
  if(captureFrame == true){
    saveFrame("gifVersion/particles-###.png");
  }
}

public void triangleGen(float x1, float y1, float x2, float y2, float x3, float y3, int c, float colorOpacity){
  pushStyle();
  fill(c, colorOpacity);
  triangle(x1, y1, x2, y2, x3, y3);
  popStyle();
}

// \uc0c9\uc744...\uc368\ubcf4\uc544\uc694...
public int colorBlended(float fract, float h, float s, float b, float h2, float s2, float b2, float a){
  h2 = (h2 - h);
  s2 = (s2 - s);
  b2 = (b2 - b);
  return color(h + h2 * fract, s + s2 * fract, b + b2 * fract, a);
}

class Particle {
  float x, y, r;
  float z;
  float randomiser;
  int c;
  int i = 1, j = 1;

  Particle(){
    x = random(trim, randomBoundary);
    y = random(trim, randomBoundaryHeight);
    r = random(1, 3);

    randomiser = random(100);
    colorMode(HSB,360,100,100,1);

    if(randomiser > 70) c = colorBlended(random(1), 178, 37, 92, 171, 68, 72, 0.8f);
    else if(randomiser > 30) c = colorBlended(random(1), 188, 37, 92, 171, 68, 72, 0.8f);
    else c = colorBlended(random(1), 161, 39, 94, 199, 96, 60, 0.8f);
  }

  public void display(){
    pushStyle();
    noStroke();
    fill(c);
    ellipse(x, y, 3, 3);
    popStyle();
  }

  public void update()
  {
    // animating logic (\ucc98\uc74c\uc5d4 \uc815\ubc29\ud5a5\uc774\ub77c\uc11c \uc7ac\ubbf8\uac00 \uc5c6\ub2e4!!!)
    x = x + j*0.02f;
    y = y + i*0.02f;

    if (y > randomBoundaryHeight - r) {
      i =- 1;
      // \ud30c\ub780\uacc4\uc5f4
      c = colorBlended(random(1), 195, 80, 50, 210, 96, 100, 0.8f);

    }
    if (y < trim + r) {
      i = 1;
      // \uc0ac\uc774\uc548 \uacc4\uc5f4
      c = colorBlended(random(1), 190, 40, 75, 199, 96, 95, 0.8f);

    }

    if (x > randomBoundary - r){
      j =- 1;
      c = colorBlended(random(1), 181, 80, 75, 199, 96, 70, 0.8f);

    }

    if (x < trim + r) {
      j = 1;
      // \ub179\uc0c9\uacc4??
      c = colorBlended(random(1), 171, 50, 94, 199, 96, 40, 0.8f);
    }
  }
}

public void keyPressed(){
  println("SAVED");
  saveFrame("capture-###@2x.png");
}
  public void settings() {  size(1167, 600, OPENGL);  smooth(8);  pixelDensity(displayDensity()); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "particle_triangles" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
