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

  void executeBehavior(int indexInArray)
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
        maxVelocity -= .1;

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

  void determineClosestPrey()
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

  void chase()
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

  void checkAndEat()
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






