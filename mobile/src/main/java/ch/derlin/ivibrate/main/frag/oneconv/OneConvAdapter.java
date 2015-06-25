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
  * Adapter used to display messages from one conversation.
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class OneConvAdapter extends BaseAdapter{

    List<Message> mList;
    Activity mActivity;


    public OneConvAdapter( Activity activity, List<Message> list ){
        mList = list;
        mActivity = activity;
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

    public void add( Message message ){
        mList.add( message );
        notifyDataSetChanged();
    }



     public void addAll( List<Message> messages ){
         this.mList = messages;
         notifyDataSetChanged();
     }


     public void remove( Message message ){
        if( mList.contains( message ) ){
            mList.remove( message );
            notifyDataSetChanged();
        }
    }


     public void setAcked( Long messageId ){
         if( messageId == null ) return;

         for( Message message : mList ){
             if( messageId.equals( message.getId() ) ){
                 message.setIsAcked( true );
                 notifyDataSetChanged();
             }
         }//end for
     }

     // ----------------------------------------------------

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        ViewHolder viewHolder;

        if( convertView == null ){
            convertView = mActivity.getLayoutInflater().inflate( R.layout.adapter_conv, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.title = ( TextView ) convertView.findViewById( R.id.title );
            viewHolder.text = ( TextView ) convertView.findViewById( R.id.text );
            viewHolder.image = ( ImageView ) convertView.findViewById( R.id.image );
            viewHolder.ackImage = ( ImageView ) convertView.findViewById( R.id.icon_ack );
            convertView.setTag( viewHolder );

        }else{
            viewHolder = ( ViewHolder ) convertView.getTag();
        }

        Message m = mList.get( position );
        viewHolder.image.setImageResource( m.getDir().equals( Message.SENT_MSG ) ? R.drawable.arrow_sent : R
                .drawable.arrow_received );
        viewHolder.title.setText( m.getDate() );
        viewHolder.text.setText( m.getText() == null ? m.getPattern() : m.getText() );
        viewHolder.ackImage.setImageResource( m.getIsAcked() ? R.drawable.check_checked : R.drawable.check_unchecked );
        viewHolder.ackImage.setVisibility( m.getDir().equals( Message.SENT_MSG ) ? View.VISIBLE : View.INVISIBLE );

        return convertView;
    }

    // ----------------------------------------------------

    protected static class ViewHolder{
        TextView title, text;
        ImageView image, ackImage;
    }
}
