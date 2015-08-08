package br.com.sumone.sumonetwitter.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.sumone.sumonetwitter.utils.ImageUtils;

/**
 * Created by Tiago on 07/08/2015.
 *
 * Permite a biblioteca usar Picasso criar uma imagem
 * do usuario como um marker do maps
 *
 * @see com.squareup.picasso.Target
 * @see MarkerOptions
 * @see Picasso
 */
public class PicassoMarker implements Target {

    private MarkerOptions mMarker;

    private OnImageLoaded mOnImageLoaded;

    public PicassoMarker(MarkerOptions marker) {
        mMarker = marker;
    }

    public void setOnImageLoaded(OnImageLoaded onImageLoaded) {
        this.mOnImageLoaded = onImageLoaded;
    }

    @Override
    public int hashCode() {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            MarkerOptions marker = ((PicassoMarker) o).mMarker;
            return mMarker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mMarker.icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.getCircleBitmap(bitmap, 32)));
        mOnImageLoaded.imageLoaded(mMarker);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public interface OnImageLoaded {
        void imageLoaded(MarkerOptions markerOptions);
    }
}