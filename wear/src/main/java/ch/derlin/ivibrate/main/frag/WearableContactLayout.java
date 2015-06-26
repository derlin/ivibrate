package ch.derlin.ivibrate.main.frag;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.derlin.ivibrate.R;

/**
 * Created by lucy on 24/06/15.
 */
public class WearableContactLayout extends LinearLayout implements WearableListView.OnCenterProximityListener{

    private ImageView mCircle;
    private TextView mName;

    private final float mFadedTextAlpha;
    private final int mFadedCircleColor;
    private final int mChosenCircleColor;


    public WearableContactLayout( Context context ){
        this( context, null );
    }


    public WearableContactLayout( Context context, AttributeSet attrs ){
        this( context, attrs, 0 );
    }


    public WearableContactLayout( Context context, AttributeSet attrs, int defStyle ){
        super( context, attrs, defStyle );

        mFadedTextAlpha = 40 / 100f;
        mFadedCircleColor = getResources().getColor( R.color.white );
        mChosenCircleColor = getResources().getColor( R.color.my_red );
    }


    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        mCircle = ( ImageView ) findViewById( R.id.circle );
        mName = ( TextView ) findViewById( R.id.name );
    }


    @Override
    public void onCenterPosition( boolean animate ){
        mName.setAlpha( 1f );
        ( ( GradientDrawable ) mCircle.getDrawable() ).setColor( mChosenCircleColor );
    }


    @Override
    public void onNonCenterPosition( boolean animate ){
        ( ( GradientDrawable ) mCircle.getDrawable() ).setColor( mFadedCircleColor );
        mName.setAlpha( mFadedTextAlpha );
    }
}
