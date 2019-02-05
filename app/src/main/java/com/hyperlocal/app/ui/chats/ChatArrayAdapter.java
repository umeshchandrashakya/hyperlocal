package com.hyperlocal.app.ui.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.hyperlocal.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author ${Umesh} on 27-10-2017.
 */

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView timeTextView;
    private TextView userNameTextView;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right_row, parent, false);
            timeTextView = (TextView) row.findViewById(R.id.time);
            userNameTextView = (TextView) row.findViewById(R.id.userName);
        }else{
            row = inflater.inflate(R.layout.left_row, parent, false);
            timeTextView = (TextView) row.findViewById(R.id.time);
            userNameTextView = (TextView) row.findViewById(R.id.userName);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.message);
        String time = convertTimeStamp(Long.parseLong(chatMessageObj.getTime()));
        timeTextView.setText(time);
        userNameTextView.setText(getFirstWordFromString(chatMessageObj.userName));
        return row;
    }

    public String getFirstWordFromString(String word){
        String firstWord = word;
        if(firstWord.contains(" ")){
            firstWord= firstWord.substring(0, firstWord.indexOf(" "));
        }
        return firstWord;
    }

    public String convertDate(String strDate){
        String dateStr="";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = df.parse(strDate);
            DateFormat currentDateFormat = new SimpleDateFormat("d MMM yyyy, h:mm:ss a");
            //currentDateFormat.setTimeZone(TimeZone.getDefault());
            currentDateFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
            dateStr = currentDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public String getCurrentTimeZone(){
        TimeZone tz = Calendar.getInstance().getTimeZone();
        System.out.println(tz.getDisplayName());
        return tz.getID();
    }

    public String convertTimeStamp(long timeStamp) {
        Date currentDate = new Date(timeStamp);
        System.out.println("current Date: " + currentDate);
        DateFormat df = new SimpleDateFormat("HH:mm a");
        System.out.println("Milliseconds to Date: " + df.format(currentDate));
        return df.format(currentDate);
    }

}