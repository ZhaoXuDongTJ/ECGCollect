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

import ecg.ecg_collect.LiteSQL.userLite;
import ecg.ecg_collect.R;

public class userListActivity extends AppCompatActivity {

    private Button recycler_userBtn;
    private List<UIuserList> uIuserLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        recycler_userBtn = findViewById(R.id.recycler_userBtn);
        recycler_userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initUser();
        RecyclerView recyclerView = findViewById(R.id.recycler_user_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        UserAdapter adapter = new UserAdapter(uIuserLists);
        recyclerView.setAdapter(adapter);
    }
    private void initUser(){
        List<userLite> userLites = DataSupport.findAll(userLite.class);
        for(userLite us:userLites){
            UIuserList ui = new UIuserList(us.getUserName());
            uIuserLists.add(ui);
        }

    }
}
