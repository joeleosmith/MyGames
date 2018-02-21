package Rent;

public class Go extends Landlord
{

  public Go(int Salary)
  {
    super(Salary, 0,0,0,0,0);
  }

  public int calculateRent()
  {
    return iRents[0];
  }

}