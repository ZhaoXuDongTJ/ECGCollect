package ecg.ecg_collect.LiteSQL.list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import ecg.ecg_collect.LiteSQL.signListLite;
import ecg.ecg_collect.R;

public class signListActivity extends AppCompatActivity {

    private Button recycler_userBtn;
    private ArrayList<UIsignList> uIsignLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list);
        recycler_userBtn = findViewById(R.id.recycler_userBtn);
        recycler_userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initUser();
        RecyclerView recyclerView = findViewById(R.id.recycler_sign_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        SignAdapter adapter = new SignAdapter(uIsignLists);
        recyclerView.setAdapter(adapter);
    }
    private void initUser(){
        List<signListLite> userLites = DataSupport.findAll(signListLite.class);
        for(signListLite us:userLites){
            UIsignList ui = new UIsignList(us.getTime(),us.getName());
            uIsignLists.add(ui);
        }

    }
}
