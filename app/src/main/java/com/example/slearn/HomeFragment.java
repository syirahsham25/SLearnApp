package com.example.slearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_home, container, false);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        Button btn1 = view.findViewById(R.id.BtnSelfRev);
        Button btn2 = view.findViewById(R.id.BtnStudyPlanner);

        View.OnClickListener CLSelfRevision = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestSelfRevision);
            }
        };
        btn1.setOnClickListener(CLSelfRevision);

        View.OnClickListener CLStudyPlanner = new View.OnClickListener(){
            public void onClick (View v){
                Navigation.findNavController(view).navigate(R.id.DestStudyPlanner);
            }
        };
        btn2.setOnClickListener(CLStudyPlanner);

    }




//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
