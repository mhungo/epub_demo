package epub_core.ui.base;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import epub_core.model.dictionary.Dictionary;
import epub_core.network.TLSSocketFactory;
import epub_core.util.AppUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author gautam chibde on 4/7/17.
 */

public class DictionaryTask extends AsyncTask<String, Void, Dictionary> {

    private static final String TAG = "DictionaryTask";

    private DictionaryCallBack callBack;

    public DictionaryTask(DictionaryCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Dictionary doInBackground(String... strings) {
        String strUrl = strings[0];
        try {
            Log.v(TAG, "-> doInBackground -> url -> " + strUrl);
            URL url = new URL(strUrl);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            if (Build.VERSION.SDK_INT <= 20)
                httpsURLConnection.setSSLSocketFactory(new TLSSocketFactory());
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    AppUtil.charsetNameForURLConnection(httpsURLConnection)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            return objectMapper.readValue(stringBuilder.toString(), Dictionary.class);
        } catch (Exception e) {
            Log.e(TAG, "DictionaryTask failed", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Dictionary dictionary) {
        super.onPostExecute(dictionary);
        if (dictionary != null) {
            callBack.onDictionaryDataReceived(dictionary);
        } else {
            callBack.onError();
        }
        cancel(true);
    }
}
