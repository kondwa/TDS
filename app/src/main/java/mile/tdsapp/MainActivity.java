package mile.tdsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button btnSignIn;
    private Button btnSignUp;

    // Firebase..
    private FirebaseAuth mAuth;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mProgress = findViewById(R.id.signin_progress);
        mProgress.setVisibility(View.INVISIBLE);

        email = findViewById(R.id.email_signin);
        password = findViewById(R.id.password_signin);
        btnSignIn = findViewById(R.id.btnsignin);
        btnSignUp = findViewById(R.id.btnsignup);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();
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
                mProgress.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }else{
                            Log.w("signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Sign in failed.",Toast.LENGTH_SHORT).show();
                        }
                        mProgress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });
    }
}