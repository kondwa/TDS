package mile.tdsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private Button btnSignIn;
    private Button btnSignUp;
    // Firebase...
    private FirebaseAuth mAuth;
    private ProgressBar mProgess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        mProgess = findViewById(R.id.signup_progress);
        mProgess.setVisibility(View.INVISIBLE);

        firstname = findViewById(R.id.first_name);
        lastname = findViewById(R.id.last_name);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        btnSignIn = findViewById(R.id.reg_signin);
        btnSignUp = findViewById(R.id.reg_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mFirst = firstname.getText().toString().trim();
                String mLast = lastname.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                if(mFirst.isEmpty()){
                    firstname.setError("First name is required.");
                    return;
                }
                if(mLast.isEmpty()){
                    lastname.setError("Last name is required.");
                    return;
                }
                if(mEmail.isEmpty()){
                    email.setError("Email is required.");
                    return;
                }
                if(mPass.isEmpty()){
                    password.setError("Password is required.");
                    return;
                }
                if(mPass.length() < 6){
                    password.setError("Password should have a minimum of 6 characters.");
                    return;
                }
                mProgess.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Log.w("createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Sign up failed.",Toast.LENGTH_SHORT).show();
                        }
                        mProgess.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}