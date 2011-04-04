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

  color entityColor;

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

  void drawEntity(int indexInArray)
  {
    this.indexInArray = indexInArray;
    fill(entityColor);
    ellipse(xPos, yPos, radius, radius);
  }

  void moveEntity(int indexInArray)
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

  void newGoal()
  {
    goalXPos = random(-wanderRange, width + wanderRange);
    goalYPos = random(-wanderRange, height + wanderRange);
  }
}

