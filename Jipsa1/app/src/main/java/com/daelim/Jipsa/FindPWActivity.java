package com.daelim.Jipsa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FindPWActivity extends AppCompatActivity {

    TextView TvFPID, TvFPName, TvFPPhone;
    EditText EdFPID, EdFPName, Ed_Email;
    Button BtnFPCom,btn_pwdck;
    String name,numOfEmail,email,id;
    boolean flag_email=false;
    FirebaseFirestore db;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);
        EdFPID = findViewById(R.id.ed_FpID);
        EdFPName = findViewById(R.id.ed_FpName1);
        Ed_Email = findViewById(R.id.ed_Em1);
        BtnFPCom = findViewById(R.id.btn_FPCom1);
        BtnFPCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_email){
                    db = FirebaseFirestore.getInstance();
                    db.collection("members").document(id).update("pwd",numOfEmail);
                    Intent FIComIntent = new Intent(FindPWActivity.this, LoginActivity.class);
                    startActivity(FIComIntent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPWActivity.this);
                    builder.setTitle("?????? ??????");
                    builder.setMessage("????????? ????????? ??????????????????");
                    builder.setPositiveButton("??????", null);
                    builder.create().show();
//                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_pwdck = findViewById(R.id.btn_FCer1);
        btn_pwdck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = EdFPID.getText().toString();
                name = EdFPName.getText().toString();
                email  = Ed_Email.getText().toString();
                if(!id.equals("")&&!name.equals("")&&!email.equals("")){
                    db = FirebaseFirestore.getInstance();

                    DocumentReference docRef = db.collection("members").document(id);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData()+document.get("pwd"));
                                    if(name.equals(document.get("name"))&&email.equals(document.get("email"))){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPWActivity.this);
                                        builder.setTitle("???????????? ??????");
                                        builder.setMessage("???????????? ???????????? ??????????????? ?????????????????????");
                                        builder.setPositiveButton("??????", null);
                                        builder.create().show();
//                                        Toast.makeText(getApplicationContext(), "???????????? ???????????? ????????????", Toast.LENGTH_SHORT).show();
                                        JSONEmail Ji = new JSONEmail();
                                        Ji.setEmail(email);
                                        Ji.execute("http://192.168.6.1:3000/pwdmail");
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPWActivity.this);
                                        builder.setTitle("??????");
                                        builder.setMessage("?????? ?????????");
                                        builder.setPositiveButton("??????", null);
                                        builder.create().show();
//                                        Toast.makeText(getApplicationContext(), "?????? ?????????", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPWActivity.this);
                                    builder.setTitle("??????");
                                    builder.setMessage("?????? ????????? ?????????");
                                    builder.setPositiveButton("??????", null);
                                    builder.create().show();
//                                    Toast.makeText(getApplicationContext(), "?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    flag_email = true;
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPWActivity.this);
                    builder.setTitle("??????");
                    builder.setMessage("????????? ??????");
                    builder.setPositiveButton("??????", null);
                    builder.create().show();
//                    Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public class JSONEmail extends AsyncTask<String, String, String> {
        String email;
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject??? ????????? key value ???????????? ?????? ???????????????.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", email);
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //????????? ???
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST???????????? ??????
                    con.setRequestProperty("Cache-Control", "no-cache");//?????? ??????
                    con.setRequestProperty("Content-Type", "application/json");//application JSON ???????????? ??????
                    con.setRequestProperty("Accept", "text/html");//????????? response ???????????? html??? ??????
                    con.setDoOutput(true);//Outstream?????? post ???????????? ?????????????????? ??????
                    con.setDoInput(true);//Inputstream?????? ??????????????? ????????? ???????????? ??????
                    con.connect();

                    //????????? ?????????????????? ????????? ??????
                    OutputStream outStream = con.getOutputStream();
                    //????????? ???????????? ??????
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//????????? ?????????

//                    ????????? ?????? ???????????? ??????
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    System.out.println(buffer.toString());
                    return buffer.toString();//????????? ?????? ?????? ?????? ???????????? ?????? OK!!??? ???????????????

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//????????? ?????????
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            numOfEmail = result.substring(61,71);
            System.out.println(numOfEmail);

//            tvData = (TextView)findViewById(R.id.tvData);
//        tvData.setText(result);//????????? ?????? ?????? ?????? ??????????????? ???
        }
        protected  void setEmail(String email){
            this.email = email;
        }
    }
}