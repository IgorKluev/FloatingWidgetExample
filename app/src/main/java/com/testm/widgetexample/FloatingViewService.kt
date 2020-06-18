package com.testm.widgetexample

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class FloatingViewService : Service()
{
    private val IMEI_SERVICE_CHANNEL_ID = "channel_imei_service"
    private val CHANNEL_ID = "TESTS_REMINDER_CHANNEL_ID"

    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        val layout_parms: Int
        layout_parms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        //Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layout_parms,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the view position
        params.gravity =
            Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager?.addView(mFloatingView, params)

        //The root element of the collapsed view layout
        val collapsedView = mFloatingView!!.findViewById<View>(R.id.collapse_view)
        //The root element of the expanded view layout
//        val expandedView = mFloatingView!!.findViewById<View>(R.id.expanded_container)


//        //Set the close button
//        val closeButtonCollapsed =
//            mFloatingView!!.findViewById<View>(R.id.close_btn) as ImageView
//        closeButtonCollapsed.setOnClickListener {
//            //close the service and remove the from from the window
//            //                stopSelf();
//        }

//        //Set the view while floating view is expanded.
//        //Set the play button.
//        val playButton =
//            mFloatingView!!.findViewById<View>(R.id.play_btn) as ImageView
//        playButton.setOnClickListener {
//            Toast.makeText(this@FloatingViewService, "Playing the song.", Toast.LENGTH_LONG)
//                .show()
//        }

        //Set the next button.
//        val nextButton =
//            mFloatingView!!.findViewById<View>(R.id.next_btn) as ImageView
//        nextButton.setOnClickListener {
//            Toast.makeText(this@FloatingViewService, "Playing next song.", Toast.LENGTH_LONG)
//                .show()
//        }

//        //Set the pause button.
//        val prevButton =
//            mFloatingView!!.findViewById<View>(R.id.prev_btn) as ImageView
//        prevButton.setOnClickListener {
//            Toast.makeText(
//                this@FloatingViewService,
//                "Playing previous song.",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//
//        //Set the close button
//        val closeButton =
//            mFloatingView!!.findViewById<View>(R.id.close_button) as ImageView
//        closeButton.setOnClickListener {
//            //                collapsedView.setVisibility(View.VISIBLE);
//            //                expandedView.setVisibility(View.GONE);
//        }

        //Open the application on thi button click
//        val openButton =
//            mFloatingView!!.findViewById<View>(R.id.open_button) as ImageView
//        openButton.setOnClickListener {
//            //Open the application  click.
//            //                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
//            //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //                startActivity(intent);
//            //
//            //                //close the service and remove view from the view hierarchy
//            //                stopSelf();
//        }

        //Drag and move floating view using user's touch action.
        mFloatingView!!.findViewById<View>(R.id.root_container)
            .setOnTouchListener(object : OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            //remember the initial position.
                            initialX = params.x
                            initialY = params.y

                            //get the touch location
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            val Xdiff = (event.rawX - initialTouchX).toInt()
                            val Ydiff = (event.rawY - initialTouchY).toInt()

                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 10 && Ydiff < 10) {
                                if (isViewCollapsed()) {
                                    //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
//                                collapsedView.setVisibility(View.GONE);
//                                expandedView.setVisibility(View.VISIBLE);
                                    val intent =
                                        Intent(this@FloatingViewService, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)

                                    //close the service and remove view from the view hierarchy
                                    stopSelf()
                                }
                            }
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()

                            //Update the layout with new X & Y coordinate
                            mWindowManager?.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val channelId: String = fetchServiceId()!!
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Example Service")
            .setContentText("test")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

//        stopSelf();
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    private fun fetchServiceId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                "my_service", IMEI_SERVICE_CHANNEL_ID
            )
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private fun isViewCollapsed(): Boolean {
        return mFloatingView == null || mFloatingView!!.findViewById<View>(R.id.collapse_view)
            .visibility == View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager!!.removeView(mFloatingView)
    }
}