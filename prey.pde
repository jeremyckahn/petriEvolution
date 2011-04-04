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

  void moveEntity(int indexInArray)
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









