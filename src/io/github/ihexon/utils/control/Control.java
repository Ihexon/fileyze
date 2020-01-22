package io.github.ihexon.utils.control;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Control extends AbstractControl {
	private static Control control = null;
	public static final boolean IS_WINDOWS;
	static
	{
		String os = System.getProperty("os.name");
		if (os == null)
		{
			IS_WINDOWS = false;
		}
		else
		{
			String osl = os.toLowerCase(Locale.ENGLISH);
			IS_WINDOWS = osl.contains("windows");
		}
	}

	public Control() {
	}

	public  boolean getIS_WINDOWS(){
		return IS_WINDOWS;
	}

	public static Control getSingleton() {
		if (control == null){
			control = new Control();
		}
		return control;
	}

	/**
	 * Change the quiet time.
	 * @return the quiet time in millis
	 */
	public long getUpdateQuietTimeMillis(){
		// !!! Set update time, default every 1 second to capture the event from file !!!
		long updateQuietTimeDuration = 1000;
		TimeUnit updateQuietTimeUnit = TimeUnit.MILLISECONDS;
		return TimeUnit.MILLISECONDS.convert(updateQuietTimeDuration, updateQuietTimeUnit);
	}
}
