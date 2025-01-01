package com.example.slearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

public class WelcomeActivity extends AppCompatActivity {

    TextView mainRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        mainRedirectText = findViewById(R.id.btnWelcome);

        mainRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CLWelcome), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
//
//        Button btn1 = view.findViewById(R.id.btnWelcome);
//
//        View.OnClickListener DLMain = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.DLMain);
//            }
//        };
//        btn1.setOnClickListener(DLMain);
//
//    }

}