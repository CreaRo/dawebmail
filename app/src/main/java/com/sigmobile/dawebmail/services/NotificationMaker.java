package com.sigmobile.dawebmail.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.Html;

import com.sigmobile.dawebmail.LoginActivity;
import com.sigmobile.dawebmail.R;
import com.sigmobile.dawebmail.database.EmailMessage;
import com.sigmobile.dawebmail.database.User;
import com.sigmobile.dawebmail.database.CurrentUser;
import com.sigmobile.dawebmail.utils.Settings;

import java.util.ArrayList;

/**
 * Created by rish on 6/10/15.
 */
public class NotificationMaker {

    public static void showNotification(Context context, User user, String fromName, String subject) {
        Settings settings = new Settings(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.msg_notification);
        mBuilder.setTicker(context.getString(R.string.notification_ticker_new_webmail));
        mBuilder.setContentTitle(fromName + " to " + User.getUserThreeLetterName(user));
        mBuilder.setContentText(subject);
        mBuilder.setSound(Uri.parse(settings.getString(Settings.KEY_NOTIFICATION_SOUND)));
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);

        Intent notificationIntent = new Intent(context, LoginActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);

        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) (System.currentTimeMillis()), mBuilder.build());
        CurrentUser.setCurrentUser(user, context);
    }

    public static void sendInboxNotification(int numberToShow, User user, Context context, ArrayList<EmailMessage> newEmails) {
        Settings settings = new Settings(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), 0);
            Notification.Builder mBuilder = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.notification_ticker_new_webmails))
                    .setTicker(context.getString(R.string.notification_ticker_new_webmails))
                    .setContentText(context.getString(R.string.notification_swipe_to_view))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.msg_notification);

            if (User.getUsersCount() > 1) {
                mBuilder.setContentTitle(context.getString(R.string.notification_ticker_new_webmails) + " for " + User.getUserThreeLetterName(user));
            } else
                mBuilder.setContentTitle(context.getString(R.string.notification_ticker_new_webmails));

            Notification.InboxStyle notification = null;
            notification = new Notification.InboxStyle(mBuilder);

            for (int i = 0; i < numberToShow; i++) {
                String emailFrom = newEmails.get(newEmails.size() - 1 - i).getFromName();
                String emailSubject = newEmails.get(newEmails.size() - 1 - i).getSubject();
                String htmlText = "<b>" + emailFrom + "</b> " + emailSubject;
                notification.addLine(Html.fromHtml(htmlText));
            }

            Intent resultIntent = new Intent(context, LoginActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(LoginActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setSound(Uri.parse(settings.getString(Settings.KEY_NOTIFICATION_SOUND)));
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

            CurrentUser.setCurrentUser(user, context);
        }
    }

    public static void cancelNotification(Context context) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}