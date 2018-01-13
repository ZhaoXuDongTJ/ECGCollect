package ecg.ecg_collect.LiteSQL.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ecg.ecg_collect.R;

/**
 * Created by 92198 on 2017/12/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UIuserList> mUIuserList;

    public UserAdapter(List<UIuserList> uIuserLists) {
        mUIuserList = uIuserLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UIuserList uIuserList = mUIuserList.get(position);
        holder.userName.setText(uIuserList.getName());
    }

    @Override
    public int getItemCount() {
        return mUIuserList.size();
    }
// 内部类
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
        }
    }
}
