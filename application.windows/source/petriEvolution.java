import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class petriEvolution extends PApplet {

/* Petri - Draft 1
 by Jeremy Kahn
 */

// A great big pile of variables.
int screenResX = 800, screenResY = 600, 
predatorSize = 10, preySize = 2, cannibalSize = predatorSize,
startingPrey = 1, startingPredators = 0, startingCannibals = 0,
startingPositionRange = 50, wanderRange = 8500,

predatorWanderTime = 100, predatorReproductionTime = 30,
predatorHungerCapacity = 5, predatorReproductionRequirementLowerRange = 10,
predatorReproductionRequirementUpperRange = 20,
predatorSizeLimit = predatorReproductionRequirementLowerRange,

// Prey reproduction values.
preyBaseAgeOfMaturity = 6, preyMaxAgeOfMaturity = 15, 

// Evolution values.
preyBaseEvolutionAge = 25, preyMaxEvolutionAge = 40,
predatorBaseEvolutionAge = 20, predatorMaxEvolutionAge = 40,

// Cannibal lifespan values.
cannibalBaseLifespan = 25, cannibalMaxLifespan = 45,
cannibalReincarnationTime = 20,

// Population control values.
preyPopulationLimit = 1000, preyRepopulationLevel = 1,
predatorPopulationLimit = 300, predatorRepopulationLevel = 1,
cannibalPopulationLimit = 250, cannibalRepopulationLevel = 15;

// Speed values.
float maxPredatorVelocity = 5, predatorAcceleration = 1, 
maxPreyVelocity = .8f, preyAcceleration = .1f,
maxCannibalVelocity = 5.5f, cannibalAcceleration = 1;

boolean preyCanPopulate = true,
predatorsCanPopulate = true,
cannibalsCanPopulate = true;

// These are actually color values.  "color" is just a typecasted int.
int backgroundColor = color(55),
predatorColor = color(50, 50, 255),
preyColor = color(0, 255, 0),
cannibalColor = color(255, 0, 180),
deadCannibalColor = color(128, 0, 64);

/* Henceforth, groups of entities will be referred to as "piles,"
 because it's funnier that way. */
Vector predatorPile = new Vector();
Vector preyPile = new Vector();
Vector cannibalPile = new Vector();

public void setup()
{
  frameRate(15);
  //size(800, 600);
  size(screen.width, screen.height);
  noCursor();

  for (int i = 0; i < startingPrey; i++)
  {
    preyPile.addElement(new prey()); 
  }

  // Delete later, can be used for testing now.
  /*for (int i = 0; i < startingPredators; i++)
    predatorPile.addElement(new predator()); 

  for (int i = 0; i < startingCannibals; i++)
    cannibalPile.addElement(new cannibal()); */

  noStroke();
  smooth();

  background(backgroundColor);
}

public void draw()
{
  background(backgroundColor);

  populationControl();

  // This may have to be moved into a more robust control sructure.
  for (int i = 0; i < cannibalPile.size(); i++)
  { 
    cannibal tempCannibal = (cannibal)cannibalPile.elementAt(i);
    tempCannibal.executeBehavior(i);
  }

  for (int i = 0; i < predatorPile.size(); i++)
  { 
    predator tempPredator = (predator)predatorPile.elementAt(i);
    tempPredator.executeBehavior(i);
  }

  for (int i = 0; i < predatorPile.size(); i++)
  { 
    predator tempPredator = (predator)predatorPile.elementAt(i);
    tempPredator.drawEntity(i);
  }

  for (int i = 0; i < preyPile.size(); i++)
  {
    prey tempPrey = (prey)preyPile.elementAt(i);
    tempPrey.moveEntity(i);
  }

  /* Draw all entities.
   These are in separate for loops to avoid flckering issues!*/
  for (int i = 0; i < preyPile.size(); i++)
  {
    prey tempPrey = (prey)preyPile.elementAt(i);
    tempPrey.drawEntity(i);
  }

  for (int i = 0; i < cannibalPile.size(); i++)
  { 
    cannibal tempCannibal = (cannibal)cannibalPile.elementAt(i);
    tempCannibal.drawEntity(i);
  }

  for (int i = 0; i < predatorPile.size(); i++)
  { 
    predator tempPredator = (predator)predatorPile.elementAt(i);
    tempPredator.drawEntity(i);
  }

}

/* Returns true if two circles are colliding.*/
public boolean collision(entity predator, entity prey)
{
  float xDist = predator.xPos - prey.xPos,
  yDist = predator.yPos - prey.yPos;

  /* Pathagorean Theorem, yo!*/
  if ((predator.radius/2 + prey.radius/2) >= sqrt(sq(xDist) + sq(yDist)))
  {
    return true;
  }

  return false;
}

public float findDistance(entity predator, entity prey)
{
  float xDist = predator.xPos - prey.xPos,
  yDist = predator.yPos - prey.yPos;

  return sqrt(sq(xDist) + sq(yDist));
}

public void keyPressed()
{
  /*println("Prey:  " + preyPile.size() + 
   ", Predators:  " + predatorPile.size() + 
   ", Cannibals:  " + cannibalPile.size());*/

  /*if (key == '1')
   try
   {
   preyPile.removeElementAt(preyPile.size() - 1);
   }
   catch (Exception Ex)
   {
   println("Prey removal failed!");
   }
   
   if (key == '2')
   try
   {
   predatorPile.removeElementAt(predatorPile.size() - 1);
   }
   catch (Exception Ex)
   {
   println("Predator removal failed!");
   }
   
   if (key == '3')
   try
   {
   cannibalPile.removeElementAt(cannibalPile.size() - 1);
   }
   catch (Exception Ex)
   {
   println("Cannibal removal failed!");
   }*/
}

public void populationControl()
{ 
  if (preyPile.size() >= preyPopulationLimit)
    preyCanPopulate = false;

  if (predatorPile.size() >= predatorPopulationLimit)
    predatorsCanPopulate = false;

  if (cannibalPile.size() >= cannibalPopulationLimit)
    cannibalsCanPopulate = false;

  if (!preyCanPopulate && (preyPile.size() <= preyRepopulationLevel))
    preyCanPopulate = true;

  if (!predatorsCanPopulate && (predatorPile.size() <= predatorRepopulationLevel))
    predatorsCanPopulate = true;

  if (!cannibalsCanPopulate && (cannibalPile.size() <= cannibalRepopulationLevel))
    cannibalsCanPopulate = true;
}






class cannibal extends entity
{
  /* Cannibal states. */
  final int HUNT = 0, WANDER = 1, DYING = 2;

  boolean chasing = false;
  int state = WANDER, totalPredatorsEaten = 0,
  /* This refers to how much life is left in the cannibal when dying. */
  percentAlive = 100,
  countdownToReincarnation = (int)(cannibalReincarnationTime * frameRate), reincarnationPercent,
  lifespan;

  cannibal()
  {
    super(cannibalSize);

    entityColor = cannibalColor;
    maxVelocity = maxCannibalVelocity;
    acceleration = cannibalAcceleration;
    this.lifespan = (int)random(cannibalBaseLifespan, cannibalMaxLifespan);
  }

  cannibal(float xPos, float yPos, int indexInArray)
  {
    super(cannibalSize);

    entityColor = cannibalColor;
    maxVelocity = maxCannibalVelocity;
    acceleration = cannibalAcceleration;
    this.lifespan = (int)random(cannibalBaseLifespan, cannibalMaxLifespan);

    this.xPos = xPos;
    this.yPos = yPos;
    this.indexInArray = indexInArray;

    newGoal();
  }

  public void executeBehavior(int indexInArray)
  {        
    if (predatorPile.size() > 0)
      state = HUNT;
    else
      state = WANDER;

    if (this.age >= this.lifespan)
      state = DYING;

    if (deadOrAlive == ALIVE)
    {
      switch (state)
      {
      case WANDER:
        newGoal();
        moveEntity(indexInArray);
        break;

      case DYING:
        if (maxVelocity > 0)
          maxVelocity -= .05f;

        percentAlive--;
        this.entityColor = fade(deadCannibalColor, cannibalColor, percentAlive);
        newGoal();
        moveEntity(indexInArray);

        if (percentAlive <= 0)
          deadOrAlive = DEAD;
        break;

        /* Deliberate fall-through.*/
      case HUNT:
      default:
        determineClosestPredator();
        chase();
        moveEntity(indexInArray);
        checkAndEat();
        break;
      }
    }
    else
    {
      if (countdownToReincarnation > 0)
        countdownToReincarnation--;
      else
      {
        reincarnationPercent++;

        this.radius = (int)map(reincarnationPercent, 0, 100, cannibalSize, preySize);
        this.entityColor = fade(deadCannibalColor, preyColor, reincarnationPercent);

        if(this.reincarnationPercent >= 100)
        {
          try
          {
            preyPile.addElement(new prey(this.xPos, this.yPos, preyPile.size()));
            cannibalPile.remove(this.indexInArray);
          }
          catch (Exception ex)
          {

          }
        }
      }
    }
  }

  public void chase()
  {
    try
    {
      predator tempPredator = (predator)predatorPile.elementAt(targetVectorIndex);
      goalXPos = tempPredator.xPos;
      goalYPos = tempPredator.yPos;
    }
    catch (Exception ex)
    {
      state = HUNT;
    }
  }

  public void determineClosestPredator()
  {
    if (!chasing)
    {
      float currentShortestDistance = Float.MAX_VALUE;

      for (int i = 0; i < predatorPile.size(); i++)
      {
        float distance = findDistance(this, (predator)predatorPile.elementAt(i));
        if (currentShortestDistance >= distance)
        {
          currentShortestDistance = distance;
          targetVectorIndex = i;
          chasing = true;
        }
      }
    }
  }

  public void checkAndEat()
  {
    for (int i = 0; i < predatorPile.size(); i++)
    {
      if (collision(this, (predator)predatorPile.elementAt(i)))
      {
        try
        {
          predatorPile.removeElementAt(i);
        }
        catch (Exception ex)
        {

        }
        totalPredatorsEaten++;
      }
    }
  }
}






/* Notes:
 
 Entity age is measured in seconds.
 */

class entity
{
  float xPos, yPos, 
  goalXPos, goalYPos, 
  xVelocity, yVelocity, 
  maxVelocity, acceleration;
  int radius, age, birthTime,
  indexInArray, targetVectorIndex = 0;

  /* Alive/dead states. */
  final boolean ALIVE = true, DEAD = false;
  boolean deadOrAlive = ALIVE;

  int entityColor;

  entity(int radius)
  {
    this.radius = radius;

    xPos = random(-startingPositionRange, width + startingPositionRange);
    yPos = random(-startingPositionRange, height + startingPositionRange);

    xVelocity = 0;
    yVelocity = 0;

    age = 0;
    birthTime = frameCount;

    deadOrAlive = ALIVE;
  }

  public void drawEntity(int indexInArray)
  {
    this.indexInArray = indexInArray;
    fill(entityColor);
    ellipse(xPos, yPos, radius, radius);
  }

  public void moveEntity(int indexInArray)
  {
    if (goalXPos >= xPos)
    {
      if (xVelocity < maxVelocity)
        xVelocity += acceleration;
    }
    else
    {
      if (xVelocity > -maxVelocity)
        xVelocity -= acceleration;
    }

    if (goalYPos >= yPos)
    {
      if (yVelocity < maxVelocity)
        yVelocity += acceleration;
    }
    else
    {
      if (yVelocity > -maxVelocity)
        yVelocity -= acceleration;
    }

    /* Position values are adjusted.*/
    xPos += xVelocity;
    yPos += yVelocity;

    age = (int)((frameCount - birthTime) / frameRate);
  }

  public void newGoal()
  {
    goalXPos = random(-wanderRange, width + wanderRange);
    goalYPos = random(-wanderRange, height + wanderRange);
  }
}

/* Color Shifting Algorithm
By Jeremy Kahn

This function will find a color that exists in between two given colors, based
upon a percentage.*/

/*
from:         Initial color
to:           Shifted-to color (if fadePercent is 100)
fadePercent:  How much to fade.  0 is none, 100 is totally shifted.*/
public int fade(int from, int to, float fadePercent)
{
  float fromR = red(from),
  fromG = green(from),
  fromB = blue(from),
  fromA = alpha(from),
  toR = red(to),
  toG = green(to),
  toB = blue(to),
  toA = alpha(to),
  differenceR = map(fadePercent, 0, 100, fromR, toR), 
  differenceG = map(fadePercent, 0, 100, fromG, toG), 
  differenceB = map(fadePercent, 0, 100, fromB, toB),
  differenceA = map(fadePercent, 0, 100, fromA, toA);

  /* These prevent NaNs and Infinite values.*/
  if ((fromR == 0) && (toR == 0))
    differenceR = 0;

  if ((fromG == 0) && (toG == 0))
    differenceG = 0;

  if ((fromB == 0) && (toB == 0))
    differenceB = 0;

  if ((fromA == 0) && (toA == 0))
    differenceA = 0;

  if ((fromR == 255) && (toR == 255))
    differenceR = 255;

  if ((fromG == 255) && (toG == 255))
    differenceG = 255;

  if ((fromB == 255) && (toB == 255))
    differenceB = 255;

  if ((fromA == 255) && (toA == 255))
    differenceA = 255;

  return color(differenceR, differenceG, differenceB, differenceA);
}





class predator extends entity
{
  /* Predator states. */
  final int HUNT = 0, WANDER = 1, REPRODUCE = 2, EVOLVE = 3;

  boolean chasing = false;
  int state = WANDER, previousState = state,
  wanderTimer = predatorWanderTime, shrinkTimer = predatorReproductionTime,
  evolutionAgeMin = predatorBaseEvolutionAge, evolutionAgeMax = predatorMaxEvolutionAge,
  evolutionAge = (int)random(evolutionAgeMin, evolutionAgeMax),
  currentPreyEaten = 0, totalPreyEaten = 0,
  evolutionCounter = 0, snapShottedRadius,
  preyNeedToReproduce;

  predator()
  {
    super(predatorSize);
    entityColor = predatorColor;
    maxVelocity = maxPredatorVelocity;
    acceleration = predatorAcceleration;
    preyNeedToReproduce = (int)random(predatorReproductionRequirementLowerRange, predatorReproductionRequirementUpperRange);

    newGoal();
  }

  predator(float xPos, float yPos, int indexInArray)
  {
    super(predatorSize);
    entityColor = predatorColor;
    maxVelocity = maxPredatorVelocity;
    acceleration = predatorAcceleration;
    preyNeedToReproduce = (int)random(predatorReproductionRequirementLowerRange, predatorReproductionRequirementUpperRange);
  
    this.xPos = xPos;
    this.yPos = yPos;
    this.indexInArray = indexInArray;

    newGoal();
  }

  public void executeBehavior(int indexInArray)
  {
    if (age >= evolutionAge
      && cannibalsCanPopulate)
      state = EVOLVE;

    switch (state)
    {
    case WANDER:
      newGoal();
      moveEntity(indexInArray);

      wanderTimer--;

      if (wanderTimer <= 0)
        state = HUNT;
      break;

    case REPRODUCE:
      if (maxVelocity > 0)
        maxVelocity -= .1f;

      shrinkTimer--;

      if(shrinkTimer <= 0)
      {
        radius--;
        shrinkTimer = predatorReproductionTime; 
      }

      if (radius <= predatorSize)
      {
        state = HUNT; 
        maxVelocity = maxPredatorVelocity;

        predator tempPred = new predator();
        tempPred.xPos = this.xPos;
        tempPred.yPos = this.yPos;
        predatorPile.addElement(tempPred);
      }

      newGoal();
      moveEntity(indexInArray);
      break;

    case EVOLVE:
      if (previousState != EVOLVE)
        snapShottedRadius = radius;

      evolutionCounter++;
      this.entityColor = fade(predatorColor, cannibalColor, evolutionCounter);
      this.radius = (int)map(evolutionCounter, 0, 100, snapShottedRadius, cannibalSize);

      newGoal();
      moveEntity(indexInArray);

      if(this.evolutionCounter >= 100)
      {
        try
        {
          cannibalPile.addElement(new cannibal(this.xPos, this.yPos, cannibalPile.size())); 
          predatorPile.remove(this.indexInArray);
        }
        catch (Exception ex)
        {

        }
      }

      break;

      /* Deliberate fall-through.*/
    case HUNT:
    default:
      determineClosestPrey();
      chase();
      moveEntity(indexInArray);
      checkAndEat();
      break;
    }

    previousState = state;
  }

  public void determineClosestPrey()
  {
    if (!chasing)
    {
      float currentShortestDistance = Float.MAX_VALUE;

      for (int i = 0; i < preyPile.size(); i++)
      {
        float distance = findDistance(this, (prey)preyPile.elementAt(i));
        if (currentShortestDistance >= distance)
        {
          currentShortestDistance = distance;
          targetVectorIndex = i;
          chasing = true;
        }
      }
    }
  }

  public void chase()
  {
    try
    {
      prey tempPrey = (prey)preyPile.elementAt(targetVectorIndex);
      goalXPos = tempPrey.xPos;
      goalYPos = tempPrey.yPos;
    }
    catch (Exception ex)
    {
      state = WANDER;
      wanderTimer = predatorWanderTime;
    }
  }

  public void checkAndEat()
  {
    for (int i = 0; i < preyPile.size(); i++)
    {
      if (collision(this, (prey)preyPile.elementAt(i)))
      {
        try
        {
          preyPile.removeElementAt(i);
        }
        catch (Exception ex)
        {

        }

        chasing = false;
        currentPreyEaten++;

        if (radius <= predatorSizeLimit)
          radius++;

        if (currentPreyEaten >= predatorHungerCapacity)
        {
          totalPreyEaten += currentPreyEaten;
          currentPreyEaten = 0;
          state = WANDER;
          wanderTimer = predatorWanderTime;

          if ((radius - predatorSize) >= preyNeedToReproduce
            && predatorsCanPopulate)
          {
            state = REPRODUCE;
          }
        }
      }
    }
  }
}






class prey extends entity
{
  int reproductionAge, // Defines at exactly what age this prey will reproduce.
  evolutionAge, // Defines at exactly what age this prey will evolve.
  evolutionCounter,
  state; 

  // States
  final int WANDER = 0, EVOLVING = 1;

  prey()
  {
    super(preySize);
    xPos = width / 2;
    yPos = height / 2;
    entityColor = preyColor;
    maxVelocity = maxPreyVelocity;
    acceleration = preyAcceleration;
    this.reproductionAge = (int)random(preyBaseAgeOfMaturity, preyMaxAgeOfMaturity);
    this.evolutionAge = (int)random(preyBaseEvolutionAge, preyMaxEvolutionAge);
    state = WANDER;
    evolutionCounter = 0;

    newGoal();
  }

  prey(float xPos, float yPos, int indexInArray)
  {
    super(preySize);
    entityColor = preyColor;
    maxVelocity = maxPreyVelocity;
    acceleration = preyAcceleration;
    this.xPos = xPos;
    this.yPos = yPos;
    this.indexInArray = indexInArray;
    state = WANDER;
    evolutionCounter = 0;

    this.reproductionAge = (int)random(preyBaseAgeOfMaturity, preyMaxAgeOfMaturity);
    this.evolutionAge = (int)random(preyBaseEvolutionAge, preyMaxEvolutionAge);

    newGoal();
  }

  public void moveEntity(int indexInArray)
  {    
    newGoal();
    super.moveEntity(indexInArray);

    if (this.age >= this.evolutionAge
      && predatorsCanPopulate)
      state = EVOLVING;

    if (state == WANDER)
    {
      if (this.age >= this.reproductionAge
        && preyCanPopulate)
      {
        this.reproductionAge += (int)random(preyBaseAgeOfMaturity, preyMaxAgeOfMaturity);
        preyPile.addElement(new prey(this.xPos, this.yPos, preyPile.size())); 
      }
    }

    if (state == EVOLVING)
    {
      this.evolutionCounter++;

      this.radius = (int)map(evolutionCounter, 0, 100, preySize, predatorSize);
      this.entityColor = fade(preyColor, predatorColor, evolutionCounter);

      if(this.evolutionCounter >= 100)
      {
        try
        {
          predator tempPred = new predator(this.xPos, this.yPos, predatorPile.size());
          predatorPile.addElement(tempPred);
          preyPile.remove(this.indexInArray);
        }
        catch (Exception ex)
        {

        }
      }
    }
  }
}










  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--hide-stop", "petriEvolution" });
  }
}
