package kmckee90.emaapp.phd;

/**
 * Created by Work on 9/8/2016.
 */

//An interface implemented by holdbutton and holdbuttonbipolar with all the methods that will be used by the database.
public interface HoldButtonInterface {
    double getDuration();
    String getName();
    boolean isFlagged();
    long getTimestamp();
}
