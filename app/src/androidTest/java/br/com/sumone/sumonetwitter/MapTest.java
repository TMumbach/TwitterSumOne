package br.com.sumone.sumonetwitter;

import android.content.Context;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import java.lang.reflect.Method;

import br.com.sumone.sumonetwitter.map.TweetMapFragment;

/**
 * Created by Tiago on 08/08/2015.
 */
public class MapTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void locationByAddress() {
        LatLng locationFromAddress = TweetMapFragment.getLocationFromAddress(getTestContext(), "São José, Santa catarina - Brasil");
        assertNotNull(locationFromAddress);

        locationFromAddress = TweetMapFragment.getLocationFromAddress(getTestContext(), "Florianópolis, Santa catarina - Brasil");
        assertNotNull(locationFromAddress);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private Context getTestContext()
    {
        try
        {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }
}
