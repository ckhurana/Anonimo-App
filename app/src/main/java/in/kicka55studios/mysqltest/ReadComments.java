package in.kicka55studios.mysqltest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class ReadComments extends AppCompatActivity {

    public static final String TAG_POSTS = "posts";
    public static final String TAG_POST_ID = "post_id";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_TITLE = "title";
    public static final String TAG_COMMENT = "message";

    private String loggedUser = "";

    private Toolbar toolbar;
    private TextView textView1;
    private EditText commentInput;
    private ProgressDialog pDialog = null;
    private ScrollView scroll = null;

    private static final String COMMENTS_URL = "http://thicker.cu.cc/webservice/comments.php";
    private static final String ADD_COMMENT_URL = "http://thicker.cu.cc/webservice/addcomment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_comments);

        textView1 = (TextView) findViewById(R.id.textView1);
        commentInput = (EditText) findViewById(R.id.commentInput);
        scroll = (ScrollView) findViewById(R.id.scroll);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        loggedUser = getIntent().getStringExtra(Login.EXTRA_USERNAME);

        loadComments();


    }

    private void loadComments() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MyTask().execute(COMMENTS_URL);
        } else {
            Login.makeToast(this, "Network Unavailable");
        }

    }

    public void addComment(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (commentInput.getText().toString().trim().equals("")) {
                Login.makeToast(this, "Comment field empty!");
                commentInput.setText("");
            } else {
                new AddCommentTask().execute(ADD_COMMENT_URL);
            }

        } else {
            Login.makeToast(this, "Network Unavailable");
        }
    }

    class AddCommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReadComments.this);
            pDialog.setMessage("Adding Comments...");
            pDialog.setCancelable(true);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            HashMap<String, String> param = new HashMap<>();
            param.put(TAG_USERNAME, loggedUser);
            param.put(TAG_TITLE, "from app");
            param.put(TAG_COMMENT, commentInput.getText().toString().trim());
            return (new JSONParser()).makeHttpRequest(url[0], "POST", param);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            commentInput.setText("");
            pDialog.dismiss();
            loadComments();
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReadComments.this);
            pDialog.setMessage("Parsing Comments...");
            pDialog.setCancelable(true);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return (new JSONParser()).makeHttpGetRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                textView1.setText(parseJson(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
            scroll.post(new Runnable() {
                @Override
                public void run() {
                    scroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private String parseJson(String jsonString) throws JSONException {
        JSONObject jObj = new JSONObject(jsonString);
        String result = "";
        if (jObj.getInt(Login.TAG_SUCCESS) == 1) {
            JSONArray jsonArray = jObj.getJSONArray(TAG_POSTS);

            int numRows = jsonArray.length();

            for (int i = 0; i < numRows; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                result += (i + 1) + " - " + jsonObject.getString(TAG_USERNAME) + " - " + jsonObject.getString(TAG_COMMENT) + '\n';
            }
            return result;
        } else {
            Toast.makeText(this, "json not success", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        if (id == R.id.refresh) {
            new MyTask().execute(COMMENTS_URL);
        }

        return super.onOptionsItemSelected(item);
    }
}
