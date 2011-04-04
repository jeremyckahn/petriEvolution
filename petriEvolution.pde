/* Petri Evolution
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
maxPreyVelocity = .8, preyAcceleration = .1,
maxCannibalVelocity = 5.5, cannibalAcceleration = 1;

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

void setup()
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

void draw()
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
boolean collision(entity predator, entity prey)
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

float findDistance(entity predator, entity prey)
{
  float xDist = predator.xPos - prey.xPos,
  yDist = predator.yPos - prey.yPos;

  return sqrt(sq(xDist) + sq(yDist));
}

void keyPressed()
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

void populationControl()
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
