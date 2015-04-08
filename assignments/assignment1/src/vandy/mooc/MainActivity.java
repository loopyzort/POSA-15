package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mUrlEditText = (EditText) findViewById(R.id.url);
    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Download Image" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            // Hide the keyboard.
            hideKeyboard(this,
                         mUrlEditText.getWindowToken());

            Uri uri = getUrl();
            if (uri != null) {
                Intent intent = makeDownloadImageIntent(getUrl());
                startActivityForResult(intent, DOWNLOAD_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        boolean failure = false;
        if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Uri resultUri = data.getParcelableExtra(DownloadImageActivity.URI_EXTRA);
                if (resultUri != null) {
                    Intent intent = makeGalleryIntent(resultUri.getPath());
                    startActivity(intent);
                } else {
                    failure = true;
                }
            } else {
                failure = true;
            }
        }
        if (failure) {
            Toast.makeText(this, "An error occurred trying to download image",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Factory method that returns an implicit Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String url) {
        Intent result = new Intent(Intent.ACTION_VIEW);
        result.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        return result;
    }

    /**
     * Factory method that returns an implicit Intent for downloading
     * an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        return new Intent(this, DownloadImageActivity.class).putExtra(
                DownloadImageActivity.URI_EXTRA, url);
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        if (URLUtil.isValidUrl(url.toString())) {
            return url;
        } else {
            Toast.makeText(this,
                           "Invalid URL",
                           Toast.LENGTH_SHORT).show();
            return null;
        } 
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }
}
