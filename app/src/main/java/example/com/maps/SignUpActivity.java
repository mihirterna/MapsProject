package example.com.maps;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText email,pswd,cnfmPswd,name;
    Button button;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.sign_email);
        pswd=findViewById(R.id.sign_pswd);
        cnfmPswd=findViewById(R.id.sign_cnfmPswd);
        name=findViewById(R.id.sign_name);
        button=findViewById(R.id.sign_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 20-03-2018 Verify incredentials ie.e pswd must be more than 6 digits
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                           databaseReference=databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                           databaseReference.child("name").setValue(name.getText().toString());
                           databaseReference.child("email").setValue(email.getText().toString());
                           startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        }
                        }
                });
            }
        });

    }
}
