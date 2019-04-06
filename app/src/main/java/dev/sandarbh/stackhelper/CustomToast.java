package dev.sandarbh.stackhelper;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    private Toast infoToast;
    private TextView toastMsg;
    private View toastView;

    CustomToast(Context context){
        infoToast = new Toast(context);
        infoToast.setDuration(Toast.LENGTH_SHORT);

        LayoutInflater inflater = LayoutInflater.from(context);
        toastView = inflater.inflate(R.layout.custom_toast,null);
        toastMsg = toastView.findViewById(R.id.toastMsg);
        infoToast.setView(toastView);
    }

    public void showToast(String Message){

        toastMsg.setText(Message);
        infoToast.show();
    }

    public void setToastPosition(int gravity,int X,int Y){

        infoToast.setGravity(gravity,X,Y);
    }

    public boolean isVisible(){
        return infoToast.getView().isShown();

    }

    public void setToastVisibility(int visibility){
        infoToast.getView().setVisibility(visibility);

    }

    public void logDetails(){
        Rect rect = infoToast.getView().getClipBounds();
        if (rect!=null)
            Log.e("Container : ",rect.top+" "+rect.bottom+" "+rect.left+" "+rect.right+" ");
    }
}
