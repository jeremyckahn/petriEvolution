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

  void executeBehavior(int indexInArray)
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
          maxVelocity -= .05;

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

  void chase()
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

  void determineClosestPredator()
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

  void checkAndEat()
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






