package ch.derlin.ivibrate.main.frag;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.main.Friend;

import java.util.List;

/**
 * Created by lucy on 24/06/15.
 */
public class ContactsAdapter extends WearableListView.Adapter{
    private List<Friend> mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactsAdapter( Context context, List<Friend> contacts ){
        mContext = context;
        mInflater = LayoutInflater.from( context );
        mDataset = contacts;
    }


    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder{
        private TextView textView;


        public ItemViewHolder( View itemView ){
            super( itemView );
            // find the text view within the custom item's layout
            textView = ( TextView ) itemView.findViewById( R.id.name );
        }
    }

    // ----------------------------------------------------

    public String getPhone(int position){
        return mDataset.get( position ).getPhone();
    }
    // ----------------------------------------------------

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        // Inflate our custom layout for list items
        return new ItemViewHolder( mInflater.inflate( R.layout.list_contact_row, null ) );
    }


    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder( WearableListView.ViewHolder holder, int position ){
        // retrieve the text view
        ItemViewHolder itemHolder = ( ItemViewHolder ) holder;
        TextView view = itemHolder.textView;
        // replace text contents
        view.setText( mDataset.get( position ).getName() );
        // replace list item's metadata
        holder.itemView.setTag( position );
    }


    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
