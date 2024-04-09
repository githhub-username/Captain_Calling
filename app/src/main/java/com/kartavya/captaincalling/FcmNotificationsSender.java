package com.kartavya.captaincalling;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;


    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAAH1ySqsI:APA91bHwAaq9blunkcGY6h22h49pZY6-N_LuYxJMFoAYhGci6_XFoNCX7EvewaY0jTG9K7yyUpdAv7hKNTf20KZK_eWlyArOSwmb7BNFW01HgUYX0SSWSFsm7gzB7WdHXjVkRgZArvxq";
    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);

        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);

            JSONObject notiObject = new JSONObject();

            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", "logo_main"); // enter icon that exists in drawable only


            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    handleResponse(response);

                    /*



                    if(response != null){

                        // code run is got response
                        try {

                            if(response != null){

                                boolean success = response.getBoolean("success");

                                if (success) {
                                    // FCM notification sent successfully
                                    // Handle success, update UI, etc.
                                    Log.d("FCM_NOTIFICATION", "Notification sent successfully");
                                } else {
                                    // FCM notification sending failed
                                    // Handle failure, show error message, etc.
                                    Log.d("FCM_NOTIFICATION", "Failed to send notification");
                                }

                            }else{

                                Log.e("FCM_NOTIFICATION", "Response does not contain 'success' field");


                            }
                            // Assuming the server responds with a JSON object

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Log.e("FCM_NOTIFICATION", "Error parsing JSON response");
                        }


                    }else{

                        Log.e("FCM_NOTIFICATION", "Received null response from the server");

                    } */



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error
                    //  Log.e("FCM_NOTIFICATION", "Error sending FCM notification: " + error.getMessage(), error);

                    handleError(error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    /*


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;

                     */

                    return createHeaders();


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("FCM_NOTIFICATION", "Error creating JSON request: " + e.getMessage());

        }

    }

    private void handleResponse(JSONObject response) {
        if (response != null) {
            Log.d("FCM_NOTIFICATION", "Response: " + response.toString());
            try {
                // Check if the response indicates success (modify this based on your server response)
                boolean success = isResponseSuccessful(response);

                if (success) {
                    // FCM notification sent successfully
                    Log.d("FCM_NOTIFICATION", "Notification sent successfully");
                } else {
                    // FCM notification sending failed
                    Log.d("FCM_NOTIFICATION", "Failed to send notification");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("FCM_NOTIFICATION", "Error parsing JSON response");
            }
        } else {
            Log.e("FCM_NOTIFICATION", "Received null response from the server");
        }
    }

    private boolean isResponseSuccessful(JSONObject response) throws JSONException {
        // Modify this based on the actual content of your server response
        // For example, check for the presence of a message_id
        return response.has("message_id");
    }




    private void handleError(VolleyError error) {
        Log.e("FCM_NOTIFICATION", "Error sending FCM notification: " + error.getMessage(), error);
    }

    private Map<String, String> createHeaders() {
        Map<String, String> header = new HashMap<>();
        header.put("content-type", "application/json");
        header.put("authorization", "key=" + fcmServerKey);
        return header;
    }

}
