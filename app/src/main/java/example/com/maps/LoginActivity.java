package example.com.maps;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends AppCompatActivity {
    EditText email,pswd;
    Button loginButton;
    FirebaseAuth firebaseAuth;
    TextView signUp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            email = findViewById(R.id.login_email);
            pswd = findViewById(R.id.login_pswd);
            loginButton = findViewById(R.id.login_button);
            signUp = findViewById(R.id.login_gotoSignUp);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 20-03-2018 Verify login credentials & fix toolbar pswd 6 digit or more
                    String mail,pswdt;
                    mail=email.getText().toString();
                    pswdt=pswd.getText().toString();
                    if(pswdt.length()<6){
                        Toast.makeText(LoginActivity.this, "Password must be at least 6 digit", Toast.LENGTH_SHORT).show();
                    }
                    else

                    firebaseAuth.signInWithEmailAndPassword(mail, pswdt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Email & password doesnt match.", Toast.LENGTH_SHORT).show();
                            } else {
                                finish();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    });
                }
            });
        }
    }
    @Override
    public void onBackPressed()
    {   finish();
        super.onBackPressed();
    }
}

