import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import processing.pdf.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class particle_triangles extends PApplet {





/* \ubaa9\ud45c
6. \uc6c0\uc9c1\uc784\uc744 \ubd80\ub4dc\ub7fd\uac8c...
7. \uc120\uc758 \uac2f\uc218\ub97c \ubc18\ubcf5\ub418\uc9c0 \uc54a\ub3c4\ub85d \uc904\uc5ec\ubcf8\ub2e4.
8. z \ucd95\uc758 \uac1c\ub150\uc744 \ub3c4\uc785\ud574\uc11c (\uac00\uc6b4\ub370 \uc810?) \ubcfc\ub85d \ud558\uac8c \ub9cc\ub4e4\uc5b4\ubcf8\ub2e4!
*/

PVector midPoint;

// ArrayList midPointsX;
// ArrayList midPointsY;
ArrayList particles;

PVector vP1, vP2, vP3, cP1;

int pointNumber;
int trim;
int[] style;
int distance = 160;

float randomBoundary;
float randomBoundaryHeight;

float midPointX, midPointY;
float areaX, areaY, areaTotal;


public void setup() {
  
  
  
  colorMode(HSB,360,100,100,1);
  background(0, 0, 100, 1);

  // let's make a random point!
  pointNumber = 24;
  particles = new ArrayList();

  trim = 40;
  randomBoundary = width - trim*2;
  randomBoundaryHeight = height - trim*2;


  // generating points.
  int index = 0;
  float randomiser;

  for(int i = 0; i < pointNumber; i++){
    Particle particle = new Particle();
    particles.add(particle);
  }

}

public void draw() {
  fill(0, 0, 100, 1);
  noStroke();
  rect(0, 0, width, height);

  int index = 0;
  float fading = 0.9f;
  // \ubb54\uac00 \uc0bc\uac01\ud615\uc744 \ub9cc\ub4e4\ub824\uba74 \uc774\ub807\uac8c \ud574\uc57c\ud558\ub294 \uac78\uae4c...

  for(int i = 0; i < particles.size(); i++){
    pushMatrix();

    Particle particle1 = (Particle) particles.get(i);
    particle1.display();
    particle1.update();

    ellipseMode(CENTER);

    for(int j = i + 1; j < particles.size(); j++){
      Particle particle2 = (Particle) particles.get(j);
      particle2.display();
      particle2.update();

      if (dist(particle1.x, particle1.y, particle2.x, particle2.y) < distance) {
        fill(particle2.c);

        // make triangle
        for (int k = i + 2; k < particles.size(); k++ ) {
          Particle particle3 = (Particle) particles.get(k);
          particle3.display();
          particle3.update();

          midPointX = (particle1.x + particle2.x + particle3.x) / 3;
          midPointY = (particle1.y + particle2.y + particle3.y) / 3;
          // vP2.add(vP1);

          // \ub113\uc774\ub85c \uac00\uc790...
          areaX = (particle1.x * particle2.y) + (particle2.x * particle3.y) + (particle3.x * particle1.y);
          areaY = (particle1.x * particle3.y) + (particle3.x * particle2.y) + (particle2.x * particle1.y);
          areaTotal = (areaX - areaY)/2;
          int checkArea = round(areaTotal);

          if(checkArea < 10000 && checkArea > 0) {
              if(dist(particle2.x, particle2.y, particle3.x, particle3.y) < distance){
                triangleGen(particle2.x, particle2.y, particle3.x, particle3.y, midPointX, midPointY, particle2.c, 1);
                triangleGen(particle1.x, particle1.y, particle3.x, particle3.y, midPointX, midPointY, particle3.c, 1);
                triangleGen(particle1.x, particle1.y, particle2.x, particle2.y, midPointX, midPointY, particle1.c, 1);
                particle2.update();
                particle3.update();
                particle1.update();
              }
          }

        }
      }
    }

    // triangle(particle1.x, particle1.y, particle2.x, particle2.y, particle3.x, particle3.y);
    popMatrix();
  }
}

public void styling(){
  int c;

  pushStyle();
  strokeWeight(1);
  // c = colorBlended(random(1), 190, 40, 75, 199, 96, 95, 0.8);
  // stroke(190, 60, 30, 0.2);
  // stroke(c);
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
    r = 4;

    // \uc0c9\uc740 \ub79c\ub364\uc73c\ub85c \ubf51\uc790..
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
    ellipse(x, y, r, r);
    popStyle();
  }

  public void triangleGen(Particle p2, Particle p3){
    pushStyle();
    noStroke();

    float x2 = p2.x;
    float x3 = p3.x;

    float y2 = p2.y;
    float y3 = p3.y;

    float midPointX = (x + x2 + x3) / 3;
    float midPointY = (y + y2 + y3) / 3;

    int c1 = c;
    int c2 = p2.c;
    int c3 = p3.c;

    fill(c1);
    triangle(x, y, x2, y2, midPointX, midPointY);

    fill(c2);
    triangle(x2, y2, x3, y3, midPointX, midPointY);

    fill(c3);
    triangle(x, y, x3, y3, midPointX, midPointY);

    popStyle();
  }

  public void update()
  {
    // animating logic (\ucc98\uc74c\uc5d4 \uc815\ubc29\ud5a5\uc774\ub77c\uc11c \uc7ac\ubbf8\uac00 \uc5c6\ub2e4!!!)
    x = x + j*0.02f;
    y = y + i*0.02f;

    if (y > randomBoundaryHeight - r) {
      i =- 1;
      rotateX(PI/3.0f * i);
      // \ud30c\ub780\uacc4\uc5f4
      c = colorBlended(random(1), 195, 80, 50, 210, 96, 100, 0.8f);

    }
    if (y < trim + r) {
      i = 1;
      rotateX(PI/3.0f * -i);
      // \uc0ac\uc774\uc548 \uacc4\uc5f4
      c = colorBlended(random(1), 190, 40, 75, 199, 96, 95, 0.8f);

    }

    if (x > randomBoundary - r){
      j =- 1;
      c = colorBlended(random(1), 181, 80, 75, 199, 96, 70, 0.8f);
      rotateY(PI/3.0f * j);

    }

    if (x < trim + r) {
      j = 1;
      // \ub179\uc0c9\uacc4??
      c = colorBlended(random(1), 171, 50, 94, 199, 96, 40, 0.8f);
      rotateY(PI/3.0f * -j);
    }

  }
}
  public void settings() {  size(800, 600, OPENGL);  smooth(8);  pixelDensity(displayDensity()); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "particle_triangles" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
