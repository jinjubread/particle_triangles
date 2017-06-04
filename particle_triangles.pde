import processing.opengl.*;
import processing.pdf.*;


/* 목표
6. 움직임을 부드럽게...
7. 선의 갯수를 반복되지 않도록 줄여본다.
8. z 축의 개념을 도입해서 (가운데 점?) 볼록 하게 만들어본다!
*/

PVector midPoint;

ArrayList particles;

int pointNumber;
int trim, offset;
int distance = 100;

float randomBoundary;
float randomBoundaryHeight;

float midPointX, midPointY;
float lineA, lineB;
float lineRatio = 3.0;

float alphaModulate;


void setup() {
  size(375, 667, OPENGL);
  pixelDensity(displayDensity());
  smooth(8);
  colorMode(HSB,360,100,100,1);
  background(0, 0, 100, 1);

  // let's make a random point!
  pointNumber = 24;
  particles = new ArrayList();

  trim = 20;
  offset = 40;

  randomBoundary = width - trim*2;
  randomBoundaryHeight = height - trim*2;

  // generating points.
  for(int i = 0; i < pointNumber; i++){
    Particle particle = new Particle();
    particles.add(particle);
  }

  noLoop();

}

void draw() {
  fill(#080B1A, 0.8);
  noStroke();
  rect(0, 0, width, height);

  // 뭔가 삼각형을 만들려면 이렇게 해야하는 걸까...

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
          particle3.display();
          particle1.display();
          particle2.display();

          // in the middle of the 3 points
          midPointX = (particle1.x + particle2.x + particle3.x) / 3;
          midPointY = (particle1.y + particle2.y + particle3.y) / 3;

          // 비례를 구해보자..
          lineA = dist(particle2.x, particle2.y, particle3.x, particle3.y);
          lineB = dist((particle2.x + particle3.x)/2, (particle2.y + particle3.y)/2, particle1.x, particle1.y);

          if(lineA <= distance && lineB <= distance){
            // 점이 셋다 사정거리일때.
            alphaModulate = 1;
            

          } else if(lineA <= distance && lineB > distance) {
            // 둘은 가까운데 하나는 아닐때 1 (fade-out)
            alphaModulate = map((lineB), distance, distance + offset, 1, 0);

          } else if(lineB <= distance && lineA > distance) {
            // 둘은 가까운데 하나는 아닐때 2 (fade-out)
            alphaModulate = map((lineA), 0, distance + offset, 1, 0);

          } else {
            // 셋다 멀때.
            alphaModulate = 0;
          }

          // 별가루 같은 느낌.
          pushStyle();
          fill(56, 47 - (particle2.randomiser / 2), 100, particle1.randomiser / 100);
          ellipse(midPointX, midPointY, particle1.r, particle1.r);
          popStyle();

          triangleGen(particle2.x, particle2.y, particle3.x, particle3.y, midPointX, midPointY, particle2.c, alphaModulate);
          triangleGen(particle1.x, particle1.y, particle3.x, particle3.y, midPointX, midPointY, particle3.c, alphaModulate);
          triangleGen(particle1.x, particle1.y, particle2.x, particle2.y, midPointX, midPointY, particle1.c, alphaModulate);
          particle2.update();
          particle3.update();
          particle1.update();
        }
      }
    }
    popMatrix();
  }
}

public void triangleGen(float x1, float y1, float x2, float y2, float x3, float y3, color c, float colorOpacity){
  pushStyle();
  fill(c, colorOpacity);
  triangle(x1, y1, x2, y2, x3, y3);
  popStyle();
}

// 색을...써보아요...
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
  color c;
  int i = 1, j = 1;

  Particle(){
    x = random(trim, randomBoundary);
    y = random(trim, randomBoundaryHeight);
    r = random(1, 4);

    randomiser = random(100);
    colorMode(HSB,360,100,100,1);

    if(randomiser > 70) c = colorBlended(random(1), 178, 37, 92, 171, 68, 72, 0.8);
    else if(randomiser > 30) c = colorBlended(random(1), 188, 37, 92, 171, 68, 72, 0.8);
    else c = colorBlended(random(1), 161, 39, 94, 199, 96, 60, 0.8);
  }

  void display(){
    pushStyle();
    noStroke();
    fill(c);
    ellipse(x, y, 3, 3);
    popStyle();
  }

  void triangleGen(Particle p2, Particle p3){
    pushStyle();
    noStroke();

    float x2 = p2.x;
    float x3 = p3.x;

    float y2 = p2.y;
    float y3 = p3.y;

    float midPointX = (x + x2 + x3) / 3;
    float midPointY = (y + y2 + y3) / 3;

    color c1 = c;
    color c2 = p2.c;
    color c3 = p3.c;

    fill(c1);
    triangle(x, y, x2, y2, midPointX, midPointY);

    fill(c2);
    triangle(x2, y2, x3, y3, midPointX, midPointY);

    fill(c3);
    triangle(x, y, x3, y3, midPointX, midPointY);

    popStyle();
  }

  void update()
  {
    // animating logic (처음엔 정방향이라서 재미가 없다!!!)
    x = x + j*0.02;
    y = y + i*0.02;

    if (y > randomBoundaryHeight - r) {
      i =- 1;
      // 파란계열
      c = colorBlended(random(1), 195, 80, 50, 210, 96, 100, 0.8);

    }
    if (y < trim + r) {
      i = 1;
      rotateX(PI/3.0 * -i);
      // 사이안 계열
      c = colorBlended(random(1), 190, 40, 75, 199, 96, 95, 0.8);

    }

    if (x > randomBoundary - r){
      j =- 1;
      c = colorBlended(random(1), 181, 80, 75, 199, 96, 70, 0.8);

    }

    if (x < trim + r) {
      j = 1;
      // 녹색계??
      c = colorBlended(random(1), 171, 50, 94, 199, 96, 40, 0.8);
    }

  }
}

// Save screenshots
void keyPressed() {
  println("saved");
  saveFrame("particles-###.png");
}
