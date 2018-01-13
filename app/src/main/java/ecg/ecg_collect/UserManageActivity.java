package ecg.ecg_collect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;

import ecg.ecg_collect.LiteSQL.list.signListActivity;
import ecg.ecg_collect.LiteSQL.list.userListActivity;
import ecg.ecg_collect.LiteSQL.signListLite;
import ecg.ecg_collect.LiteSQL.userLite;

public class UserManageActivity extends AppCompatActivity {

    private Button ManageReturnBtn;
    private TextView start_user_list;
    private TextView start_sign_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        ManageReturnBtn = findViewById(R.id.ManageReturnBtn);
        ManageReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        start_user_list = findViewById(R.id.start_user_list);
        List<userLite> userLites = DataSupport.findAll(userLite.class);
        start_user_list.setText(start_user_list.getText()+"一共"+userLites.size()+"个用户");
        start_user_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserManageActivity.this,userListActivity.class));
            }
        });
        start_sign_list = findViewById(R.id.start_sign_list);
        List<signListLite> signListLite = DataSupport.findAll(signListLite.class);
        start_sign_list.setText(start_sign_list.getText()+"一共"+signListLite.size()+"条记录");
        start_sign_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserManageActivity.this,signListActivity.class));
            }
        });
    }
}
