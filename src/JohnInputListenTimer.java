import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

//用于锁屏功能
public class JohnInputListenTimer implements KeyboardEventListener, MouseEventListener 
{
	private int full;
	private int count;
	private Timer timer=new Timer();
	private boolean isStoped;
	
	//每30秒减30,合一秒减一,传入的秒数即为不活动多少秒则锁屏
	public void start(int seconds)
	{
		count=seconds;
		full=count;
		GlobalEventListener gl=new GlobalEventListener();
		gl.addKeyboardEventListener(this);
		gl.addMouseEventListener(this);
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if(isStoped)
				{
					timer.cancel();
					return;
				}
				if(count<=0)
				{
					try
					{
						Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					timer.cancel();
					return;
				}
				count-=30;
			}
		}, 30000, 30000);
	}
	
	public void stop()
	{
		isStoped=true;
		timer.cancel();
	}

	@Override
	public void GlobalMouseX(MouseEvent arg0)
	{
		count=full;
	}

	@Override
	public void GlobalMouseY(MouseEvent arg0)
	{
		count=full;
	}

	@Override
	public void GlobalKeyPressed(KeyboardEvent arg0)
	{
		count=full;
	}

	@Override
	public void GlobalKeyReleased(KeyboardEvent arg0)
	{
		count=full;
	}
}
