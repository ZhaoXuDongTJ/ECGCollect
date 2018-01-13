package ecg.ecg_collect.LiteSQL.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ecg.ecg_collect.R;


/**
 * Created by 92198 on 2017/12/24.
 */

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {

    private List<UIsignList> mUIsignList;

    public SignAdapter(List<UIsignList> mUIsignList) {
        this.mUIsignList = mUIsignList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UIsignList uIsignList = mUIsignList.get(position);
        holder.sign_data.setText(uIsignList.getTime().toString());
        holder.sign_name.setText(uIsignList.getName());
    }

    @Override
    public int getItemCount() {
        return mUIsignList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView sign_name;
        TextView sign_data;

        public ViewHolder(View itemView) {
            super(itemView);
            sign_name = itemView.findViewById(R.id.sign_name);
            sign_data = itemView.findViewById(R.id.sign_data);
        }
    }
}
