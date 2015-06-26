package ch.derlin.ivibrate.main.frag.listconv;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Adapter for the list of contacts in the {@link ListConversationsFragment}.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class ListConvAdapter extends BaseAdapter{
    private List<Friend> mList;
    private Activity context;


    public ListConvAdapter( Activity context, List<Friend> mList ){
        this.mList = mList;
        this.context = context;
    }

    // ----------------------------------------------------


    public void add( Friend f ){
        mList.add( f );
        notifyDataSetChanged();
    }


    public void remove( Friend friend ){
        int position = mList.indexOf( friend );
        if( position >= 0 ){
            mList.remove( position );
            notifyDataSetChanged();
        }
    }



    public void setFriends( Collection<Friend> friends ){
        if( mList == null ){
            mList = new ArrayList<>();

        }else{
            mList.clear();
        }

        this.mList.addAll( friends );
        notifyDataSetChanged();
    }

    // ----------------------------------------------------


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

    // ----------------------------------------------------


    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        ViewHolder viewHolder;

        if( convertView == null ){
            convertView = context.getLayoutInflater().inflate( R.layout.adapter_conv, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.image = ( ImageView ) convertView.findViewById( R.id.image );
            viewHolder.title = ( TextView ) convertView.findViewById( R.id.title );
            viewHolder.text = ( TextView ) convertView.findViewById( R.id.text );
            convertView.setTag( viewHolder );
        }else{
            viewHolder = ( ViewHolder ) convertView.getTag();
        }

        Friend f = mList.get( position );
        LocalContactDetails details = f.getDetails();
        viewHolder.title.setText( f.getDisplayName() );
        if( details != null && details.getPhotoUri() != null ){
            viewHolder.image.setImageURI( f.getDetails().getPhotoUri() );
        }else{
            viewHolder.image.setImageResource( R.drawable.qm_face );
        }

        long count = f.getMessagesCount();
        viewHolder.text.setText( String.format( "%d message%s.", count, count > 1 ? "s" : "" ) );

        return convertView;
    }



    // ----------------------------------------------------

    protected static class ViewHolder{
        ImageView image;
        TextView title, text;
    }
}
