import processing.opengl.*;

ArrayList particles;

int pointNumber = 16;
int trim = 20, offset = 10, distance = 120;

float randomBoundary, randomBoundaryHeight;
float alphaModulate, n;

float midPointX, midPointY;
float lineA, lineB;

void setup() {
  size(400, 600, OPENGL);
  pixelDensity(displayDensity());
  smooth(8);
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

void draw() {
  fill(#080B1A, 1);
  noStroke();
  rect(0, 0, width, height);

  for(int i = 0; i < particles.size(); i++){
    pushMatrix();

    Particle particle1 = (Particle) particles.get(i);
    particle1.update();

    ellipseMode(CENTER);

    for(int j = i + 1; j < particles.size(); j++){
      Particle particle2 = (Particle) particles.get(j);

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

          // finding ratio based on 2 lines of a triangle
          lineA = dist(particle2.x, particle2.y, particle3.x, particle3.y);
          lineB = dist((particle2.x + particle3.x)/2, (particle2.y + particle3.y)/2, particle1.x, particle1.y);
          alphaModulate = map((lineB), distance, distance + offset, 1, 0);

          if(lineA <= distance && lineB <= distance){
            // When all points are in the distance
            if((lineA >= lineB) == true){
              alphaModulate = map((lineB), distance, distance + offset, 1, 0);
            } else {
              alphaModulate = map((lineA), distance, distance + offset, 1, 0);
            }
            particle1.update();
          } else if(lineA <= distance && lineB > distance) {
            // A is far
            alphaModulate = map((lineB), distance, distance + offset, 1, 0);

          } else if(lineB <= distance && lineA > distance) {
            // B is far
            alphaModulate = map((lineA), distance, distance + offset, 1, 0);
            particle3.update();

            // Stardust effect
            pushStyle();
            fill(56, 47 - (particle2.randomiser / 2), 100, (particle1.randomiser)/100);
            ellipse(midPointX, midPointY, particle1.r, particle1.r);
            popStyle();

          } else {
            // All points are further than target distance.
            alphaModulate = 0;
            particle2.update();
          }

          // normalising
          n = norm(alphaModulate, 0, 1);

          // triangles.
          triangleGen(particle2.x, particle2.y, particle3.x, particle3.y, midPointX, midPointY, particle2.c, n);
          triangleGen(particle1.x, particle1.y, particle3.x, particle3.y, midPointX, midPointY, particle3.c, n);
          triangleGen(particle1.x, particle1.y, particle2.x, particle2.y, midPointX, midPointY, particle1.c, n);
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

// Colour blending
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
    r = random(1, 3);

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

  void update()
  {
    // animating particles
    x = x + j*0.02;
    y = y + i*0.02;

    if (y > randomBoundaryHeight - r) {
      i =- 1;
      // Blue
      c = colorBlended(random(1), 195, 80, 50, 210, 96, 100, 0.8);

    }
    if (y < trim + r) {
      i = 1;
      // Cyan
      c = colorBlended(random(1), 190, 40, 75, 199, 96, 95, 0.8);

    }

    if (x > randomBoundary - r){
      j =- 1;
      // Teal
      c = colorBlended(random(1), 181, 80, 75, 199, 96, 70, 0.8);

    }

    if (x < trim + r) {
      j = 1;
      // Green
      c = colorBlended(random(1), 171, 50, 94, 199, 96, 40, 0.8);
    }
  }
}

// void keyPressed(){
//   println("SAVED");
//   saveFrame("capture-###@2x.png");
// }
