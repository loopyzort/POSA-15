package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * An Activity that downloads an image, stores it in a local file on the local device, and returns a
 * Uri to the image file.
 */
public class DownloadImageActivity extends Activity {

    public static final String URI_EXTRA = "downloadImageActivityResultUrl";
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created. One time initialization code
     * goes here, e.g., UI layout and some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            final Uri uri = getIntent().getParcelableExtra(URI_EXTRA);
            Toast.makeText(this, "Starting download", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Uri resultUri =
                            DownloadUtils.downloadImage(DownloadImageActivity.this, uri);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK, new Intent().putExtra(URI_EXTRA, resultUri));
                            finish();
                        }
                    });

                }
            }).start();

        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse Uri argument in intent");
            setResult(RESULT_CANCELED);
            finish();

        }
        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity
        // -from-a-background-thread
        // for more discussion about this topic.
    }
}
