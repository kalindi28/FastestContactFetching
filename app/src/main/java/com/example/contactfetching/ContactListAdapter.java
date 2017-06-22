package com.example.contactfetching;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.contactfetching.DataClass.CommonDataClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 6/22/2017.
 */

public class ContactListAdapter extends BaseAdapter {
    ArrayList<CommonDataClass> mArrList;
    Context mContext;
    MyViewHolder myViewHolder;
    public class MyViewHolder {
        ImageView mImgProfileContact;
        TextView mTvNameContact, mTvNumberContact, mTvContactEmail;
    }
    public ContactListAdapter(ArrayList<CommonDataClass> verifiedContactList, Context mContext) {
        this.mArrList = verifiedContactList;
        this.mContext=mContext;
    }


    @Override
    public int getCount() {
        return mArrList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        myViewHolder=new MyViewHolder();
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sgl_list_item, null);
            myViewHolder.mImgProfileContact = (ImageView) convertView.findViewById(R.id.imgProfileContact);
            myViewHolder.mTvNameContact = (TextView) convertView.findViewById(R.id.tvNameContact);
            myViewHolder.mTvNumberContact = (TextView) convertView.findViewById(R.id.tvNumberContact);
            myViewHolder.mTvContactEmail = (TextView) convertView.findViewById(R.id.tvContactEmail);

            convertView.setTag(myViewHolder);
        }else{
            myViewHolder= (MyViewHolder) convertView.getTag();
        }

        myViewHolder.mImgProfileContact.setTag(""+position);
        myViewHolder.mTvNameContact.setText(mArrList.get(position).getStrContactName());
        if(!mArrList.get(position).getStrContactNumber().equalsIgnoreCase("")) {
            myViewHolder.mTvNumberContact.setText(mArrList.get(position).getStrContactNumber());
        }else{
            myViewHolder.mTvNumberContact.setText("N/A");
        }
        if (!mArrList.get(position).getStrContactEmail().toString().equals("")) {
            myViewHolder.mTvContactEmail.setText(mArrList.get(position).getStrContactEmail());
        } else if(mArrList.get(position).getStrContactEmail().toString().equals("")){
            myViewHolder.mTvContactEmail.setText("Email does not exist");
        }


            if(mArrList.get(position).getStrContactImage().contains(mArrList.get(position).getStrContactId())){
                Uri uri = Uri.parse(mArrList.get(position).getStrContactImage());
                //   Bitmap bitmap = Contacts.People.loadContactPhoto(mContext,uri, R.drawable.ic_boy, null);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(mContext.getContentResolver(),
                                    Uri.parse(mArrList.get(position).getStrContactImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // set it here in the ImageView
                myViewHolder.mImgProfileContact.setImageBitmap(bitmap);
            }else {
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                        R.mipmap.ic_launcher);
                myViewHolder.mImgProfileContact.setImageBitmap(icon);
            }


        return convertView;
    }
}
