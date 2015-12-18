package mhthakka.ncsu.edu.gestureovermqtt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import edu.ncsu.mhthakka.mqtt.messages.GestureTouch;

/**
 * Created by Meet on 12/9/2015.
 */
public class GestureRecognitionView extends View {


    MqttClient client;


    SparseArray<GestureTouch> touches = new SparseArray<>();

    int touchSEQNo = 0;
    int moveNumber = 0;
    private Paint defaultPaint = new Paint();

    public GestureRecognitionView(Context context, AttributeSet attrs) throws MqttException, InterruptedException {
        super(context, attrs);
            client = new MqttClient("tcp://192.168.0.106:1883","app",new MemoryPersistence());

    client.connect();
        byte[] bytes = "test".getBytes();
        client.publish("touches",bytes,1,true);

        Thread.sleep(1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        final int action = event.getAction();

    try {
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {
                // first pressed gesture has started
                onNewTouch(event);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {

                onNewTouch(event);
                break;
            }


            case MotionEvent.ACTION_UP: {

                onTouchReleased(event);
                break;
            }


            case MotionEvent.ACTION_POINTER_UP: {
                try {
                    onTouchReleased(event);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                event.getActionIndex();
                moveNumber++;
                try {
                    onMoveEvent(event);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                break;
            }


        }


    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

        // trigger redraw on UI thread
        this.postInvalidate();

        return true;
    }

    private void onMoveEvent(MotionEvent event) throws MqttException {

        //send infor only of first touch.
        if(event.getActionIndex() ==0)
        {

            GestureTouch g = new GestureTouch();
            g.x = event.getX();
            g.y = event.getY();
            g.milliseconds = event.getEventTime();
            g.touches = touches.size();
            g.gestureNumber = touches.get(event.getActionIndex()).gestureNumber;

           sendEvent(g);
        }






    }


    /**
     * add the touch Id in sparse array . will use gesture number for future reference.
     *
     * @param event
     */

    private void onNewTouch(MotionEvent event) throws MqttException {

        GestureTouch g= new GestureTouch();
        g.gestureNumber =   touchSEQNo++;
        g.x = event.getX();
        g.y = event.getY();
        g.touchNumber = event.getActionIndex();
        g.milliseconds = event.getEventTime();
        touches.setValueAt(event.getActionIndex(), g);


        if(g.touchNumber == 0)
        {
            sendEvent(g);
         //send touch...
        }
    }


    private void onTouchReleased(MotionEvent event) throws MqttException {
        touches.remove(event.getActionIndex());


        GestureTouch g= new GestureTouch();

        g.gestureNumber = touches.get(event.getActionIndex()).gestureNumber;
        g.touchNumber = event.getActionIndex();
        g.milliseconds = event.getEventTime();
        g.x = -1;
        g.y = -1;
        touches.setValueAt(g.touchNumber, g);



        if(g.touchNumber == 0)
        {
            sendEvent(g);
        }





    }



    public void sendEvent(GestureTouch g) throws MqttException {
        byte[] bytes;

        bytes = g.toString().getBytes();
        client.publish("touches",bytes,1,false);






    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("" + touchSEQNo, 10, 10, defaultPaint);
        canvas.drawText("Moves"+ moveNumber,10,50,defaultPaint);
    }

}