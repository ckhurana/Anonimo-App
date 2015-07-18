package in.kicka55studios.mysqltest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Toolbar toolbar;

    public final static String TAG_USERNAME = "username";
    public final static String TAG_PASSWORD = "password";

    public final static String EXTRA_USERNAME = "in.kicka55studios.mysqltest.EXTRA_USERNAME";

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "http://thicker.cu.cc/webservice/login.php";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passInput);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void startLogin(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MyTask().execute(LOGIN_URL);
        } else {
            makeToast(this, "Network Unavailable");
        }
    }

    public void _(String msg) {
        Log.e("MySQLtest", msg);
    }

    public static void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setMessage("Attempting Login...");
            pDialog.show();
            _("Inside preExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> values = new HashMap<>();
            values.put(TAG_USERNAME, usernameInput.getText().toString().trim());
            values.put(TAG_PASSWORD, passwordInput.getText().toString().trim());
            return jsonParser.makeHttpRequest(params[0], "POST", values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            String msg = "";
            int success = 0;

            try {
                JSONObject json = new JSONObject(result);
                msg = json.getString(TAG_MESSAGE);
                success = json.getInt(TAG_SUCCESS);
                _("msg: " + msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();

            if (success == 1) {
                String user = usernameInput.getText().toString();
                makeToast(Login.this, "Welcome back " + user);
                Intent i = new Intent(Login.this, ReadComments.class);
                i.putExtra(EXTRA_USERNAME, user);
                startActivity(i);
            } else {
                makeToast(Login.this, msg);
            }

            usernameInput.setText("");
            passwordInput.setText("");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.registerUser) {
            startActivity(new Intent(this, Register.class));
        }

        return super.onOptionsItemSelected(item);
    }
}