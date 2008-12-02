package ten24.twitt24;

public class Twitt24Scheduler extends Thread
{
  Twitt24 twitt24;
  boolean running;

  public Twitt24Scheduler(Twitt24 twitt24)
  {
    this.twitt24 = twitt24;
  }
  
  @Override
  public void run()
  {
    super.run();
    running=true;
    while (running)
    {
      try
      {
      twitt24.doMagicUpdate();
      sleep(52000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }

  void setRunning(boolean running)
  {
    this.running = running;
  }
}