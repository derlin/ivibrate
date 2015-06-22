package ch.derlin.ivibrate.main.frag.oneconv;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
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
            convertView = mActivity.getLayoutInflater().inflate( android.R.layout.simple_list_item_2, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.title = ( TextView ) convertView.findViewById( android.R.id.text1 );
            viewHolder.text = ( TextView ) convertView.findViewById( android.R.id.text2 );
            convertView.setTag( viewHolder );

        }else{
            viewHolder = ( ViewHolder ) convertView.getTag();
        }

        Message m = mList.get( position );
        viewHolder.title.setText( (m.getDir().equals( Message.SENT_MSG ) ? "-> " : "<- ") + m.getDate() );
        viewHolder.text.setText( m.getPattern() );

        return convertView;
    }

    // ----------------------------------------------------

    protected static class ViewHolder{
        TextView title, text;
    }
}
