package ch.derlin.ivibrate.main.frag.oneconv;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.sql.entities.Message;

import java.util.List;

/**
 * Created by lucy on 20/06/15.
 */
public class OneConvAdapter extends BaseAdapter{

    List<Message> mList;
    Activity mActivity;

    public OneConvAdapter(Activity activity, List<Message> list){
        mList = list;
        mActivity = activity;
    }

    @Override
    public int getCount(){
        return mList.size();
    }


    @Override
    public Object getItem( int position ){
        return mList.get( position );
    }


    @Override
    public long getItemId( int position ){
        return position;
    }

    public void add(Message message){
        mList.add( message );
        notifyDataSetChanged();
    }

    public void remove(Message message){
        if(mList.contains( message )){
            mList.remove( message );
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        ViewHolder viewHolder;

        if( convertView == null ){
            convertView = mActivity.getLayoutInflater().inflate( R.layout.adapter_conv, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.title = ( TextView ) convertView.findViewById( R.id.title );
            viewHolder.text = ( TextView ) convertView.findViewById( R.id.text );
            viewHolder.image = ( ImageView ) convertView.findViewById( R.id.image );
            convertView.setTag( viewHolder );

        }else{
            viewHolder = ( ViewHolder ) convertView.getTag();
        }

        Message m = mList.get( position );
        viewHolder.image.setImageResource( m.getDir().equals( Message.SENT_MSG ) ? R.drawable.arrow_sent_pad : R.drawable
                .arrow_received_pad );
        viewHolder.title.setText( m.getDate() );
        viewHolder.text.setText( m.getPattern() );

        return convertView;
    }

    // ----------------------------------------------------

    protected static class ViewHolder{
        TextView title, text;
        ImageView image;
    }
}
