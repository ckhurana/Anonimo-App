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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class Register extends AppCompatActivity {

    private ProgressDialog pDialog = null;
    private EditText usernameInput, passwordInput;
    private Toolbar toolbar;

    private static final String REGISTER_URL = "http://thicker.cu.cc/webservice/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passInput);

    }

    public void registerNewUser(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MyClass().execute(REGISTER_URL);
        } else {
            Login.makeToast(this, "Network Unavailable");
        }

    }

    class MyClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Registering New User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Login.TAG_USERNAME, usernameInput.getText().toString().trim());
            params.put(Login.TAG_PASSWORD, passwordInput.getText().toString().trim());

            JSONParser jsonParser = new JSONParser();

            return jsonParser.makeHttpRequest(url[0], "POST", params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            int success = 0;
            String msg = "";

            try {
                JSONObject json = new JSONObject(s);
                success = json.getInt(Login.TAG_SUCCESS);
                msg = json.getString(Login.TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Login.makeToast(Register.this, msg);
            passwordInput.setText("");
            if (success == 1) {
                usernameInput.setText("");
                finish();
            }

            pDialog.dismiss();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
