package pfe.stage.oumast.signupandlogintabs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tab1Login extends Fragment implements View.OnClickListener{
    EditText emailLogin, passwordLogin;
    Button buttonLogin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1login, container, false);
        emailLogin = (EditText) rootView.findViewById(R.id.editTextEmailLogin);
        passwordLogin = (EditText) rootView.findViewById(R.id.editTextPasswordLogin);
        emailLogin.setText(MainActivity.emailLoginMain);
        buttonLogin = (Button) rootView.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isConnected()){
            checkAuto();
        }
    }
    //pour vérifier la connexion
    public boolean isConnected(){
        final ConnectivityManager connMgr = (ConnectivityManager)
                MainActivity.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            return true;
        } else if (mobile.isConnectedOrConnecting ()) {
            return true;
        } else {
            return false;
        }
    }
    //faire l'authentification au temps de demarrage de l'application
    public void checkAuto(){
        HashMap hmap = MainActivity.db.getAllrecord();
        System.out.println("----checkAuto()");
        if(hmap.size() == 2){
            // auto authenticate
            String m =null;
            m = hmap.get("email").toString();
            String p = null;
            p = hmap.get("pass").toString();

            if(m !=null && p != null){
                String result = null;
                String id = null;
                id = MainActivity.db_id.getDeviceID();
                System.out.println("----result : id"+id);
                HashMap<String, Object> objects = new HashMap<String, Object>();
                objects.put("email",m);
                objects.put("password", p);
                objects.put("deviceid", id);
                //envoyer les données par get
                result = CnxToServer.GET(objects, "https://moteur.ma/fr/sms_app/login_sms/");
                System.out.println("----result : "+result);
                if(result != null){
                    int code = Integer.parseInt(result);
                    if(code!=0){
                    Toast.makeText(MainActivity.ctx, "Welcome", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.ctx, HomeActivity.class);
                    //demarrer l'interface principale de projet
                    startActivity(intent);
                }
            }
            }
        }
    }
    public static String email = "", pass="";
    @Override
    public void onClick(View v) {
        System.out.println("----onClick()");
        if(!validateEmail(emailLogin.getText().toString())){
            emailLogin.setError("Invalid Mail");
            emailLogin.requestFocus();
        }else
        if(!validatePassword(passwordLogin.getText().toString())){
            passwordLogin.setError("Invalid Password");
            passwordLogin.requestFocus();
        }else {
            email = emailLogin.getText().toString(); pass = passwordLogin.getText().toString();
            email = email.replace("\\s+","");pass = pass.replace("\\s+","");
            String result = null;
            //result = CnxToServer.POSTREST(objects, "https://oumastmohamed15.000webhostapp.com/authentication.php");
            String id = null;
            id = MainActivity.db_id.getDeviceID();
            System.out.println("----result : id"+id);
            HashMap<String, Object> objects = new HashMap<String, Object>();
            objects.put("email",email);
            objects.put("password", pass);
            objects.put("deviceid", id);
            result = CnxToServer.GET(objects, "https://moteur.ma/fr/sms_app/login_sms/");
            System.out.println("----result : "+result);
            if(result != null){
                int code = Integer.parseInt(result);
                if(code!=0){
                    Intent intent = new Intent(MainActivity.ctx, HomeActivity.class);
                    try{
                        //inserer l'email et password dans base de donnee
                        MainActivity.db.insertRowLogin(email, pass);
                    }catch (Exception e){
                        System.out.println("----------------> Error insert row in databases, Tab1Login class, error : "+e.getMessage());
                        return;
                    }
                    Toast.makeText(MainActivity.ctx, "Welcome", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.ctx, "Password or Email incorrect, please, try again !", Toast.LENGTH_LONG+3).show();
                }
            }else{
                Toast.makeText(MainActivity.ctx, "Connection failed, please, check connection !", Toast.LENGTH_LONG+3).show();
            }
        }
    }
    //valider la forme d'un email
    protected boolean validateEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    // verifier le mot de passe > 3
    protected boolean validatePassword(String password) {
        if(password!=null && password.length()>2) {
            return true;
        } else {
            return false;
        }
    }
}
