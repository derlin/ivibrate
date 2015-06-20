package ch.derlin.ivibrate.main.frag.listconv;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;

import java.util.List;

/**
 * Created by lucy on 19/06/15.
 */
public class ListConvAdapter extends BaseAdapter{
    private List<Friend> mList;
    private Activity context;

    public ListConvAdapter( Activity context, List<Friend> mList ){
        this.mList = mList;
        this.context = context;
    }


    public void add(Friend f){
        mList.add( f );
        notifyDataSetChanged();
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


    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = context.getLayoutInflater().inflate( android.R.layout.simple_list_item_2, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.title = ( TextView ) convertView.findViewById( android.R.id.text1 );
            viewHolder.text = ( TextView ) convertView.findViewById( android.R.id.text2 );
            convertView.setTag( viewHolder );
        }else{
            viewHolder = ( ViewHolder ) convertView.getTag();
        }

        Friend f = mList.get( position );
        LocalContactDetails details = f.getDetails();
        viewHolder.title.setText( details == null ? f.getPhone() : f.getDetails().getName());
        viewHolder.text.setText( f.getMessagesCount() + " messages.");

        return convertView;
    }

    // ----------------------------------------------------

    protected static class ViewHolder{
        TextView title, text;
    }
}
