package br.com.sumone.sumonetwitter.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Utilitario para imagens
 */
public class ImageUtils {

	public static void setImageIntoFromURL(final ImageView imageView, String url) {
		setImageIntoFromURL(imageView, url, true);
	}

	public static void setImageIntoFromURL(final ImageView imageView, String url, boolean rounded) {
		final Target target = new Target() {

			@Override
			public void onPrepareLoad(Drawable arg0) {
			}

			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
				roundedImage(imageView, bitmap);
			}

			@Override
			public void onBitmapFailed(Drawable arg0) {
			}
		};

		imageView.setTag(target);

		Picasso.with(imageView.getContext()).load(url).into(target);
	}

	public static void roundedImage(ImageView picView, Bitmap thePic) {
		picView.setImageBitmap(getCircleBitmap(thePic, 32));
	}

	/**
	 *  Corta um bitmap em formato circular
	 *
	 * @param bitmap bitmap
	 * @param pixels pixels da imagem
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
