package example.com.maps;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class UserRecycleAdapter extends RecyclerView.Adapter<UserRecycleAdapter.UserRecycleViewHolder>
{
    List<LocationDetails> list = new ArrayList<>();
    List<Bitmap> bitmapList=new ArrayList<>();

    public UserRecycleAdapter(List<LocationDetails> list) {
        this.list = list;
    }

    @Override
    public UserRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card_view, parent, false);
        return new UserRecycleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserRecycleViewHolder holder, int position) {
    LocationDetails locationDetails = list.get(position);
    holder.textView.setText(locationDetails.city);
    holder.textView1.setText(locationDetails.describe);
    holder.imageView.setImageBitmap(locationDetails.getImg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserRecycleViewHolder extends RecyclerView.ViewHolder{
    TextView textView,textView1;
    ImageView imageView;
    public UserRecycleViewHolder(View itemView)
    {
        super(itemView);
        textView=itemView.findViewById(R.id.user_location);
        textView1=itemView.findViewById(R.id.user_descibe);
        imageView=itemView.findViewById(R.id.user_img);
    }
}
}
